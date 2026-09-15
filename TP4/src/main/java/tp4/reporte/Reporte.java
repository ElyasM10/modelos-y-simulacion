package tp4.reporte;

import org.apache.commons.math3.linear.RealVector;

public class Reporte {

    public static void imprimirVector(String titulo, String[] sectores, RealVector valores) {

        System.out.println(titulo + ":");

        for (int i = 0; i < sectores.length; i++) {
            System.out.printf("  %-12s %10.2f%n", sectores[i], valores.getEntry(i));
        }
    }
}
