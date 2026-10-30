package dao;

import conexion.conexionBD;
import modelo.Compra;
import modelo.DetalleCompra;
import modelo.FormaPago;
import modelo.Proveedor;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CompraDAO {

    //CREATE
    public boolean insertarCompra(Compra compra) {
        if (compra == null) {
            System.out.println("Error: El objeto compra no puede ser nulo.");
            return false;
        }

        if (compra.getNumeroCompra() == null || compra.getNumeroCompra().trim().isEmpty()) {
            System.out.println("Error: El número de compra es obligatorio.");
            return false;
        }

        String sqlCompra = "INSERT INTO compra (numero_compra, fecha, codigo_proveedor, id_usuario_responsable, forma_pago, total) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO detalle_compra (numero_compra, codigo_producto, cantidad, costo_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";

        Connection conexion = null;

        try {
            conexion = conexionBD.obtenerConexion();
            if (conexion == null) {
                return false;
            }
            conexion.setAutoCommit(false);

            try (PreparedStatement pstmtCompra = conexion.prepareStatement(sqlCompra)) {
                pstmtCompra.setString(1, compra.getNumeroCompra());
                pstmtCompra.setDate(2, java.sql.Date.valueOf(compra.getFechaCompra()));
                pstmtCompra.setString(3, compra.getProveedor() != null ? compra.getProveedor().getCodigo() : "");
                pstmtCompra.setInt(4, 1);
                pstmtCompra.setString(5, compra.getFormaPago().name());
                pstmtCompra.setBigDecimal(6, compra.getTotal());
                pstmtCompra.executeUpdate();
            }

            if (compra.getDetallesCompra() != null && !compra.getDetallesCompra().isEmpty()) {
                try (PreparedStatement pstmtDetalle = conexion.prepareStatement(sqlDetalle)) {
                    for (DetalleCompra detalle : compra.getDetallesCompra()) {
                        pstmtDetalle.setString(1, compra.getNumeroCompra());
                        pstmtDetalle.setString(2, detalle.getProducto() != null ? detalle.getProducto().getCodigo() : "");
                        pstmtDetalle.setDouble(3, detalle.getCantidadComprada());
                        pstmtDetalle.setBigDecimal(4, detalle.getCostoUnitario());
                        pstmtDetalle.setBigDecimal(5, detalle.getSubtotal());
                        pstmtDetalle.addBatch();
                    }
                    pstmtDetalle.executeBatch();
                }
            }
            conexion.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al registrar la compra (Rollback ejecutado):" + e.getMessage());
            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error al realizar rollback:" + ex.getMessage());
                }
            }
        } finally {
            if (conexion != null) {
                try {
                    conexion.setAutoCommit(true);
                    conexion.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar la conexión:" + e.getMessage());
                }
            }
        }
        return false;
    }

//READ
    public List<Compra> listarCompras() {
        List<Compra> listaCompras = new ArrayList<>();
        String sql = "SELECT * FROM compra";

        try (Connection conexion = conexionBD.obtenerConexion(); Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String numeroCompra = rs.getString("numero_compra");
                Date fechaSql = rs.getDate("fecha");
                LocalDate fechaCompra = fechaSql != null ? fechaSql.toLocalDate() : LocalDate.now();
                BigDecimal total = rs.getBigDecimal("total");
                FormaPago formaPago = FormaPago.valueOf(rs.getString("forma_pago"));

                Proveedor proveedor = new Proveedor();
                proveedor.setCodigo(rs.getString("codigo_proveedor"));

                Compra compra = new Compra(numeroCompra, fechaCompra, formaPago, total, proveedor);
                listaCompras.add(compra);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar las compras:" + e.getMessage());
        }
        return listaCompras;
    }

//UPDATE
    public boolean actualizarCompra(Compra compra) {
        String sql = "UPDATE compra SET forma_pago = ?, total = ? WHERE numero_compra = ?";

        try (Connection conexion = conexionBD.obtenerConexion(); PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            if (conexion == null) {
                return false;
            }

            pstmt.setString(1, compra.getFormaPago().name());
            pstmt.setBigDecimal(2, compra.getTotal());
            pstmt.setString(3, compra.getNumeroCompra());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar la compra:" + e.getMessage());
            return false;
        }
    }

    //DELETE
    public boolean eliminarCompra(String numeroCompra) {
        String sqlDetalles = "DELETE FROM detalle_compra WHERE numero_compra = ?";
        String sqlCompra = "DELETE FROM compra WHERE numero_compra = ?";

        Connection conexion = null;
        try {
            conexion = conexionBD.obtenerConexion();
            if (conexion == null) {
                return false;
            }
            conexion.setAutoCommit(false);

            try (PreparedStatement pstmtDetalles = conexion.prepareStatement(sqlDetalles)) {
                pstmtDetalles.setString(1, numeroCompra);
                pstmtDetalles.executeUpdate();
            }

            try (PreparedStatement pstmtCompra = conexion.prepareStatement(sqlCompra)) {
                pstmtCompra.setString(1, numeroCompra);
                pstmtCompra.executeUpdate();
            }
            conexion.commit();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al eliminar la compra (Rollback ejecutado):" + e.getMessage());
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
                    System.out.println("Error al cerrar conexión:" + e.getMessage());
                }
            }
        }
        return false;
    }
}
