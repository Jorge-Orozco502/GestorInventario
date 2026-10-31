package dao;

import conexion.conexionBD;
import modelo.MovimientoInventario;
import modelo.Producto;
import modelo.Usuario;
import modelo.TipoMovimiento;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MovimientoInventarioDAO {

    // CREATE
    public boolean insertarMovimiento(MovimientoInventario movimiento) {
        if (movimiento == null) {
            System.out.println("Error: El objeto movimiento de inventario no puede ser nulo.");
            return false;
        }

        if (movimiento.getProducto() == null || movimiento.getProducto().getCodigo() == null) {
            System.out.println("Error: El movimiento debe estar asociado a un producto válido.");
            return false;
        }

        if (movimiento.getUsuarioResponsable() == null) {
            System.out.println("Error: El movimiento debe tener un usuario responsable asociado.");
            return false;
        }

        String sql = "INSERT INTO movimiento_inventario (fecha, codigo_producto, tipo_movimiento, cantidad, existencia_anterior, existencia_nueva, id_usuario, referencia_documento, motivo) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = conexionBD.obtenerConexion(); PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            if (conexion == null) {
                return false;
            }

            pstmt.setTimestamp(1, movimiento.getFecha() != null ? Timestamp.valueOf(movimiento.getFecha()) : Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(2, movimiento.getProducto().getCodigo());
            pstmt.setString(3, movimiento.getTipoMovimiento().name());
            pstmt.setDouble(4, movimiento.getCantidad());
            pstmt.setDouble(5, movimiento.getExistenciaAnterior());
            pstmt.setDouble(6, movimiento.getExistenciaNueva());
            pstmt.setInt(7, movimiento.getUsuarioResponsable().getIdUsuario());
            pstmt.setString(8, movimiento.getReferenciaDocumento());
            pstmt.setString(9, movimiento.getMotivo());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al registrar el movimiento de inventario: " + e.getMessage());
            return false;
        }
    }

    // READ
    public List<MovimientoInventario> listarMovimientos() {
        List<MovimientoInventario> listaMovimientos = new ArrayList<>();
        String sql = "SELECT m.*, u.nombre_usuario, u.nombre_completo, p.nombre AS nombre_producto "
                + "FROM movimiento_inventario m "
                + "JOIN usuario u ON m.id_usuario = u.id_usuario "
                + "JOIN producto p ON m.codigo_producto = p.codigo";

        try (Connection conexion = conexionBD.obtenerConexion(); Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int idMovimiento = rs.getInt("id_movimiento");
                Timestamp timestamp = rs.getTimestamp("fecha");
                LocalDateTime fecha = timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now();

                String codigoProducto = rs.getString("codigo_producto");
                String nombreProducto = rs.getString("nombre_producto");

                TipoMovimiento tipoMovimiento = TipoMovimiento.valueOf(rs.getString("tipo_movimiento"));
                double cantidad = rs.getDouble("cantidad");
                double existenciaAnterior = rs.getDouble("existencia_anterior");
                double existenciaNueva = rs.getDouble("existencia_nueva");

                int idUsuario = rs.getInt("id_usuario");
                String nombreUsuario = rs.getString("nombre_usuario");

                String referenciaDocumento = rs.getString("referencia_documento");
                String motivo = rs.getString("motivo");

                Producto producto = new Producto() {
                    @Override
                    public boolean esVendible() {
                        return true;
                    }
                };
                producto.setCodigo(codigoProducto);
                producto.setNombre(nombreProducto);

                Usuario usuario = new Usuario() {
                    @Override
                    public boolean tienePermiso(String accion) {
                        return true;
                    }
                };
                usuario.setIdUsuario(idUsuario);
                usuario.setNombreUsuario(nombreUsuario);

                MovimientoInventario movimiento = new MovimientoInventario(
                        idMovimiento, fecha, tipoMovimiento, cantidad,
                        existenciaAnterior, existenciaNueva, referenciaDocumento,
                        motivo, usuario, producto
                );

                listaMovimientos.add(movimiento);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar los movimientos de inventario: " + e.getMessage());
        }
        return listaMovimientos;
    }

    public List<MovimientoInventario> listarMovimientosPorProducto(String codigoProducto) {
        List<MovimientoInventario> listaMovimientos = new ArrayList<>();

        if (codigoProducto == null || codigoProducto.trim().isEmpty()) {
            return listaMovimientos;
        }

        String sql = "SELECT * FROM movimiento_inventario WHERE codigo_producto = ? ORDER BY fecha DESC";

        try (Connection conexion = conexionBD.obtenerConexion(); PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            if (conexion == null) {
                return listaMovimientos;
            }
            pstmt.setString(1, codigoProducto.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int idMovimiento = rs.getInt("id_movimiento");
                    Timestamp timestamp = rs.getTimestamp("fecha");
                    LocalDateTime fecha = timestamp != null ? timestamp.toLocalDateTime() : LocalDateTime.now();
                    TipoMovimiento tipoMovimiento = TipoMovimiento.valueOf(rs.getString("tipo_movimiento"));
                    double cantidad = rs.getDouble("cantidad");
                    double existenciaAnterior = rs.getDouble("existencia_anterior");
                    double existenciaNueva = rs.getDouble("existencia_nueva");
                    int idUsuario = rs.getInt("id_usuario");
                    String referenciaDocumento = rs.getString("referencia_documento");
                    String motivo = rs.getString("motivo");

                    Producto producto = new Producto() {
                        @Override
                        public boolean esVendible() {
                            return true;
                        }
                    };
                    producto.setCodigo(codigoProducto);

                    Usuario usuario = new Usuario() {
                        @Override
                        public boolean tienePermiso(String accion) {
                            return true;
                        }
                    };
                    usuario.setIdUsuario(idUsuario);

                    MovimientoInventario movimiento = new MovimientoInventario(
                            idMovimiento, fecha, tipoMovimiento, cantidad,
                            existenciaAnterior, existenciaNueva, referenciaDocumento,
                            motivo, usuario, producto
                    );

                    listaMovimientos.add(movimiento);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar movimientos por producto: " + e.getMessage());
        }
        return listaMovimientos;
    }
}
