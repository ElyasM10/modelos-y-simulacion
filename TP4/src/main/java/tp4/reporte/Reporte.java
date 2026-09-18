package tp4.reporte;

// import org.apache.commons.math3.linear.RealVector; // solo hace falta con la versión con librería (ver tp4.leontief.LeontiefApacheCommonsMath)

public class Reporte {

    public static void imprimirVector(String titulo, String[] sectores, double[] valores) {

        System.out.println(titulo + ":");

        for (int i = 0; i < sectores.length; i++) {
            System.out.printf("  %-12s %10.2f%n", sectores[i], valores[i]);
        }
    }

    // Versión equivalente para cuando se usa la librería (Leontief.produccion/aplicarInversa
    // devuelven RealVector en vez de double[]):
    //
    // public static void imprimirVector(String titulo, String[] sectores, RealVector valores) {
    //
    //     System.out.println(titulo + ":");
    //
    //     for (int i = 0; i < sectores.length; i++) {
    //         System.out.printf("  %-12s %10.2f%n", sectores[i], valores.getEntry(i));
    //     }
    // }
}
