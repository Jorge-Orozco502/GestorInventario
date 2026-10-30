package modelo;

import java.math.BigDecimal;

public class ProductoTerminado extends Producto {

    //Constructor
    public ProductoTerminado(String codigo, String nombre, String categoria, String descripcion,
            String unidadMedida, BigDecimal precioCompra, BigDecimal precioVenta,
            double existenciaActual, double existenciaMinima, EstadoProducto estadoProducto) {
        super(codigo, nombre, categoria, descripcion, unidadMedida, precioCompra, precioVenta,
                existenciaActual, existenciaMinima, estadoProducto);
    }

    //Sobrecarga de constructor
    public ProductoTerminado(String codigo, String nombre, String categoria, String descripcion,
            String unidadMedida, BigDecimal precioCompra, BigDecimal precioVenta,
            double existenciaMinima) {
        super(codigo, nombre, categoria, descripcion, unidadMedida, precioCompra, precioVenta,
                0.0, existenciaMinima, EstadoProducto.ACTIVO);
    }

    //Constructor Vacio
    public ProductoTerminado() {
        super();
    }

    @Override
    public boolean esVendible() {
        return true;
    }

}
