package dao;

import conexion.conexionBD;
import modelo.TrabajoFabricacion;
import modelo.MaterialUtilizado;
import modelo.ProductoTerminado;
import modelo.MateriaPrima;
import modelo.Usuario;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TrabajoFabricacionDAO {

    // CREATE
    public boolean insertarTrabajo(TrabajoFabricacion trabajo) {
        if (trabajo == null) {
            System.out.println("Error: El objeto trabajo de fabricación no puede ser nulo.");
            return false;
        }

        if (trabajo.getProductoTerminado() == null || trabajo.getUsuarioResponsable() == null) {
            System.out.println("Error: El trabajo debe tener un producto terminado y un usuario responsable.");
            return false;
        }

        String sqlTrabajo = "INSERT INTO trabajo_fabricacion (numero_trabajo, fecha, descripcion, codigo_producto_terminado, tipo_producto, id_usuario_responsable) VALUES (?, ?, ?, ?, 'PRODUCTO_TERMINADO', ?)";
        String sqlMaterial = "INSERT INTO material_utilizado (numero_trabajo, codigo_materia_prima, tipo_producto, cantidad_utilizada) VALUES (?, ?, 'MATERIA_PRIMA', ?)";

        Connection conexion = null;
        try {
            conexion = conexionBD.obtenerConexion();
            if (conexion == null) {
                return false;
            }

            conexion.setAutoCommit(false);

            try (PreparedStatement pstmtTrabajo = conexion.prepareStatement(sqlTrabajo)) {
                pstmtTrabajo.setString(1, trabajo.getNumeroTrabajo());
                pstmtTrabajo.setDate(2, java.sql.Date.valueOf(trabajo.getFecha() != null ? trabajo.getFecha() : LocalDate.now()));

                if (trabajo.getDescripcion() != null && !trabajo.getDescripcion().trim().isEmpty()) {
                    pstmtTrabajo.setString(3, trabajo.getDescripcion());
                } else {
                    pstmtTrabajo.setNull(3, Types.VARCHAR);
                }

                pstmtTrabajo.setString(4, trabajo.getProductoTerminado().getCodigo());
                pstmtTrabajo.setInt(5, trabajo.getUsuarioResponsable().getIdUsuario());
                pstmtTrabajo.executeUpdate();
            }

            if (trabajo.getDetalleMateriales() != null && !trabajo.getDetalleMateriales().isEmpty()) {
                try (PreparedStatement pstmtMaterial = conexion.prepareStatement(sqlMaterial)) {
                    for (MaterialUtilizado material : trabajo.getDetalleMateriales()) {
                        if (material != null && material.getMateriaPrima() != null) {
                            pstmtMaterial.setString(1, trabajo.getNumeroTrabajo());
                            pstmtMaterial.setString(2, material.getMateriaPrima().getCodigo());
                            pstmtMaterial.setDouble(3, material.getCantidadUtilizada());
                            pstmtMaterial.addBatch();
                        }
                    }
                    pstmtMaterial.executeBatch();
                }
            }

            conexion.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("Error al registrar el trabajo de fabricación: " + e.getMessage());
            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error al realizar el rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            if (conexion != null) {
                try {
                    conexion.setAutoCommit(true);
                    conexion.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }

    // READ
    public List<TrabajoFabricacion> listarTrabajos() {
        List<TrabajoFabricacion> listaTrabajos = new ArrayList<>();
        String sql = "SELECT t.*, p.nombre AS nombre_producto, u.nombre_usuario, u.nombre_completo "
                + "FROM trabajo_fabricacion t "
                + "JOIN producto p ON t.codigo_producto_terminado = p.codigo AND t.tipo_producto = p.tipo_producto "
                + "JOIN usuario u ON t.id_usuario_responsable = u.id_usuario";

        try (Connection conexion = conexionBD.obtenerConexion(); Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String numeroTrabajo = rs.getString("numero_trabajo");
                Date fechaSql = rs.getDate("fecha");
                LocalDate fecha = fechaSql != null ? fechaSql.toLocalDate() : LocalDate.now();
                String descripcion = rs.getString("descripcion");
                String tipoProducto = rs.getString("tipo_producto");

                String codigoProducto = rs.getString("codigo_producto_terminado");
                String nombreProducto = rs.getString("nombre_producto");

                int idUsuario = rs.getInt("id_usuario_responsable");
                String nombreUsuario = rs.getString("nombre_usuario");
                String nombreCompleto = rs.getString("nombre_completo");

                ProductoTerminado productoTerminado = new ProductoTerminado();
                productoTerminado.setCodigo(codigoProducto);
                productoTerminado.setNombre(nombreProducto);

                Usuario usuario = new Usuario() {
                    @Override
                    public boolean tienePermiso(String accion) {
                        return true;
                    }
                };
                usuario.setIdUsuario(idUsuario);
                usuario.setNombreUsuario(nombreUsuario);
                usuario.setNombreCompleto(nombreCompleto);

                List<MaterialUtilizado> materiales = listarMaterialesPorTrabajo(numeroTrabajo, conexion);

                TrabajoFabricacion trabajo = new TrabajoFabricacion(
                        numeroTrabajo, fecha, descripcion, tipoProducto, productoTerminado, usuario, materiales
                );

                listaTrabajos.add(trabajo);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar los trabajos de fabricación: " + e.getMessage());
        }
        return listaTrabajos;
    }

    private List<MaterialUtilizado> listarMaterialesPorTrabajo(String numeroTrabajo, Connection conexionExterna) {
        List<MaterialUtilizado> materiales = new ArrayList<>();
        String sql = "SELECT m.*, p.nombre AS nombre_materia FROM material_utilizado m "
                + "JOIN producto p ON m.codigo_materia_prima = p.codigo "
                + "WHERE m.numero_trabajo = ?";

        boolean usarConexionExterna = (conexionExterna != null);
        Connection conexion = usarConexionExterna ? conexionExterna : conexionBD.obtenerConexion();

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, numeroTrabajo);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String codigoMateria = rs.getString("codigo_materia_prima");
                    String nombreMateria = rs.getString("nombre_materia");
                    double cantidadUtilizada = rs.getDouble("cantidad_utilizada");

                    MateriaPrima materiaPrima = new MateriaPrima();
                    materiaPrima.setCodigo(codigoMateria);
                    materiaPrima.setNombre(nombreMateria);

                    MaterialUtilizado material = new MaterialUtilizado(materiaPrima, cantidadUtilizada);
                    materiales.add(material);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al listar los materiales utilizados: " + e.getMessage());
        } finally {
            if (!usarConexionExterna && conexion != null) {
                try {
                    conexion.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return materiales;
    }

    // DELETE
    public boolean eliminarTrabajo(String numeroTrabajo) {
        String sql = "DELETE FROM trabajo_fabricacion WHERE numero_trabajo = ?";
        try (Connection conexion = conexionBD.obtenerConexion(); PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            pstmt.setString(1, numeroTrabajo);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar el trabajo de fabricación: " + e.getMessage());
            return false;
        }
    }

    // UPDATE
    public boolean actualizarTrabajo(TrabajoFabricacion trabajo) {
        if (trabajo == null || trabajo.getNumeroTrabajo() == null) {
            System.out.println("Error: El trabajo o el número de trabajo no pueden ser nulos.");
            return false;
        }

        String sql = "UPDATE trabajo_fabricacion SET fecha = ?, descripcion = ?, codigo_producto_terminado = ?, tipo_producto = 'PRODUCTO_TERMINADO', id_usuario_responsable = ? WHERE numero_trabajo = ?";

        try (Connection conexion = conexionBD.obtenerConexion(); PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            pstmt.setDate(1, java.sql.Date.valueOf(trabajo.getFecha() != null ? trabajo.getFecha() : java.time.LocalDate.now()));

            if (trabajo.getDescripcion() != null && !trabajo.getDescripcion().trim().isEmpty()) {
                pstmt.setString(2, trabajo.getDescripcion());
            } else {
                pstmt.setNull(2, java.sql.Types.VARCHAR);
            }

            pstmt.setString(3, trabajo.getProductoTerminado().getCodigo());
            pstmt.setInt(4, trabajo.getUsuarioResponsable().getIdUsuario());
            pstmt.setString(5, trabajo.getNumeroTrabajo());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar el trabajo de fabricación: " + e.getMessage());
            return false;
        }
    }

}
