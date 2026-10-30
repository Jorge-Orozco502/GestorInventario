package modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RegistroHistorial {
    //Atributos
    private final String id;
    private final LocalDate fecha;
    private final String tipoMovimiento;
    private final String referencia;
    private final BigDecimal total;
    private final String usuarioResponsable;

    //Constructor
    public RegistroHistorial(String id, LocalDate fecha, String tipoMovimiento,
            String referencia, BigDecimal total, String usuarioResponsable) {
        this.id = id;
        this.fecha = fecha;
        this.tipoMovimiento = tipoMovimiento;
        this.referencia = referencia;
        this.total = total;
        this.usuarioResponsable = usuarioResponsable;
    }

    public String getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public String getReferencia() {
        return referencia;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getUsuarioResponsable() {
        return usuarioResponsable;
    }
}
