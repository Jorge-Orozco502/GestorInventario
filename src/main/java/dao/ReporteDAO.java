package dao;

import conexion.conexionBD;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReporteDAO {

    public List<Object[]> ventas(
            LocalDate desde,
            LocalDate hasta,
            String formaPago,
            Integer idUsuarioFiltro) throws SQLException {

        String sql = "SELECT v.numero_venta, v.fecha, c.nombre AS cliente, "
                + "v.subtotal_general, v.total_descuentos, v.total_iva, v.total_final, "
                + "(SELECT COALESCE(SUM(d.cantidad_solicitada), 0) "
                + " FROM detalle_venta d "
                + " WHERE d.numero_venta = v.numero_venta) AS cantidad "
                + "FROM venta v "
                + "INNER JOIN cliente c ON c.codigo = v.codigo_cliente "
                + "WHERE v.fecha BETWEEN ? AND ? "
                + (idUsuarioFiltro == null ? "" : "AND v.id_usuario_responsable = ? ")
                + (formaPago.equalsIgnoreCase("TODOS") ? "" : "AND v.forma_pago = ? ")
                + "ORDER BY v.fecha DESC, v.numero_venta DESC";

        List<Object[]> filas = new ArrayList<>();

        try (Connection cn = conexionBD.obtenerConexion()) {
            if (cn == null) {
                throw new SQLException("No se pudo conectar con la base de datos.");
            }

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                int indice = 1;

                ps.setDate(indice++, Date.valueOf(desde));
                ps.setDate(indice++, Date.valueOf(hasta));

                if (idUsuarioFiltro != null) {
                    ps.setInt(indice++, idUsuarioFiltro);
                }

                if (!formaPago.equalsIgnoreCase("TODOS")) {
                    ps.setString(indice, formaPago);
                }

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {
                        filas.add(new Object[]{
                            rs.getString("numero_venta"),
                            rs.getDate("fecha"),
                            rs.getString("cliente"),
                            rs.getBigDecimal("subtotal_general"),
                            rs.getBigDecimal("total_descuentos"),
                            rs.getBigDecimal("total_iva"),
                            rs.getBigDecimal("total_final"),
                            rs.getBigDecimal("cantidad")
                        });
                    }

                }
            }
        }
        return filas;
    }

    public List<Object[]> productosMasVendidos(
            LocalDate desde,
            LocalDate hasta,
            Integer idUsuarioFiltro) throws SQLException {

        String sql = "SELECT p.nombre AS producto, "
                + "SUM(d.cantidad_solicitada) AS cantidad, "
                + "SUM(d.total_linea) AS total "
                + "FROM detalle_venta d "
                + "INNER JOIN venta v ON v.numero_venta = d.numero_venta "
                + "INNER JOIN producto p ON p.codigo = d.codigo_producto "
                + "WHERE v.fecha BETWEEN ? AND ? "
                + (idUsuarioFiltro == null
                        ? ""
                        : "AND v.id_usuario_responsable = ? ")
                + "GROUP BY p.codigo, p.nombre "
                + "ORDER BY cantidad DESC, total DESC";

        List<Object[]> filas = new ArrayList<>();

        try (Connection cn = conexionBD.obtenerConexion()) {
            if (cn == null) {
                throw new SQLException("No se pudo conectar con la base de datos.");
            }

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setDate(1, Date.valueOf(desde));
                ps.setDate(2, Date.valueOf(hasta));
                if (idUsuarioFiltro != null) {
                    ps.setInt(3, idUsuarioFiltro);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        filas.add(new Object[]{
                            rs.getString("producto"),
                            rs.getBigDecimal("cantidad"),
                            rs.getBigDecimal("total")
                        });
                    }
                }
            }
        }

        return filas;
    }

    public List<Object[]> actividad(
            LocalDate desde,
            LocalDate hasta) throws SQLException {

        String sql = "SELECT m.fecha, m.tipo_movimiento, u.nombre_completo "
                + "FROM movimiento_inventario m "
                + "INNER JOIN usuario u ON u.id_usuario = m.id_usuario "
                + "WHERE m.fecha >= ? "
                + "AND m.fecha < DATE_ADD(?, INTERVAL 1 DAY) "
                + "ORDER BY m.fecha DESC";

        List<Object[]> filas = new ArrayList<>();

        try (Connection cn = conexionBD.obtenerConexion()) {
            if (cn == null) {
                throw new SQLException("No se pudo conectar con la base de datos.");
            }

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setDate(1, Date.valueOf(desde));
                ps.setDate(2, Date.valueOf(hasta));

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        filas.add(new Object[]{
                            rs.getTimestamp("fecha"),
                            rs.getString("tipo_movimiento"),
                            rs.getString("nombre_completo")
                        });
                    }
                }
            }
        }

        return filas;
    }

    public List<Object[]> historialGeneral(
            LocalDate desde,
            LocalDate hasta,
            String tipoHistorial,
            String busqueda) throws SQLException {

        String sql
                = "SELECT fecha, tipo, referencia, detalle, cantidad_total, usuario "
                + "FROM ( "
                + "    SELECT "
                + "        v.fecha AS fecha, "
                + "        'VENTA' AS tipo, "
                + "        CONCAT('Venta #', v.numero_venta) AS referencia, "
                + "        CONCAT('Cliente: ', COALESCE(c.nombre, 'Sin cliente')) AS detalle, "
                + "        CONCAT('Q ', FORMAT(v.total_final, 2)) AS cantidad_total, "
                + "        COALESCE(u.nombre_completo, u.nombre_usuario, 'Sin usuario') AS usuario "
                + "    FROM venta v "
                + "    LEFT JOIN cliente c ON c.codigo = v.codigo_cliente "
                + "    LEFT JOIN usuario u ON u.id_usuario = v.id_usuario_responsable "
                + "    WHERE v.fecha >= ? "
                + "      AND v.fecha < DATE_ADD(?, INTERVAL 1 DAY) "
                + "    UNION ALL "
                + "    SELECT "
                + "        m.fecha AS fecha, "
                + "        CONCAT('PRODUCTO: ', m.tipo_movimiento) AS tipo, "
                + "        COALESCE(NULLIF(m.referencia_documento, ''), "
                + "                 CONCAT('Movimiento #', m.id_movimiento)) AS referencia, "
                + "        CONCAT(COALESCE(p.codigo, ''), ' - ', "
                + "               COALESCE(p.nombre, 'Producto eliminado')) AS detalle, "
                + "        CONCAT(FORMAT(m.cantidad, 2), ' unidades') AS cantidad_total, "
                + "        COALESCE(u.nombre_completo, u.nombre_usuario, 'Sin usuario') AS usuario "
                + "    FROM movimiento_inventario m "
                + "    LEFT JOIN producto p ON p.codigo = m.codigo_producto "
                + "    LEFT JOIN usuario u ON u.id_usuario = m.id_usuario "
                + "    WHERE m.fecha >= ? "
                + "      AND m.fecha < DATE_ADD(?, INTERVAL 1 DAY) "
                + ") AS historial "
                + "WHERE ( "
                + "       ? = 'TODOS' "
                + "       OR (? = 'HISTORIAL DE VENTAS' AND tipo = 'VENTA') "
                + "       OR (? = 'HISTORIAL PRODUCTOS' AND tipo LIKE 'PRODUCTO:%') "
                + ") "
                + "AND ( "
                + "       ? = '' "
                + "       OR referencia LIKE ? "
                + "       OR detalle LIKE ? "
                + "       OR tipo LIKE ? "
                + "       OR usuario LIKE ? "
                + "       OR DATE_FORMAT(fecha, '%d/%m/%Y %H:%i') LIKE ? "
                + ") "
                + "ORDER BY fecha DESC";

        List<Object[]> filas = new ArrayList<>();

        try (Connection cn = conexionBD.obtenerConexion()) {
            if (cn == null) {
                throw new SQLException("No se pudo conectar con la base de datos.");
            }

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setDate(1, Date.valueOf(desde));
                ps.setDate(2, Date.valueOf(hasta));
                ps.setDate(3, Date.valueOf(desde));
                ps.setDate(4, Date.valueOf(hasta));

                ps.setString(5, tipoHistorial);
                ps.setString(6, tipoHistorial);
                ps.setString(7, tipoHistorial);

                String texto = busqueda == null ? "" : busqueda.trim();
                String patron = "%" + texto + "%";

                ps.setString(8, texto);
                ps.setString(9, patron);
                ps.setString(10, patron);
                ps.setString(11, patron);
                ps.setString(12, patron);
                ps.setString(13, patron);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        filas.add(new Object[]{
                            rs.getTimestamp("fecha"),
                            rs.getString("tipo"),
                            rs.getString("referencia"),
                            rs.getString("detalle"),
                            rs.getString("cantidad_total"),
                            rs.getString("usuario")
                        });
                    }
                }
            }
        }

        return filas;
    }
}
