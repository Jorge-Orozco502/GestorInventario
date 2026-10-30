package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexionBD {
    private static final String URL = "jdbc:mysql://localhost:3306/inventario_vidrieria";
    private static final String USER = "root";
    private static final String PASSWORD = "TUCONTRASENA";

    //Metodo para DAO
    public static Connection obtenerConexion(){
        Connection conexion = null;
        try {
            conexion = DriverManager.getConnection(URL,USER, PASSWORD);
        } catch (SQLException e){
            System.out.println("Error al conectar a la base de datos:" + e.getMessage());
        }
        return conexion;
    }

    public static void main(String[] args) {
        try (Connection conexion = DriverManager.getConnection(URL, USER, PASSWORD)) {
            if (conexion != null) {
                System.out.println("¡Conexión exitosa!");
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos:");
            e.printStackTrace();
        }
    }
}
