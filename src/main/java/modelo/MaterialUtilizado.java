package modelo;

public class MaterialUtilizado {

    //Atributos
    private MateriaPrima materiaPrima;
    private double cantidadUtilizada;

    //Constructor
    public MaterialUtilizado(MateriaPrima materiaPrima, double cantidadUtilizada) {
        setMateriaPrima(materiaPrima);
        setCantidadUtilizada(cantidadUtilizada);
    }

    //Constructor Vacio
    public MaterialUtilizado() {
    }

    public MateriaPrima getMateriaPrima() {
        return materiaPrima;
    }

    public void setMateriaPrima(MateriaPrima materiaPrima) {
        if (materiaPrima == null) {
            throw new IllegalArgumentException("La materia prima no puede ser nula.");
        }
        this.materiaPrima = materiaPrima;
    }

    public double getCantidadUtilizada() {
        return cantidadUtilizada;
    }

    public void setCantidadUtilizada(double cantidadUtilizada) {
        if (cantidadUtilizada < 0) {
            throw new IllegalArgumentException("La cantidad utilizada no puede ser negativa.");
        }
        this.cantidadUtilizada = cantidadUtilizada;
    }

    public double calcularCosto() {
        if (this.materiaPrima != null && this.materiaPrima.getPrecioCompra() != null) {
            //Convertir el decimal del precio a un double
            return this.materiaPrima.getPrecioCompra().doubleValue() * this.cantidadUtilizada;
        }
        return 0.0;
    }

    @Override
    public String toString() {
        return "Material Utilizado{"
                + "materiaPrima=" + (materiaPrima != null ? materiaPrima.getNombre() : "N/A")
                + ", cantidadUtilizada=" + cantidadUtilizada
                + '}';
    }
}
