package tp6.consola;

import java.io.File;

// TP6 - versión consola: corre los 3 ejercicios, imprime resultados y guarda los gráficos como PNG.
public class ModoConsola {

    static final String CARPETA = "graficos"; // acá se guardan los PNG

    public static void ejecutar() throws Exception {
        // Paso 1: modo "headless" (sin pantalla). Esta versión solo guarda imágenes, así que se evita que Java abra ventanas.
        // Tiene que ir antes de usar cualquier clase gráfica.
        System.setProperty("java.awt.headless", "true"); // la consola solo guarda PNG: que Java no abra ventanas
        // Paso 2: se crea la carpeta de salida (si ya existe no pasa nada)
        new File(CARPETA).mkdirs();
        // Paso 3: se corre cada ejercicio por separado; cada uno imprime sus resultados y guarda sus imágenes en la carpeta
        Ejercicio1Consola.ejecutar(CARPETA);
        Ejercicio2Consola.ejecutar(CARPETA);
        Ejercicio3Consola.ejecutar(CARPETA);
        System.out.println("\nGráficos guardados en la carpeta " + CARPETA + "/");
    }
}
