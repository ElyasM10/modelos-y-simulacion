package tp6.consola;

import java.io.File;

// TP6 - versión consola: corre los 3 ejercicios, imprime resultados y guarda los gráficos como PNG.
public class ModoConsola {

    static final String CARPETA = "graficos"; // acá se guardan los PNG

    public static void ejecutar() throws Exception {
        System.setProperty("java.awt.headless", "true"); // la consola solo guarda PNG: que Java no abra ventanas
        new File(CARPETA).mkdirs();
        Ejercicio1Consola.ejecutar(CARPETA);
        Ejercicio2Consola.ejecutar(CARPETA);
        Ejercicio3Consola.ejecutar(CARPETA);
        System.out.println("\nGráficos guardados en la carpeta " + CARPETA + "/");
    }
}
