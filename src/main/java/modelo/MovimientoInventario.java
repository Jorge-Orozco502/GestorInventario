package modelo;

import java.time.LocalDateTime;

public class MovimientoInventario {

    //Atributos
    private int idMovimiento;
    private LocalDateTime fecha;
    private TipoMovimiento tipoMovimiento;
    private double cantidad;
    private double existenciaAnterior;
    private double existenciaNueva;
    private String referenciaDocumento;
    private String motivo;
    private Usuario usuarioResponsable;

    private Producto producto;

    //Constructor
    public MovimientoInventario(int idMovimiento, LocalDateTime fecha, TipoMovimiento tipoMovimiento, double cantidad, double existenciaAnterior, double existenciaNueva, String referenciaDocumento, String motivo,
            Usuario usuarioResponsable, Producto producto) {
        this.idMovimiento = idMovimiento;
        setFecha(fecha);
        setTipoMovimiento(tipoMovimiento);
        setCantidad(cantidad);
        setExistenciaAnterior(existenciaAnterior);
        setExistenciaNueva(existenciaNueva);
        this.referenciaDocumento = referenciaDocumento;
        this.motivo = motivo;
        setUsuarioResponsable(usuarioResponsable);
        setProducto(producto);
    }

    //Constructor asignando fecha y hora actual por defecto
    public MovimientoInventario() {
        this.fecha = LocalDateTime.now();
    }

    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha del movimiento no puede ser nula.");
        }
        this.fecha = fecha;
    }

    public TipoMovimiento getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(TipoMovimiento tipoMovimiento) {
        if (tipoMovimiento == null) {
            throw new IllegalArgumentException("El tipo de movimiento no puede ser nulo.");
        }
        this.tipoMovimiento = tipoMovimiento;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad del movimiento no puede ser negativa.");
        }
        this.cantidad = cantidad;
    }

    public double getExistenciaAnterior() {
        return existenciaAnterior;
    }

    public void setExistenciaAnterior(double existenciaAnterior) {
        if (existenciaAnterior < 0) {
            throw new IllegalArgumentException("La existencia anterior no puede ser negativa.");
        }
        this.existenciaAnterior = existenciaAnterior;
    }

    public double getExistenciaNueva() {
        return existenciaNueva;
    }

    public void setExistenciaNueva(double existenciaNueva) {
        if (existenciaNueva < 0) {
            throw new IllegalArgumentException("La existencia nueva no puede ser negativa.");
        }
        this.existenciaNueva = existenciaNueva;
    }

    public String getReferenciaDocumento() {
        return referenciaDocumento;
    }

    public void setReferenciaDocumento(String referenciaDocumento) {
        this.referenciaDocumento = referenciaDocumento;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Usuario getUsuarioResponsable() {
        return usuarioResponsable;
    }

    public void setUsuarioResponsable(Usuario usuarioResponsable) {
        if (usuarioResponsable == null) {
            throw new IllegalArgumentException("El usuario responsable no puede ser nulo.");
        }
        this.usuarioResponsable = usuarioResponsable;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto asociado al movimiento no puede ser nulo. ");
        }
        this.producto = producto;
    }
}
