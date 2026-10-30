

package aplicacion;

import conexion.conexionBD;
import java.sql.Connection;

public class PruebaConexion {
    public static void main(String[] args) {
        System.out.println("Probando conexion con la base de datos...");
        Connection conn = conexionBD.obtenerConexion();
        
        if (conn != null) {
            System.out.println("¡Conexion exitosa establecida correctamente!");
        } else {
            System.out.println("Error: No se pudo establecer la conexion.");
        }
    }
}
