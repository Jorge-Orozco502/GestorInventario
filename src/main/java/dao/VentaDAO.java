package dao;

import conexion.conexionBD;
import modelo.Venta;
import modelo.DetalleVenta;
import modelo.FormaPago;
import modelo.Cliente;
import modelo.Usuario;
import modelo.Administrador;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    //CREATE
    public boolean insertarVenta(Venta venta) {
        String sqlAsegurarConsecutivo
                = "INSERT IGNORE INTO consecutivo_venta (id, ultimo_numero) VALUES (1, 0)";

        String sqlConsecutivo
                = "UPDATE consecutivo_venta "
                + "SET ultimo_numero = LAST_INSERT_ID(ultimo_numero + 1) "
                + "WHERE id = 1";

        String sqlObtenerNumero
                = "SELECT LAST_INSERT_ID()";

        String sqlVenta
                = "INSERT INTO venta ("
                + "numero_venta, fecha, codigo_cliente, nombre_cf, direccion_cf, "
                + "id_usuario_responsable, forma_pago, subtotal_general, "
                + "total_descuentos, total_iva, total_final"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String sqlDetalle
                = "INSERT INTO detalle_venta ("
                + "numero_venta, codigo_producto, cantidad_solicitada, "
                + "precio_unitario, subtotal, iva_linea, total_linea"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?)";

        if (venta == null
                || venta.getFecha() == null
                || venta.getFormaPago() == null
                || venta.getCliente() == null
                || venta.getCliente().getCodigoCliente() == null
                || venta.getUsuarioResponsable() == null
                || venta.getUsuarioResponsable().getIdUsuario() <= 0
                || venta.getDetallesVenta() == null
                || venta.getDetallesVenta().isEmpty()) {
            System.err.println("La venta tiene datos incompletos.");
            return false;
        }

        Connection conexion = null;

        try {
            conexion = conexionBD.obtenerConexion();

            if (conexion == null) {
                System.err.println("No se pudo conectar a la base de datos.");
                return false;
            }

            conexion.setAutoCommit(false);

            try (PreparedStatement ps = conexion.prepareStatement(sqlAsegurarConsecutivo)) {
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conexion.prepareStatement(sqlConsecutivo)) {
                if (ps.executeUpdate() != 1) {
                    throw new SQLException(
                            "No existe el registro del consecutivo de ventas."
                    );
                }
            }

            long consecutivo;

            try (PreparedStatement ps
                    = conexion.prepareStatement(sqlObtenerNumero); ResultSet rs = ps.executeQuery()) {

                if (!rs.next()) {
                    throw new SQLException("No se pudo obtener el consecutivo.");
                }

                consecutivo = rs.getLong(1);
            }

            String numeroVenta = "V" + String.format("%06d", consecutivo);

            venta.setNumeroVenta(numeroVenta);

            try (PreparedStatement ps = conexion.prepareStatement(sqlVenta)) {
                ps.setString(1, numeroVenta);
                ps.setDate(2, java.sql.Date.valueOf(venta.getFecha()));
                ps.setString(3, venta.getCliente().getCodigoCliente());
                ps.setString(4, venta.getNombreCf());
                ps.setString(5, venta.getDireccionCf());
                ps.setInt(6, venta.getUsuarioResponsable().getIdUsuario());
                ps.setString(7, venta.getFormaPago().name());
                ps.setBigDecimal(8, venta.getSubtotalGeneral());
                ps.setBigDecimal(9, venta.getTotalDescuentos());
                ps.setBigDecimal(10, venta.getTotalIva());
                ps.setBigDecimal(11, venta.getTotalFinal());

                ps.executeUpdate();
            }
            try (PreparedStatement ps = conexion.prepareStatement(sqlDetalle)) {
                for (DetalleVenta detalle : venta.getDetallesVenta()) {
                    if (detalle == null
                            || detalle.getProducto() == null
                            || detalle.getProducto().getCodigo() == null
                            || detalle.getCantidadSolicitada() <= 0) {
                        throw new SQLException("Hay un detalle de venta inválido.");
                    }

                    ps.setString(1, numeroVenta);
                    ps.setString(2, detalle.getProducto().getCodigo());
                    ps.setDouble(3, detalle.getCantidadSolicitada());
                    ps.setBigDecimal(4, detalle.getPrecioUnitario());
                    ps.setBigDecimal(5, detalle.getSubtotal());
                    ps.setBigDecimal(6, detalle.getIvaLinea());
                    ps.setBigDecimal(7, detalle.getTotalLinea());

                    ps.addBatch();
                }

                ps.executeBatch();
            }

            conexion.commit();

            return true;

        } catch (SQLException | RuntimeException e) {
            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException ex) {
                    e.addSuppressed(ex);
                }
            }

            System.err.println("Error al registrar la venta: " + e.getMessage());
            return false;

        } finally {
            if (conexion != null) {
                try {
                    conexion.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println(
                            "No se pudo restaurar autoCommit: " + e.getMessage()
                    );
                }

                try {
                    conexion.close();
                } catch (SQLException e) {
                    System.err.println(
                            "No se pudo cerrar la conexión: " + e.getMessage()
                    );
                }
            }
        }
    }

    //READ
    public List<Venta> listarVentas() {
        List<Venta> listaVentas = new ArrayList<>();
        String sql = "SELECT * FROM venta";

        try (Connection conexion = conexionBD.obtenerConexion(); Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String numVenta = rs.getString("numero_venta");
                Date fechaSql = rs.getDate("fecha");
                LocalDate fecha = fechaSql != null ? fechaSql.toLocalDate() : LocalDate.now();

                BigDecimal subtotalGeneral = rs.getBigDecimal("subtotal_general");
                BigDecimal totalDescuentos = rs.getBigDecimal("total_descuentos");
                BigDecimal totalIva = rs.getBigDecimal("total_iva");
                BigDecimal totalFinal = rs.getBigDecimal("total_final");

                FormaPago formaPago = FormaPago.valueOf(rs.getString("forma_pago"));

                Cliente cliente = new Cliente();
                cliente.setCodigoCliente(rs.getString("codigo_cliente"));

                Usuario usuarioTemp = new Administrador("sistema", "hash", "Usuario Sistema");
                usuarioTemp.setIdUsuario(rs.getInt("id_usuario_responsable"));

                Venta venta = new Venta(
                        numVenta,
                        fecha,
                        formaPago,
                        subtotalGeneral,
                        totalDescuentos,
                        totalIva,
                        totalFinal,
                        cliente,
                        usuarioTemp
                );

                venta.setNombreCf(rs.getString("nombre_cf"));
                venta.setDireccionCf(rs.getString("direccion_cf"));

                listaVentas.add(venta);
                System.out.println("Venta encontrada -> No:" + numVenta + ",Fecha:" + fecha + ",Total:" + totalFinal);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar las ventas:" + e.getMessage());
        }
        return listaVentas;
    }

    //UPDATE
    public boolean actualizarVenta(Venta venta) {
        String sql = "UPDATE venta SET forma_pago = ?, subtotal_general = ?, total_descuentos = ?, total_iva = ?, total_final = ? WHERE numero_venta = ?";

        try (Connection conexion = conexionBD.obtenerConexion(); PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, venta.getFormaPago().name());
            pstmt.setBigDecimal(2, venta.getSubtotalGeneral());
            pstmt.setBigDecimal(3, venta.getTotalDescuentos());
            pstmt.setBigDecimal(4, venta.getTotalIva());
            pstmt.setBigDecimal(5, venta.getTotalFinal());
            pstmt.setString(6, venta.getNumeroVenta());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar la venta:" + e.getMessage());
            return false;
        }
    }

    //DELETE
    public boolean eliminarVenta(String numeroVenta) {
        String sqlDetalles = "DELETE FROM detalle_venta WHERE numero_venta= ?";
        String sqlVenta = "DELETE FROM venta WHERE numero_venta = ?";

        Connection conexion = null;
        try {
            conexion = conexionBD.obtenerConexion();
            if (conexion == null) {
                return false;
            }
            conexion.setAutoCommit(false);

            try (PreparedStatement pstmtDetalles = conexion.prepareStatement(sqlDetalles)) {
                pstmtDetalles.setString(1, numeroVenta);
                pstmtDetalles.executeUpdate();
            }

            try (PreparedStatement pstmtVenta = conexion.prepareStatement(sqlVenta)) {
                pstmtVenta.setString(1, numeroVenta);
                pstmtVenta.executeUpdate();
            }
            conexion.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar la venta (Rollback ejecutado):" + e.getMessage());
            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error al hacer rollback:" + ex.getMessage());
                }
            }
        } finally {
            if (conexion != null) {
                try {
                    conexion.setAutoCommit(true);
                    conexion.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar conexion:" + e.getMessage());
                }
            }
        }
        return false;
    }

    public BigDecimal obtenerTotalVentasHoy() {
        String sql = "SELECT COALESCE(SUM(total_final), 0) AS total "
                + "FROM venta WHERE fecha = ?";

        try (Connection conexion = conexionBD.obtenerConexion(); PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            pstmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener el total de ventas de hoy: " + e.getMessage());
        }
        return BigDecimal.ZERO;
    }

    public List<Object[]> listarVentasDelDiaPorUsuario(int idUsuario) {
        List<Object[]> filas = new ArrayList<>();

        String sql = "SELECT v.numero_venta, "
                + "       CASE WHEN v.nombre_cf IS NOT NULL AND v.nombre_cf <> '' "
                + "            THEN CONCAT('CF – ', v.nombre_cf) "
                + "            ELSE c.nombre END AS cliente, "
                + "       GROUP_CONCAT(p.nombre SEPARATOR ', ') AS productos, "
                + "       v.total_final "
                + "FROM venta v "
                + "JOIN cliente c ON c.codigo = v.codigo_cliente "
                + "JOIN detalle_venta d ON d.numero_venta = v.numero_venta "
                + "JOIN producto p ON p.codigo = d.codigo_producto "
                + "WHERE v.fecha = ? AND v.id_usuario_responsable = ? "
                + "GROUP BY v.numero_venta, c.nombre, v.nombre_cf, v.total_final "
                + "ORDER BY v.numero_venta DESC";

        try (Connection conexion = conexionBD.obtenerConexion(); PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            pstmt.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
            pstmt.setInt(2, idUsuario);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    filas.add(new Object[]{
                        rs.getString("numero_venta"),
                        rs.getString("cliente"),
                        rs.getString("productos"),
                        String.format("Q %,.2f", rs.getBigDecimal("total_final"))
                    });
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar ventas del día: " + e.getMessage());
        }
        return filas;
    }

}
