package dao;

import conexion.conexionBD;
import modelo.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    //CREATE
    public boolean insertarCliente(Cliente cliente) {
        String sql = "INSERT INTO cliente (codigo, nombre, nit_cf, telefono, direccion) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexion = conexionBD.obtenerConexion(); PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            if (conexion == null) {
                return false;
            }

            pstmt.setString(1, cliente.getCodigoCliente());
            pstmt.setString(2, cliente.getNombreCliente());
            pstmt.setString(3, cliente.getNitCF());
            pstmt.setString(4, cliente.getTelefono());
            pstmt.setString(5, cliente.getDireccion());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al registrar el cliente:" + e.getMessage());
            return false;
        }
    }

    //READ
    public List<Cliente> listarClientes() {
        List<Cliente> listaClientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente";

        try (Connection conexion = conexionBD.obtenerConexion(); Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String codigo = rs.getString("codigo");
                String nombre = rs.getString("nombre");
                String nitCf = rs.getString("nit_cf");
                String telefono = rs.getString("telefono");
                String direccion = rs.getString("direccion");

                Cliente cliente = new Cliente(codigo, nombre, telefono, nitCf, direccion);
                listaClientes.add(cliente);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar los clientes:" + e.getMessage());
        }
        return listaClientes;
    }

//UPDATE
    public boolean actualizarCliente(Cliente cliente) {
        String sql = "UPDATE cliente SET nombre = ?, nit_cf = ?, telefono = ?, direccion = ? WHERE codigo = ?";

        try (Connection conexion = conexionBD.obtenerConexion(); PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            if (conexion == null) {
                return false;
            }

            pstmt.setString(1, cliente.getNombreCliente());
            pstmt.setString(2, cliente.getNitCF());
            pstmt.setString(3, cliente.getTelefono());
            pstmt.setString(4, cliente.getDireccion());
            pstmt.setString(5, cliente.getCodigoCliente()); // Clave para ubicar el registro

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar el cliente:" + e.getMessage());
            return false;
        }
    }

    //DELETE
    public boolean eliminarCliente(String codigoCliente) {
        String sql = "DELETE FROM cliente WHERE codigo = ?";

        try (Connection conexion = conexionBD.obtenerConexion(); PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            if (conexion == null) {
                return false;
            }

            pstmt.setString(1, codigoCliente);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar el cliente:" + e.getMessage());
            return false;
        }
    }

    public List<Cliente> filtrarClientes(String filtro) {
        List<Cliente> listaClientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE codigo LIKE ? OR nombre LIKE ?";

        try (Connection conexion = conexionBD.obtenerConexion(); PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            String patron = "%" + filtro + "%";
            pstmt.setString(1, patron);
            pstmt.setString(2, patron);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String codigo = rs.getString("codigo");
                    String nombre = rs.getString("nombre");
                    String nitCf = rs.getString("nit_cf");
                    String telefono = rs.getString("telefono");
                    String direccion = rs.getString("direccion");

                    Cliente cliente = new Cliente(codigo, nombre, telefono, nitCf, direccion);
                    listaClientes.add(cliente);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al filtrar los clientes: " + e.getMessage());
        }
        return listaClientes;
    }

    public Cliente buscarPorNit(String nit) throws SQLException {
        String sql = "SELECT codigo, nombre, nit_cf, telefono, direccion "
                + "FROM cliente WHERE nit_cf = ? LIMIT 1";

        try (Connection cn = conexionBD.obtenerConexion()) {
            if (cn == null) {
                throw new SQLException(
                        "No se pudo conectar con la base de datos."
                );
            }

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setString(1, nit.trim());

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Cliente(
                                rs.getString("codigo"),
                                rs.getString("nombre"),
                                rs.getString("telefono"),
                                rs.getString("nit_cf"),
                                rs.getString("direccion")
                        );
                    }
                }
            }
        }

        return null;
    }

    //  NIT, SUCURSALES
    public static String normalizarNit(String nit) {
        if (nit == null) {
            return "";
        }
        return nit.replaceAll("[\\s\\-/.]", "").toUpperCase();
    }

    public static String normalizarTexto(String texto) {
        if (texto == null) {
            return "";
        }
        String sinTildes = java.text.Normalizer
                .normalize(texto, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinTildes.trim().replaceAll("\\s+", " ").toLowerCase();
    }

    public List<Cliente> listarPorNit(String nit) throws SQLException {
        String sql = "SELECT codigo, nombre, nit_cf, telefono, direccion "
                + "FROM cliente "
                + "WHERE UPPER(REPLACE(REPLACE(REPLACE(REPLACE(nit_cf, '-', ''), ' ', ''), '/', ''), '.', '')) = ? "
                + "ORDER BY codigo";

        List<Cliente> lista = new ArrayList<>();

        try (Connection cn = conexionBD.obtenerConexion()) {
            if (cn == null) {
                throw new SQLException("No se pudo conectar con la base de datos.");
            }

            try (PreparedStatement ps = cn.prepareStatement(sql)) {
                ps.setString(1, normalizarNit(nit));

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(new Cliente(
                                rs.getString("codigo"),
                                rs.getString("nombre"),
                                rs.getString("telefono"),
                                rs.getString("nit_cf"),
                                rs.getString("direccion")
                        ));
                    }
                }
            }
        }

        return lista;
    }

    public Cliente buscarMismaSucursal(String nit, String direccion, String codigoExcluir)
            throws SQLException {

        String direccionBuscada = normalizarTexto(direccion);

        for (Cliente c : listarPorNit(nit)) {
            if (codigoExcluir != null && codigoExcluir.equals(c.getCodigoCliente())) {
                continue;
            }
            if (normalizarTexto(c.getDireccion()).equals(direccionBuscada)) {
                return c;
            }
        }
        return null;
    }

    public String generarSiguienteCodigo() throws SQLException {
        String sql = "SELECT COALESCE(MAX(CAST(codigo AS UNSIGNED)), 0) "
                + "FROM cliente WHERE codigo REGEXP '^[0-9]+$'";

        try (Connection cn = conexionBD.obtenerConexion()) {
            if (cn == null) {
                throw new SQLException("No se pudo conectar con la base de datos.");
            }

            try (Statement st = cn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                long maximo = rs.next() ? rs.getLong(1) : 0;
                return String.format("%02d", maximo + 1);
            }
        }
    }

    public String registrarConCodigoAutomatico(Cliente cliente) throws SQLException {
        String sql = "INSERT INTO cliente (codigo, nombre, nit_cf, telefono, direccion) "
                + "VALUES (?, ?, ?, ?, ?)";

        SQLException ultimoError = null;

        for (int intento = 0; intento < 5; intento++) {
            String codigo = generarSiguienteCodigo();

            try (Connection cn = conexionBD.obtenerConexion()) {
                if (cn == null) {
                    throw new SQLException("No se pudo conectar con la base de datos.");
                }

                try (PreparedStatement ps = cn.prepareStatement(sql)) {
                    ps.setString(1, codigo);
                    ps.setString(2, cliente.getNombreCliente());
                    ps.setString(3, normalizarNit(cliente.getNitCF()));
                    ps.setString(4, cliente.getTelefono());
                    ps.setString(5, cliente.getDireccion());
                    ps.executeUpdate();

                    cliente.setCodigoCliente(codigo);
                    cliente.setNitCF(normalizarNit(cliente.getNitCF()));
                    return codigo;
                }
            } catch (SQLIntegrityConstraintViolationException e) {
                ultimoError = e;
            }
        }

        throw ultimoError != null
                ? ultimoError
                : new SQLException("No se pudo generar un código de cliente.");
    }

}
