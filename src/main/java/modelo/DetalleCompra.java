package modelo;

import java.math.BigDecimal;

public class DetalleCompra {

    //Atributos
    private double cantidadComprada;
    private BigDecimal costoUnitario;
    private BigDecimal subtotal;
    private Producto producto;

    //Constructor
    public DetalleCompra(double cantidadComprada, BigDecimal costoUnitario, BigDecimal subtotal, Producto producto) {
        this.cantidadComprada = cantidadComprada;
        this.costoUnitario = costoUnitario;
        this.subtotal = subtotal;
        this.producto = producto;
    }

    //Constructor vacio
    public DetalleCompra() {
        this.costoUnitario = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }

    public double getCantidadComprada() {
        return cantidadComprada;
    }

    public void setCantidadComprada(double cantidadComprada) {
        this.cantidadComprada = cantidadComprada;
        calcularSubtotal();
    }

    public BigDecimal getCostoUnitario() {
        return costoUnitario;
    }

    public void setCostoUnitario(BigDecimal costoUnitario) {
        this.costoUnitario = costoUnitario;
        calcularSubtotal();
    }

    public BigDecimal getSubtotal() {
        if (subtotal == null) {
            calcularSubtotal();
        }
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public BigDecimal calcularSubtotal() {
        if (this.costoUnitario != null && this.cantidadComprada > 0) {
            this.subtotal = this.costoUnitario.multiply(new BigDecimal(this.cantidadComprada));
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
        return this.subtotal;
    }

}
