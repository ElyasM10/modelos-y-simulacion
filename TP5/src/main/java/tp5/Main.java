package tp5;

import tp5.ejercicios.Ejercicio1;
import tp5.ejercicios.Ejercicio2;
import tp5.ejercicios.Ejercicio3;
import tp5.ejercicios.Ejercicio4;
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
        Ejercicio4.ejecutar();
        Ejercicio5.ejecutar();

        System.out.println();
        System.out.println("========================================");
        System.out.println("Los 5 ejercicios fueron resueltos.");
        System.out.println("========================================");

        VentanaGrafico.avisarFinalizacion();
    }

}
