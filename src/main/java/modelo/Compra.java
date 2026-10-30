package modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Compra {

    private String numeroCompra;
    private LocalDate fechaCompra;
    private FormaPago formaPago;
    private BigDecimal total;

    private Proveedor proveedor;
    private List<DetalleCompra> detallesCompra;

    //Constructor
    public Compra(String numeroCompra, LocalDate fechaCompra, FormaPago formaPago, BigDecimal total, Proveedor proveedor) {
        setNumeroCompra(numeroCompra);
        setFechaCompra(fechaCompra);
        setFormaPago(formaPago);
        setTotal(total != null ? total : BigDecimal.ZERO);
        setProveedor(proveedor);
        this.detallesCompra = new ArrayList<>();
    }

    //Constructor Vacio
    public Compra() {
        this.total = BigDecimal.ZERO;
        this.detallesCompra = new ArrayList<>();
    }

    public String getNumeroCompra() {
        return numeroCompra;
    }

    public void setNumeroCompra(String numeroCompra) {
        if (numeroCompra == null || numeroCompra.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de compra no puede estar vacío.");
        }
        this.numeroCompra = numeroCompra;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = Objects.requireNonNull(fechaCompra, "La fecha de compra no puede ser nula.");
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(FormaPago formaPago) {
        this.formaPago = Objects.requireNonNull(formaPago, "La forma de pago no puede ser nula.");
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        if (total == null || total.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El total de la compra no puede ser negativo");
        }
        this.total = total;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = Objects.requireNonNull(proveedor, "La compra debe estar asociada a un proveedor.");
    }

    public List<DetalleCompra> getDetallesCompra() {
        return Collections.unmodifiableList(detallesCompra);
    }

    public void setDetallesCompra(List<DetalleCompra> detallesCompra) {
        Objects.requireNonNull(detallesCompra, "La lista de detalles de compra no puede ser nula.");
        this.detallesCompra = new ArrayList<>(detallesCompra);
        recalcularTotal();
    }

    public void agregarDetalleCompra(DetalleCompra detalle) {
        Objects.requireNonNull(detalle, "El detalle de compra no puede ser nulo.");
        this.detallesCompra.add(detalle);
        recalcularTotal();
    }

    public void recalcularTotal() {
        BigDecimal sumaTotal = BigDecimal.ZERO;

        for (DetalleCompra detalle : this.detallesCompra) {
            if (detalle.getSubtotal() != null) {
                sumaTotal = sumaTotal.add(detalle.getSubtotal());
            }
        }
        this.total = sumaTotal;
    }

    public List<MovimientoInventario> confirmarCompra(Usuario usuarioResponsable) {
        List<MovimientoInventario> movimientos = new ArrayList<>();

        if (this.detallesCompra == null || this.detallesCompra.isEmpty()) {
            throw new IllegalArgumentException("No se puede confirmar una compra sin detalles de productos");
        }

        if (usuarioResponsable == null) {
            throw new IllegalArgumentException("Se requiere un usuario responsable para registrar la confirmación de la compra.");
        }

        for (DetalleCompra detalle : this.detallesCompra) {
            Producto producto = detalle.getProducto();

            if (producto == null) {
                throw new IllegalArgumentException("Uno de los detalles de la compra no tiene un producto asociado");
            }

            double cantidadComprada = detalle.getCantidadComprada();

            double existenciaAnterior = producto.getExistenciaActual();

            producto.aumentarExistencia(cantidadComprada);

            double existenciaNueva = producto.getExistenciaActual();

            MovimientoInventario movimiento = new MovimientoInventario(
                    0,
                    LocalDateTime.now(),
                    TipoMovimiento.ENTRADA_COMPRA,
                    cantidadComprada,
                    existenciaAnterior,
                    existenciaNueva,
                    "Compra de productos - Factura No:" + this.numeroCompra,
                    "Ingreso por compra al proveedor" + (proveedor != null ? proveedor.getNombreEmpresa() : ""),
                    usuarioResponsable,
                    producto
            );
            movimientos.add(movimiento);
        }
        return movimientos;
    }

    @Override
    public String toString() {
        return "Compra{"
                + "númeroDeCompra='" + numeroCompra + '\''
                + ", fechaDeCompra=" + fechaCompra
                + ", proveedor=" + (proveedor != null ? proveedor.getNombreEmpresa() : "N/A")
                + ", formaDePago=" + formaPago
                + ", total=" + total
                + '}';
    }

}
