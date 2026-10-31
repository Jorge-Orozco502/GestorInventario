package modelo;

public class Sesion {
    private static Usuario usuarioActual;

    public static void iniciar(Usuario usuario) { usuarioActual = usuario; }
    public static Usuario getUsuarioActual()    { return usuarioActual; }
    public static void cerrar()                 { usuarioActual = null; }
}