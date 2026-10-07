package tp5;

import tp5.ejercicios.Ejercicio1;
import tp5.ejercicios.Ejercicio2;
import tp5.ejercicios.Ejercicio3;
import tp5.ejercicios.Ejercicio5;
import tp5.grafico.VentanaGrafico;

public class Main {

    public static void main(String[] args) {

        System.out.println();
        System.out.println("       TP5 - MODELOS Y SIMULACIÓN");
        System.out.println();

        Ejercicio1.ejecutar();
        Ejercicio2.ejecutar();
        Ejercicio3.ejecutar();
        Ejercicio5.ejecutar();

        System.out.println();
        System.out.println("========================================");
        System.out.println("Ejercicios 1, 2, 3 y 5 resueltos (el 4 se resolvió en Scilab/Xcos: ver carpeta scilab).");
        System.out.println("========================================");

        VentanaGrafico.avisarFinalizacion();
    }

}
