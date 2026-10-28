package tp6.consola;

import tp6.modelo.Cole;

// Ejercicio 2: los 5 tejidos de la Tabla 1 en un mismo espectro.
public class Ejercicio2Consola {

    public static void ejecutar(String carpeta) throws Exception {
        System.out.println("\n=== Ejercicio 2: los 5 tejidos ===");
        double[] frecuencias = Cole.frecuencias(100, 100e6, 300);
        GraficoPng grafico = new GraficoPng("Modelo de Cole - 5 tejidos", "f [Hz] (log)", "|Z| [ohm]", true);
        int indiceColor = 0;
        for (var tejido : Cole.TEJIDOS.entrySet()) {
            double[] parametros = tejido.getValue();
            double[] modulos = new double[frecuencias.length];
            for (int i = 0; i < frecuencias.length; i++)
                modulos[i] = Cole.modulo(Cole.modelo(frecuencias[i], parametros[0], parametros[1], parametros[2], parametros[3]));
            grafico.agregar(tejido.getKey(), frecuencias, modulos, GraficoPng.COLORES[indiceColor++], false);
            System.out.printf("%-15s |Z| en 100 Hz = %.1f, en 100 MHz = %.1f%n",
                    tejido.getKey(), modulos[0], modulos[modulos.length - 1]);
        }
        grafico.guardarPng(carpeta + "/ej2_tejidos.png", 900, 600);
    }
}
