package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DetalleVenta {

    //Atributos
    private double cantidadSolicitada;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private BigDecimal ivaLinea;
    private BigDecimal totalLinea;

    private Producto producto;

    //Constructor
    public DetalleVenta(Producto producto, double cantidadSolicitada, BigDecimal precioUnitario) {
        setProducto(producto);
        setCantidadSolicitada(cantidadSolicitada);
        setPrecioUnitario(precioUnitario != null ? precioUnitario : BigDecimal.ZERO);
        calcularTotales();
    }

    //Constructor vacio
    public DetalleVenta() {
        this.subtotal = BigDecimal.ZERO;
        this.ivaLinea = BigDecimal.ZERO;
        this.totalLinea = BigDecimal.ZERO;
    }

    public double getCantidadSolicitada() {
        return cantidadSolicitada;
    }

    public void setCantidadSolicitada(double cantidadSolicitada) {
        if (cantidadSolicitada <= 0) {
            throw new IllegalArgumentException("La cantidad solicitada debe ser mayor a cero.");
        }
        this.cantidadSolicitada = cantidadSolicitada;
        calcularTotales();
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo.");
        }
        this.precioUnitario = precioUnitario;
        calcularTotales();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getIvaLinea() {
        return ivaLinea;
    }

    public void setIvaLinea(BigDecimal ivaLinea) {
        this.ivaLinea = ivaLinea;
    }

    public BigDecimal getTotalLinea() {
        return totalLinea;
    }

    public void setTotalLinea(BigDecimal totalLinea) {
        this.totalLinea = totalLinea;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El detalle de venta debe estar asociado a un producto.");
        }
        this.producto = producto;
    }

    public void calcularTotales() {
        if (this.precioUnitario == null || this.cantidadSolicitada <= 0) {
            this.subtotal = BigDecimal.ZERO;
            this.ivaLinea = BigDecimal.ZERO;
            this.totalLinea = BigDecimal.ZERO;
            return;
        }

        BigDecimal cantidad = BigDecimal.valueOf(this.cantidadSolicitada);

        BigDecimal totalOriginal = this.precioUnitario
                .multiply(cantidad)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal porcentaje = this.producto == null
                ? BigDecimal.ZERO
                : this.producto.getPorcentajeDescuento();

        BigDecimal descuento = totalOriginal
                .multiply(porcentaje)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        this.totalLinea = totalOriginal.subtract(descuento)
                .setScale(2, RoundingMode.HALF_UP);

        this.subtotal = this.totalLinea
                .divide(new BigDecimal("1.12"), 2, RoundingMode.HALF_UP);

        this.ivaLinea = this.totalLinea.subtract(this.subtotal)
                .setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return "DetalleVenta{"
                + "producto=" + (producto != null ? producto.getNombre() : "N/A")
                + ",cantidad=" + cantidadSolicitada
                + ",precioUnitario=" + precioUnitario
                + ",subtotal=" + subtotal
                + ",ivaLinea=" + ivaLinea
                + ",totalLinea=" + totalLinea
                + '}';
    }

}
