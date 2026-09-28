package tp5.reporte;

public class Reporte {

    public static void titulo(String texto) {
        System.out.println("=== " + texto + " ===");
    }

    public static void linea(String texto) {
        System.out.println(texto);
    }

    public static void valor(String etiqueta, double valor) {
        System.out.printf("  %-20s %10.4f%n", etiqueta, valor);
    }
}
