package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Empleado extends Usuario {

    private List<String> accionesPermitidas;

    //Constructor
    public Empleado(String nombreUsuario, String contrasenaHash, String nombreCompleto) {
        super(nombreUsuario, contrasenaHash, nombreCompleto);
        this.accionesPermitidas = new ArrayList<>();
        inicializarPermisosPorDefecto();
    }

    //Constructor vacío
    public Empleado() {
        super();
        this.accionesPermitidas = new ArrayList<>();
        inicializarPermisosPorDefecto();
    }

    private void inicializarPermisosPorDefecto() {
        this.accionesPermitidas.add("REGISTRAR_VENTAS");
        this.accionesPermitidas.add("CONSULTAR_INVENTARIO");
        this.accionesPermitidas.add("GESTIONAR_CLIENTES");
        this.accionesPermitidas.add("CONSULTAR_PRODUCTOS");
    }

    public List<String> getAccionesPermitidas() {
        return Collections.unmodifiableList(accionesPermitidas);
    }

    public void setAccionesPermitidas(List<String> accionesPermitidas) {
        Objects.requireNonNull(accionesPermitidas, "La lista de acciones permitidas no puede ser nula.");
        this.accionesPermitidas = new ArrayList<>(accionesPermitidas);
    }

    public boolean tienePermiso(String accion) {
        Objects.requireNonNull(accion, "La acción a verificar no puede ser nula.");
        return this.accionesPermitidas.contains(accion.trim().toUpperCase());
    }

    @Override
    public String toString() {
        return "Empleado{"
                + "nombreUsuario='" + getNombreUsuario() + '\''
                + ", nombreCompleto='" + getNombreCompleto() + '\''
                + ", totalAccionesPermitidas=" + accionesPermitidas.size()
                + '}';
    }

}
