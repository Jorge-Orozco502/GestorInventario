package modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Venta {
    //Atributos
    private String numeroVenta;
    private LocalDate fecha;
    private FormaPago formaPago;
    private BigDecimal subtotalGeneral;
    private BigDecimal totalDescuentos;
    private BigDecimal totalIva;
    private BigDecimal totalFinal;
    private Cliente cliente;
    private Usuario usuarioResponsable;
    private List<DetalleVenta> detallesVenta;

    private String nombreCf;
    private String direccionCf;

    //Constructor
    public Venta(String numeroVenta, LocalDate fecha, FormaPago formaPago, BigDecimal subtotalGeneral, BigDecimal totalDescuentos, BigDecimal totalIva,
            BigDecimal totalFinal, Cliente cliente, Usuario usuarioResponsable) {
        this.numeroVenta = Objects.requireNonNull(numeroVenta, "El número de venta no puede ser nulo.");
        this.fecha = Objects.requireNonNull(fecha, "La fecha no puede ser nula.");
        this.formaPago = Objects.requireNonNull(formaPago, "La forma de pago no puede ser nula.");
        this.cliente = Objects.requireNonNull(cliente, "El cliente asociado no puede ser nulo.");
        this.usuarioResponsable = Objects.requireNonNull(usuarioResponsable, "El usuario responsable no puede ser nulo.");
        this.subtotalGeneral = validarMonto(subtotalGeneral, "Subtotal general");
        this.totalDescuentos = validarMonto(totalDescuentos, "Total descuentos");
        this.totalIva = validarMonto(totalIva, "Total IVA");
        this.totalFinal = validarMonto(totalFinal, "Total final");
        this.detallesVenta = new ArrayList<>();
    }

    public String getNumeroVenta() {
        return numeroVenta;
    }

    public void setNumeroVenta(String numeroVenta) {
        this.numeroVenta = Objects.requireNonNull(numeroVenta, "El número de venta no puede ser nulo.");
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = Objects.requireNonNull(fecha, "La fecha no puede ser nula.");
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(FormaPago formaPago) {
        this.formaPago = Objects.requireNonNull(formaPago, "La forma de pago no puede ser nula.");
    }

    public BigDecimal getSubtotalGeneral() {
        return subtotalGeneral;
    }

    public void setSubtotalGeneral(BigDecimal subtotalGeneral) {
        this.subtotalGeneral = validarMonto(subtotalGeneral, "Subtotal general");
    }

    public BigDecimal getTotalDescuentos() {
        return totalDescuentos;
    }

    public void setTotalDescuentos(BigDecimal totalDescuentos) {
        this.totalDescuentos = validarMonto(totalDescuentos, "Total descuentos");
    }

    public BigDecimal getTotalIva() {
        return totalIva;
    }

    public void setTotalIva(BigDecimal totalIva) {
        this.totalIva = validarMonto(totalIva, "Total IVA");
    }

    public BigDecimal getTotalFinal() {
        return totalFinal;
    }

    public void setTotalFinal(BigDecimal totalFinal) {
        this.totalFinal = validarMonto(totalFinal, "Total final");
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = Objects.requireNonNull(cliente, "El cliente no puede ser nulo.");
    }

    public Usuario getUsuarioResponsable() {
        return usuarioResponsable;
    }

    public void setUsuarioResponsable(Usuario usuarioResponsable) {
        this.usuarioResponsable = Objects.requireNonNull(usuarioResponsable, "El usuario responsable no puede ser nulo.");
    }

    public String getNombreCf() {
        return nombreCf;
    }

    public void setNombreCf(String nombreCf) {
        this.nombreCf = limpiarOpcional(nombreCf);
    }

    public String getDireccionCf() {
        return direccionCf;
    }

    public void setDireccionCf(String direccionCf) {
        this.direccionCf = limpiarOpcional(direccionCf);
    }

    public String getDescripcionCliente() {
        if (cliente == null) {
            return "";
        }

        String nit = cliente.getNitCF() == null
                ? ""
                : cliente.getNitCF().replaceAll("[\\s\\-/.]", "").toUpperCase();

        StringBuilder texto;

        if (nit.equals("CF")) {
            texto = new StringBuilder("CF");
            if (nombreCf != null) {
                texto.append(" – ").append(nombreCf);
            }
            if (direccionCf != null) {
                texto.append(" – ").append(direccionCf);
            }
            return texto.toString();
        }

        texto = new StringBuilder(
                cliente.getNombreCliente() == null ? "" : cliente.getNombreCliente()
        );
        String direccion = cliente.getDireccion();
        if (direccion != null && !direccion.isBlank()) {
            texto.append(" – ").append(direccion.trim());
        }
        return texto.toString();
    }

    private static String limpiarOpcional(String texto) {
        if (texto == null) {
            return null;
        }
        String limpio = texto.trim();
        return limpio.isEmpty() ? null : limpio;
    }

    public List<DetalleVenta> getDetallesVenta() {
        return Collections.unmodifiableList(detallesVenta);
    }

    public void setDetallesVenta(List<DetalleVenta> detallesVenta) {
        Objects.requireNonNull(detallesVenta, "La lista de detalles no puede ser nula.");
        this.detallesVenta = new ArrayList<>(detallesVenta);
    }

    //=================================
    //Metodo para validar que los montos sean validos
    private BigDecimal validarMonto(BigDecimal monto, String nombreCampo) {
        Objects.requireNonNull(monto, "El campo" + nombreCampo + "no puede ser nulo.");
        if (monto.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El campo" + nombreCampo + "no puede tener un valor negativo.");
        }
        return monto;
    }

    public void calcularTotales() {
        BigDecimal subtotalAcumulado = BigDecimal.ZERO;
        for (DetalleVenta detalle : detallesVenta) {
            subtotalAcumulado = subtotalAcumulado.add(detalle.getSubtotal());
        }
        this.subtotalGeneral = subtotalAcumulado;

        this.totalIva = this.subtotalGeneral.multiply(new BigDecimal("0.12"));

        BigDecimal descuentos = (this.totalDescuentos != null) ? this.totalDescuentos : BigDecimal.ZERO;

        this.totalFinal = this.subtotalGeneral.add(this.totalIva).subtract(descuentos);
    }

    public boolean validarExistenciaDisponible() {
        for (DetalleVenta detalle : detallesVenta) {
            if (detalle.getProducto() != null) {
                if (detalle.getProducto().getExistenciaActual() < detalle.getCantidadSolicitada()) {
                    return false;
                }
            }
        }
        return true;
    }

    public MovimientoInventario confirmarVenta() {
        if (!validarExistenciaDisponible()) {
            throw new IllegalArgumentException("No se puede confirmar la venta: stock insuficiente en uno o más producto.");
        }
        calcularTotales();
        return null;
    }

    public void agregarDetalle(DetalleVenta detalle) {
        Objects.requireNonNull(detalle, "El detalle de venta no puede ser nulo.");
        this.detallesVenta.add(detalle);
    }

    public void eliminarDetalle(DetalleVenta detalle) {
        Objects.requireNonNull(detalle, "El detalle a eliminar no puede ser nulo.");
        this.detallesVenta.remove(detalle);
    }
}
