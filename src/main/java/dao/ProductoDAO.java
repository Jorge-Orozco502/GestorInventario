package dao;

import conexion.conexionBD;
import modelo.Producto;
import modelo.ProductoTerminado;
import modelo.MateriaPrima;
import modelo.Herramienta;
import modelo.EstadoProducto;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    // CREATE
    public boolean insertarProducto(Producto producto) {
        String sql = "INSERT INTO producto (codigo, tipo_producto, nombre, categoria, descripcion, unidad_medida, precio_compra, precio_venta, existencia_actual, existencia_minima, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = conexionBD.obtenerConexion(); PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            if (conexion == null) {
                return false;
            }

            String tipoStr = "PRODUCTO_TERMINADO";
            if (producto instanceof MateriaPrima) {
                tipoStr = "MATERIA_PRIMA";
            } else if (producto instanceof Herramienta) {
                tipoStr = "HERRAMIENTA";
            }

            pstmt.setString(1, producto.getCodigo());
            pstmt.setString(2, tipoStr);
            pstmt.setString(3, producto.getNombre());
            pstmt.setString(4, producto.getCategoria());
            pstmt.setString(5, producto.getDescripcion());
            pstmt.setString(6, producto.getUnidadMedida());
            pstmt.setBigDecimal(7, producto.getPrecioCompra());
            pstmt.setBigDecimal(8, producto.getPrecioVenta());
            pstmt.setDouble(9, producto.getExistenciaActual());
            pstmt.setDouble(10, producto.getExistenciaMinima());
            pstmt.setString(11, producto.getEstadoProducto().name());

            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error al registrar el producto: " + e.getMessage());
            return false;
        }
    }

    // READ
    public List<Producto> listarProductos() {
        List<Producto> listaProductos = new ArrayList<>();
        String sql = "SELECT * FROM producto";

        try (Connection conexion = conexionBD.obtenerConexion(); Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String codigo = rs.getString("codigo");
                String tipoProdStr = rs.getString("tipo_producto");
                String nombre = rs.getString("nombre");
                String categoria = rs.getString("categoria");
                String descripcion = rs.getString("descripcion");
                String unidadMedida = rs.getString("unidad_medida");
                BigDecimal precioCompra = rs.getBigDecimal("precio_compra");
                BigDecimal precioVenta = rs.getBigDecimal("precio_venta");
                double existenciaActual = rs.getDouble("existencia_actual");
                double existenciaMinima = rs.getDouble("existencia_minima");
                EstadoProducto estadoProducto = EstadoProducto.valueOf(rs.getString("estado"));

                Producto producto;
                if ("MATERIA_PRIMA".equalsIgnoreCase(tipoProdStr)) {
                    producto = new MateriaPrima(codigo, nombre, categoria, descripcion, unidadMedida, precioCompra, precioVenta, existenciaActual, existenciaMinima, estadoProducto);
                } else if ("HERRAMIENTA".equalsIgnoreCase(tipoProdStr)) {
                    producto = new Herramienta(codigo, nombre, categoria, descripcion, unidadMedida, precioCompra, precioVenta, existenciaActual, existenciaMinima, estadoProducto);
                } else {
                    producto = new ProductoTerminado(codigo, nombre, categoria, descripcion, unidadMedida, precioCompra, precioVenta, existenciaActual, existenciaMinima, estadoProducto);
                }

                listaProductos.add(producto);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar los productos: " + e.getMessage());
        }
        return listaProductos;
    }

    // READ 
    public Producto buscarProductoPorCodigo(String codigoBuscado) {
        String sql = "SELECT * FROM producto WHERE codigo = ?";
        try (Connection conexion = conexionBD.obtenerConexion(); PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            if (conexion == null) {
                return null;
            }

            pstmt.setString(1, codigoBuscado);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String codigo = rs.getString("codigo");
                    String tipoProdStr = rs.getString("tipo_producto");
                    String nombre = rs.getString("nombre");
                    String categoria = rs.getString("categoria");
                    String descripcion = rs.getString("descripcion");
                    String unidadMedida = rs.getString("unidad_medida");
                    BigDecimal precioCompra = rs.getBigDecimal("precio_compra");
                    BigDecimal precioVenta = rs.getBigDecimal("precio_venta");
                    double existenciaActual = rs.getDouble("existencia_actual");
                    double existenciaMinima = rs.getDouble("existencia_minima");
                    EstadoProducto estadoProducto = EstadoProducto.valueOf(rs.getString("estado"));

                    if ("MATERIA_PRIMA".equalsIgnoreCase(tipoProdStr)) {
                        return new MateriaPrima(codigo, nombre, categoria, descripcion, unidadMedida, precioCompra, precioVenta, existenciaActual, existenciaMinima, estadoProducto);
                    } else if ("HERRAMIENTA".equalsIgnoreCase(tipoProdStr)) {
                        return new Herramienta(codigo, nombre, categoria, descripcion, unidadMedida, precioCompra, precioVenta, existenciaActual, existenciaMinima, estadoProducto);
                    } else {
                        return new ProductoTerminado(codigo, nombre, categoria, descripcion, unidadMedida, precioCompra, precioVenta, existenciaActual, existenciaMinima, estadoProducto);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar el producto por código: " + e.getMessage());
        }
        return null;
    }

    // UPDATE
    public boolean actualizarProducto(Producto producto) {
        String sql = "UPDATE producto SET tipo_producto = ?, nombre = ?, categoria = ?, descripcion = ?, unidad_medida = ?, precio_compra = ?, precio_venta = ?, existencia_actual = ?, existencia_minima = ?, estado = ? WHERE codigo = ?";

        try (Connection conexion = conexionBD.obtenerConexion(); PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            if (conexion == null) {
                return false;
            }

            String tipoStr = "PRODUCTO_TERMINADO";
            if (producto instanceof MateriaPrima) {
                tipoStr = "MATERIA_PRIMA";
            } else if (producto instanceof Herramienta) {
                tipoStr = "HERRAMIENTA";
            }

            pstmt.setString(1, tipoStr);
            pstmt.setString(2, producto.getNombre());
            pstmt.setString(3, producto.getCategoria());
            pstmt.setString(4, producto.getDescripcion());
            pstmt.setString(5, producto.getUnidadMedida());
            pstmt.setBigDecimal(6, producto.getPrecioCompra());
            pstmt.setBigDecimal(7, producto.getPrecioVenta());
            pstmt.setDouble(8, producto.getExistenciaActual());
            pstmt.setDouble(9, producto.getExistenciaMinima());
            pstmt.setString(10, producto.getEstadoProducto().name());
            pstmt.setString(11, producto.getCodigo());

            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar el producto: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean eliminarProducto(String codigoProducto) {
        String sql = "DELETE FROM producto WHERE codigo = ?";

        try (Connection conexion = conexionBD.obtenerConexion(); PreparedStatement pstmt = conexion.prepareStatement(sql)) {

            if (conexion == null) {
                return false;
            }

            pstmt.setString(1, codigoProducto);
            int filasAfectadas = pstmt.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar el producto: " + e.getMessage());
            return false;
        }
    }

    public List<Producto> filtrarProductos(String nombre, String categoria, String estado) {
        List<Producto> listaProductos = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM producto WHERE 1=1");
        List<Object> parametros = new ArrayList<>();

        if (nombre != null && !nombre.trim().isEmpty()) {
            sql.append(" AND nombre LIKE ?");
            parametros.add("%" + nombre.trim() + "%");
        }
        if (categoria != null && !categoria.equalsIgnoreCase("Todos") && !categoria.trim().isEmpty()) {
            sql.append(" AND categoria = ?");
            parametros.add(categoria.trim());
        }
        if (estado != null && !estado.equalsIgnoreCase("Todos") && !estado.trim().isEmpty()) {
            sql.append(" AND estado = ?");
            parametros.add(estado.trim());
        }

        try (Connection conexion = conexionBD.obtenerConexion(); PreparedStatement pstmt = conexion.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                pstmt.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String codigo = rs.getString("codigo");
                    String tipoProdStr = rs.getString("tipo_producto");
                    String nom = rs.getString("nombre");
                    String cat = rs.getString("categoria");
                    String descripcion = rs.getString("descripcion");
                    String unidadMedida = rs.getString("unidad_medida");
                    BigDecimal precioCompra = rs.getBigDecimal("precio_compra");
                    BigDecimal precioVenta = rs.getBigDecimal("precio_venta");
                    double existenciaActual = rs.getDouble("existencia_actual");
                    double existenciaMinima = rs.getDouble("existencia_minima");
                    EstadoProducto estadoProducto = EstadoProducto.valueOf(rs.getString("estado"));

                    Producto producto;
                    if ("MATERIA_PRIMA".equalsIgnoreCase(tipoProdStr)) {
                        producto = new MateriaPrima(codigo, nom, cat, descripcion, unidadMedida, precioCompra, precioVenta, existenciaActual, existenciaMinima, estadoProducto);
                    } else if ("HERRAMIENTA".equalsIgnoreCase(tipoProdStr)) {
                        producto = new Herramienta(codigo, nom, cat, descripcion, unidadMedida, precioCompra, precioVenta, existenciaActual, existenciaMinima, estadoProducto);
                    } else {
                        producto = new ProductoTerminado(codigo, nom, cat, descripcion, unidadMedida, precioCompra, precioVenta, existenciaActual, existenciaMinima, estadoProducto);
                    }

                    listaProductos.add(producto);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al filtrar los productos: " + e.getMessage());
        }
        return listaProductos;
    }

    public List<Producto> listarStockBajo() {
        String sql = "SELECT * FROM producto "
                + "WHERE existencia_actual > 0 "
                + "AND existencia_actual <= existencia_minima";
        return listarPorConsulta(sql);
    }

    public List<Producto> listarAgotados() {
        String sql = "SELECT * FROM producto WHERE existencia_actual <= 0";
        return listarPorConsulta(sql);
    }

    private List<Producto> listarPorConsulta(String sql) {
        List<Producto> lista = new ArrayList<>();

        try (Connection conexion = conexionBD.obtenerConexion(); Statement stmt = conexion.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String codigo = rs.getString("codigo");
                String tipoProdStr = rs.getString("tipo_producto");
                String nombre = rs.getString("nombre");
                String categoria = rs.getString("categoria");
                String descripcion = rs.getString("descripcion");
                String unidadMedida = rs.getString("unidad_medida");
                BigDecimal precioCompra = rs.getBigDecimal("precio_compra");
                BigDecimal precioVenta = rs.getBigDecimal("precio_venta");
                double existenciaActual = rs.getDouble("existencia_actual");
                double existenciaMinima = rs.getDouble("existencia_minima");
                EstadoProducto estado = EstadoProducto.valueOf(rs.getString("estado"));

                Producto producto;
                if ("MATERIA_PRIMA".equalsIgnoreCase(tipoProdStr)) {
                    producto = new MateriaPrima(codigo, nombre, categoria, descripcion, unidadMedida, precioCompra, precioVenta, existenciaActual, existenciaMinima, estado);
                } else if ("HERRAMIENTA".equalsIgnoreCase(tipoProdStr)) {
                    producto = new Herramienta(codigo, nombre, categoria, descripcion, unidadMedida, precioCompra, precioVenta, existenciaActual, existenciaMinima, estado);
                } else {
                    producto = new ProductoTerminado(codigo, nombre, categoria, descripcion, unidadMedida, precioCompra, precioVenta, existenciaActual, existenciaMinima, estado);
                }
                lista.add(producto);
            }
        } catch (SQLException e) {
            System.out.println("Error al listar productos por consulta: " + e.getMessage());
        }
        return lista;
    }
}
