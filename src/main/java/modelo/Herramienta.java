package modelo;

import java.math.BigDecimal;

public class Herramienta extends Producto {

    //Constructor
    public Herramienta(String codigo, String nombre, String categoria, String descripcion, String unidadMedida, BigDecimal precioCompra, BigDecimal precioVenta, double existenciaActual, double existenciaMinima, EstadoProducto estadoProducto) {
        super(codigo, nombre, categoria, descripcion, unidadMedida, precioCompra, precioVenta, existenciaActual, existenciaMinima, estadoProducto);
    }

    //Constructor vacio
    public Herramienta() {
        super();
    }

    //Metodo para definir si es vendible
    @Override
    public boolean esVendible() {
        return false;
    }

    @Override
    public String toString() {
        return "Herramienta{"
                + "codigo='" + getCodigo() + '\''
                + ",nombre='" + getNombre() + '\''
                + ",categoria='" + getCategoria() + '\''
                + ",precioVenta=" + getPrecioVenta()
                + ",existenciaActual=" + getExistenciaActual()
                + '}';
    }

}
