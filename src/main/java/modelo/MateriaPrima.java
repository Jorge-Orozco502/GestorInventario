package modelo;

import java.math.BigDecimal;

public class MateriaPrima extends Producto {

    //Constructor
    public MateriaPrima(String codigo, String nombre, String categoria, String descripcion, String unidadMedida, BigDecimal precioCompra, BigDecimal precioVenta, double existenciaActual, double existenciaMinima, EstadoProducto estadoProducto) {
        super(codigo, nombre, categoria, descripcion, unidadMedida, precioCompra, precioVenta, existenciaActual, existenciaMinima, estadoProducto);
    }

    //Constructor vacio
    public MateriaPrima() {
        super();
    }

    @Override
    public boolean esVendible() {
        return false;
    }

}
