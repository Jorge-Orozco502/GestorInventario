package dao;

import conexion.conexionBD;
import modelo.Venta;
import modelo.DetalleVenta;
import modelo.FormaPago;
import modelo.Cliente;
import modelo.Usuario;
import modelo.Administrador;
import modelo.Sesion;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {

    private String ultimoError;

    public String getUltimoError() {
        return ultimoError;
    }

    private static class ReglaDeNegocioException extends RuntimeException {

        ReglaDeNegocioException(String mensaje) {
            super(mensaje);
        }
    }

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

        String sqlBloquearStock
                = "SELECT nombre, existencia_actual FROM producto WHERE codigo = ? FOR UPDATE";

        String sqlDescontarStock
                = "UPDATE producto SET existencia_actual = ? WHERE codigo = ?";

        String sqlMovimiento
                = "INSERT INTO movimiento_inventario ("
                + "codigo_producto, tipo_movimiento, cantidad, existencia_anterior, "
                + "existencia_nueva, id_usuario, referencia_documento, motivo"
                + ") VALUES (?, 'SALIDA_VENTA', ?, ?, ?, ?, ?, ?)";

        ultimoError = null;

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

            for (DetalleVenta detalle : venta.getDetallesVenta()) {
                String codigo = detalle.getProducto().getCodigo();
                BigDecimal cantidad = BigDecimal.valueOf(detalle.getCantidadSolicitada());

                String nombreProducto;
                BigDecimal existenciaAnterior;

                try (PreparedStatement ps = conexion.prepareStatement(sqlBloquearStock)) {
                    ps.setString(1, codigo);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("El producto " + codigo + " no existe.");
                        }
                        nombreProducto = rs.getString("nombre");
                        existenciaAnterior = rs.getBigDecimal("existencia_actual");
                    }
                }

                if (existenciaAnterior.compareTo(cantidad) < 0) {
                    throw new ReglaDeNegocioException(
                            "Existencia insuficiente de \"" + nombreProducto + "\" ("
                            + codigo + ").\nDisponible: "
                            + existenciaAnterior.stripTrailingZeros().toPlainString()
                            + " | Solicitado: "
                            + cantidad.stripTrailingZeros().toPlainString()
                    );
                }

                BigDecimal existenciaNueva = existenciaAnterior.subtract(cantidad);

                try (PreparedStatement ps = conexion.prepareStatement(sqlDescontarStock)) {
                    ps.setBigDecimal(1, existenciaNueva);
                    ps.setString(2, codigo);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conexion.prepareStatement(sqlMovimiento)) {
                    ps.setString(1, codigo);
                    ps.setBigDecimal(2, cantidad);
                    ps.setBigDecimal(3, existenciaAnterior);
                    ps.setBigDecimal(4, existenciaNueva);
                    ps.setInt(5, venta.getUsuarioResponsable().getIdUsuario());
                    ps.setString(6, numeroVenta);
                    ps.setString(7, "Venta " + numeroVenta);
                    ps.executeUpdate();
                }
            }

            conexion.commit();

            return true;

        } catch (SQLException | RuntimeException e) {
            if (e instanceof ReglaDeNegocioException) {
                ultimoError = e.getMessage();
            }

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
        ultimoError = null;

        if (numeroVenta == null || numeroVenta.trim().isEmpty()) {
            ultimoError = "Indica el número de la venta a eliminar.";
            return false;
        }

        String sqlResponsable
                = "SELECT id_usuario_responsable FROM venta WHERE numero_venta = ? FOR UPDATE";

        String sqlLineas
                = "SELECT codigo_producto, cantidad_solicitada FROM detalle_venta WHERE numero_venta = ?";

        String sqlBloquearStock
                = "SELECT existencia_actual FROM producto WHERE codigo = ? FOR UPDATE";

        String sqlDevolverStock
                = "UPDATE producto SET existencia_actual = ? WHERE codigo = ?";

        String sqlMovimiento
                = "INSERT INTO movimiento_inventario ("
                + "codigo_producto, tipo_movimiento, cantidad, existencia_anterior, "
                + "existencia_nueva, id_usuario, referencia_documento, motivo"
                + ") VALUES (?, 'ENTRADA_AJUSTE', ?, ?, ?, ?, ?, ?)";

        String sqlDetalles = "DELETE FROM detalle_venta WHERE numero_venta= ?";
        String sqlVenta = "DELETE FROM venta WHERE numero_venta = ?";

        Connection conexion = null;
        try {
            conexion = conexionBD.obtenerConexion();
            if (conexion == null) {
                return false;
            }
            conexion.setAutoCommit(false);

            int idUsuarioMovimiento;
            try (PreparedStatement ps = conexion.prepareStatement(sqlResponsable)) {
                ps.setString(1, numeroVenta);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new ReglaDeNegocioException(
                                "La venta " + numeroVenta + " no existe."
                        );
                    }
                    idUsuarioMovimiento = rs.getInt("id_usuario_responsable");
                }
            }

            Usuario usuarioSesion = Sesion.getUsuarioActual();
            if (usuarioSesion != null && usuarioSesion.getIdUsuario() > 0) {
                idUsuarioMovimiento = usuarioSesion.getIdUsuario();
            }
            List<Object[]> lineas = new ArrayList<>();
            try (PreparedStatement ps = conexion.prepareStatement(sqlLineas)) {
                ps.setString(1, numeroVenta);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lineas.add(new Object[]{
                            rs.getString("codigo_producto"),
                            rs.getBigDecimal("cantidad_solicitada")
                        });
                    }
                }
            }

            for (Object[] linea : lineas) {
                String codigo = (String) linea[0];
                BigDecimal cantidad = (BigDecimal) linea[1];
                BigDecimal existenciaAnterior;

                try (PreparedStatement ps = conexion.prepareStatement(sqlBloquearStock)) {
                    ps.setString(1, codigo);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("El producto " + codigo + " no existe.");
                        }
                        existenciaAnterior = rs.getBigDecimal("existencia_actual");
                    }
                }

                BigDecimal existenciaNueva = existenciaAnterior.add(cantidad);

                try (PreparedStatement ps = conexion.prepareStatement(sqlDevolverStock)) {
                    ps.setBigDecimal(1, existenciaNueva);
                    ps.setString(2, codigo);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conexion.prepareStatement(sqlMovimiento)) {
                    ps.setString(1, codigo);
                    ps.setBigDecimal(2, cantidad);
                    ps.setBigDecimal(3, existenciaAnterior);
                    ps.setBigDecimal(4, existenciaNueva);
                    ps.setInt(5, idUsuarioMovimiento);
                    ps.setString(6, numeroVenta);
                    ps.setString(7, "Anulación de venta " + numeroVenta);
                    ps.executeUpdate();
                }
            }

            try (PreparedStatement pstmtDetalles = conexion.prepareStatement(sqlDetalles)) {
                pstmtDetalles.setString(1, numeroVenta);
                pstmtDetalles.executeUpdate();
            }

            try (PreparedStatement pstmtVenta = conexion.prepareStatement(sqlVenta)) {
                pstmtVenta.setString(1, numeroVenta);
                if (pstmtVenta.executeUpdate() != 1) {
                    throw new SQLException("No se pudo eliminar la venta " + numeroVenta + ".");
                }
            }

            conexion.commit();
            return true;

        } catch (SQLException | RuntimeException e) {
            if (e instanceof ReglaDeNegocioException) {
                ultimoError = e.getMessage();
            }
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
