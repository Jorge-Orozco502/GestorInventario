package dao;

import conexion.conexionBD;
import modelo.RegistroHistorial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HistorialDAO {

    public List<RegistroHistorial> obtenerHistorial(LocalDate desde, LocalDate hasta, String tipo) {
        List<RegistroHistorial> lista = new ArrayList<>();

        boolean incluirCompras = tipo == null || tipo.equalsIgnoreCase("TODOS") || tipo.equalsIgnoreCase("COMPRA");
        boolean incluirVentas = tipo == null || tipo.equalsIgnoreCase("TODOS") || tipo.equalsIgnoreCase("VENTA");

        String sqlCompras
                = "SELECT p.codigo AS id, CAST(NULL AS DATE) AS fecha, "
                + "       'COMPRA' COLLATE utf8mb4_unicode_ci AS tipo, "
                + "       p.nombre AS referencia, (p.precio_compra * p.existencia_actual) AS total, "
                + "       CAST(NULL AS CHAR CHARACTER SET utf8mb4) COLLATE utf8mb4_unicode_ci AS usuario "
                + "FROM producto p "
                + "WHERE p.tipo_producto IN ('MATERIA_PRIMA', 'HERRAMIENTA')";

        String filtroFecha = "";
        if (desde != null) {
            filtroFecha += " AND v.fecha >= ?";
        }
        if (hasta != null) {
            filtroFecha += " AND v.fecha <= ?";
        }

        String sqlVentas
                = "SELECT v.numero_venta AS id, v.fecha AS fecha, "
                + "       'VENTA' COLLATE utf8mb4_unicode_ci AS tipo, "
                + "       c.nombre AS referencia, v.total_final AS total, u.nombre_completo AS usuario "
                + "FROM venta v "
                + "JOIN cliente c ON c.codigo = v.codigo_cliente "
                + "JOIN usuario u ON u.id_usuario = v.id_usuario_responsable "
                + "WHERE 1=1" + filtroFecha;

        String sql;
        if (incluirCompras && incluirVentas) {
            sql = sqlCompras + " UNION ALL " + sqlVentas + " ORDER BY fecha DESC, id";
        } else if (incluirCompras) {
            sql = sqlCompras + " ORDER BY id";
        } else {
            sql = sqlVentas + " ORDER BY fecha DESC, id DESC";
        }

        try (Connection conexion = conexionBD.obtenerConexion()) {
            if (conexion == null) {
                return lista;
            }

            try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
                if (incluirVentas) {
                    int idx = 1;
                    if (desde != null) {
                        pstmt.setDate(idx++, java.sql.Date.valueOf(desde));
                    }
                    if (hasta != null) {
                        pstmt.setDate(idx++, java.sql.Date.valueOf(hasta));
                    }
                }

                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        java.sql.Date f = rs.getDate("fecha");
                        lista.add(new RegistroHistorial(
                                rs.getString("id"),
                                f != null ? f.toLocalDate() : null,
                                rs.getString("tipo"),
                                rs.getString("referencia"),
                                rs.getBigDecimal("total"),
                                rs.getString("usuario")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el historial: " + e.getMessage());
        }
        return lista;
    }
}
