package modelo;

public class Cliente {

    //Atributos
    private String codigoCliente;
    private String nombreCliente;
    private String telefono;
    private String nitCF;
    private String direccion;

    //Constructor
    public Cliente(String codigoCliente, String nombreCliente, String telefono, String nitCF, String direccion) {
        this.codigoCliente = codigoCliente;
        setNombreCliente(nombreCliente);
        this.telefono = telefono;
        this.nitCF = nitCF;
        this.direccion = direccion;
    }

    //Constructor vacio
    public Cliente() {
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente no puede estar vacío.");
        }
        this.nombreCliente = nombreCliente.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNitCF() {
        return nitCF;
    }

    public void setNitCF(String nitCF) {
        this.nitCF = nitCF;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public String toString() {
        return nombreCliente + "(NIT:" + nitCF + ")";
    }
}
