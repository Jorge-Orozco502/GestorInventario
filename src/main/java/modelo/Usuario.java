package modelo;

import java.time.LocalDate;
import java.util.Objects;

public abstract class Usuario {

    //Atributos
    private int idUsuario;
    private String nombreUsuario;
    private String contrasenaHash;
    private String nombreCompleto;
    private boolean activo;
    private LocalDate fechaCreacion;

    //Constructor
    public Usuario(int idUsuario, String nombreUsuario, String contrasenaHash, String nombreCompleto, boolean activo, LocalDate fechaCreacion) {
        setIdUsuario(idUsuario);
        setNombreUsuario(nombreUsuario);
        setContrasenaHash(contrasenaHash);
        setNombreCompleto(nombreCompleto);
        setActivo(activo);
        setFechaCreacion(fechaCreacion);
    }

    //Constructor para instanciar nuevo usuario desde cero
    public Usuario(String nombreUsuario, String contrasenaHash, String nombreCompleto) {
        setNombreUsuario(nombreUsuario);
        setContrasenaHash(contrasenaHash);
        setNombreCompleto(nombreCompleto);
        this.activo = true;
        this.fechaCreacion = LocalDate.now();
    }

    //Constructor vacio
    public Usuario() {
        this.fechaCreacion = LocalDate.now();
        this.activo = true;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser un valor positivo.");
        }
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        }
        this.nombreUsuario = nombreUsuario.trim();
    }

    public String getContrasenaHash() {
        return contrasenaHash;
    }

    public void setContrasenaHash(String contrasenaHash) {
        if (contrasenaHash == null || contrasenaHash.trim().isEmpty()) {
            throw new IllegalArgumentException("La contrasena no puede estar vacía.");
        }
        this.contrasenaHash = contrasenaHash;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre completo no puede estar vacío.");
        }
        this.nombreCompleto = nombreCompleto.trim();
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public LocalDate getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDate fechaCreacion) {
        if (fechaCreacion == null) {
            throw new IllegalArgumentException("La fecha de creación no puede ser nula.");
        }
        this.fechaCreacion = fechaCreacion;
    }

    public boolean cambiarContrasena(String nuevaContrasena) {
        if (nuevaContrasena == null || nuevaContrasena.trim().isEmpty()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía.");
        }
        setContrasenaHash(nuevaContrasena);
        return true;
    }

    public boolean verificarContrasena(String contrasenaIngresada) {
        if (contrasenaIngresada == null || contrasenaIngresada.trim().isEmpty()) {
            return false;
        }
        return this.contrasenaHash.equals(contrasenaIngresada);
    }

    public abstract boolean tienePermiso(String accion);

    @Override
    public String toString() {
        return nombreCompleto + "(Usuario:" + nombreUsuario + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Usuario usuario = (Usuario) o;
        return idUsuario == usuario.idUsuario;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario);
    }

}
