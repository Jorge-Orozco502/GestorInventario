package modelo;

import java.util.Objects;

public class Proveedor {

    //Atributos
    private String codigo;
    private String nombreEmpresa;
    private String nit;
    private String telefono;
    private String direccion;
    private String correoElectronico;

    //Constructor
    public Proveedor(String codigo, String nombreEmpresa, String nit, String telefono, String direccion, String correoElectronico) {
        setCodigo(codigo);
        setNombreEmpresa(nombreEmpresa);
        setNit(nit);
        setTelefono(telefono);
        setDireccion(direccion);
        setCorreoElectronico(correoElectronico);
    }

    //Constructor Vacio
    public Proveedor() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código del proveedor no puede estar vacío.");
        }
        this.codigo = codigo.trim();
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        if (nombreEmpresa == null || nombreEmpresa.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la empresa no puede estar vacío.");
        }
        this.nombreEmpresa = nombreEmpresa.trim();
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        if (nit == null || nit.trim().isEmpty()) {
            throw new IllegalArgumentException("El NIT del proveedor no puede estar vacio.");
        }
        this.nit = nit.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = (telefono != null) ? telefono.trim() : null;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = (direccion != null) ? direccion.trim() : null;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = (correoElectronico != null) ? correoElectronico.trim() : null;
    }

    @Override
    public String toString() {
        return nombreEmpresa + "(NIT:" + nit + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Proveedor proveedor = (Proveedor) o;
        return Objects.equals(codigo, proveedor.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}
