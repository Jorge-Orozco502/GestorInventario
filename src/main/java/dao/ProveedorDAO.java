package dao;

import conexion.conexionBD;
import modelo.Proveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {

    //CREATE
    public boolean insertarProveedor(Proveedor proveedor) {
        if (proveedor == null) {
            System.out.println("Error: El objeto proveedor no puede ser nulo.");
            return false;
        }

        String sql = "INSERT INTO proveedor (codigo, nombre_empresa, nit, telefono, direccion, correo_electronico) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conexion = conexionBD.obtenerConexion(); PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            if (conexion == null) {
                return false;
            }

            pstmt.setString(1, proveedor.getCodigo());
            pstmt.setString(2, proveedor.getNombreEmpresa());
            pstmt.setString(3, proveedor.getNit());
            pstmt.setString(4, proveedor.getTelefono());
            pstmt.setString(5, proveedor.getDireccion());
            pstmt.setString(6, proveedor.getCorreoElectronico());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al registrar el proveedor:" + e.getMessage());
            return false;
        }
    }

    //READ
    public List<Proveedor> listarProveedores() {
        List<Proveedor> listaProveedores = new ArrayList<>();
        String sql = "SELECT * FROM proveedor";

        try (Connection conexion = conexionBD.obtenerConexion(); Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String codigo = rs.getString("codigo");
                String nombreEmpresa = rs.getString("nombre_empresa");
                String nit = rs.getString("nit");
                String telefono = rs.getString("telefono");
                String direccion = rs.getString("direccion");
                String correoElectronico = rs.getString("correo_electronico");

                Proveedor proveedor = new Proveedor(codigo, nombreEmpresa, nit, telefono, direccion, correoElectronico);
                listaProveedores.add(proveedor);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar los proveedores:" + e.getMessage());
        }
        return listaProveedores;
    }

    public Proveedor buscarProveedorPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return null;
        }
        String sql = "SELECT * FROM proveedor WHERE codigo = ?";
        try (Connection conexion = conexionBD.obtenerConexion(); PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            if (conexion == null) {
                return null;
            }
            pstmt.setString(1, codigo.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Proveedor(
                            rs.getString("codigo"),
                            rs.getString("nombre_empresa"),
                            rs.getString("nit"),
                            rs.getString("telefono"),
                            rs.getString("direccion"),
                            rs.getString("correo_electronico")
                    );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar el proveedor:" + e.getMessage());
        }
        return null;
    }

//UPDATE
    public boolean actualizarProveedor(Proveedor proveedor) {
        if (proveedor == null) {
            System.out.println("Error: El objeto proveedor a actualizar no puede ser nulo.");
            return false;
        }

        String sql = "UPDATE proveedor SET nombre_empresa = ?, nit = ?, telefono = ?, direccion = ?, correo_electronico = ? WHERE codigo = ?";

        try (Connection conexion = conexionBD.obtenerConexion(); PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            if (conexion == null) {
                return false;
            }

            pstmt.setString(1, proveedor.getNombreEmpresa());
            pstmt.setString(2, proveedor.getNit());
            pstmt.setString(3, proveedor.getTelefono());
            pstmt.setString(4, proveedor.getDireccion());
            pstmt.setString(5, proveedor.getCorreoElectronico());
            pstmt.setString(6, proveedor.getCodigo());
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar el proveedor:" + e.getMessage());
            return false;
        }
    }

//DELETE
    public boolean eliminarProveedor(String codigoProveedor) {
        if (codigoProveedor == null || codigoProveedor.trim().isEmpty()) {
            System.out.println("Error: El código de proveedor no puede estar vacío para la eliminación.");
            return false;
        }

        String sql = "DELETE FROM proveedor WHERE codigo = ?";

        try (Connection conexion = conexionBD.obtenerConexion(); PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            if (conexion == null) {
                return false;
            }

            pstmt.setString(1, codigoProveedor);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar el proveedor:" + e.getMessage());
            return false;
        }
    }
}
