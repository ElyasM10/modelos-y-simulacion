package tp6.consola;

import tp6.modelo.Cole;

// Ejercicio 2: los 5 tejidos de la Tabla 1 en un mismo espectro.
public class Ejercicio2Consola {

    public static void ejecutar(String carpeta) throws Exception {
        System.out.println("\n=== Ejercicio 2: los 5 tejidos ===");
        // Paso 1: las mismas 300 frecuencias (100 Hz a 100 MHz) para todos los tejidos, así las curvas son comparables
        double[] frecuencias = Cole.frecuencias(100, 100e6, 300);
        // Paso 2: un solo gráfico donde van a ir las 5 curvas
        GraficoPng grafico = new GraficoPng("Modelo de Cole - 5 tejidos", "f [Hz] (log)", "|Z| [ohm]", true);
        int indiceColor = 0;
        // Paso 3: se recorre cada tejido de la Tabla 1 (nombre -> {R0, Rinf, fc, alpha})
        for (var tejido : Cole.TEJIDOS.entrySet()) {
            double[] parametros = tejido.getValue();
            // Se calcula el módulo de Z en cada frecuencia con los parámetros de este tejido
            double[] modulos = new double[frecuencias.length];
            for (int i = 0; i < frecuencias.length; i++)
                modulos[i] = Cole.modulo(Cole.modelo(frecuencias[i], parametros[0], parametros[1], parametros[2], parametros[3]));
            // Se agrega la curva al gráfico con un color distinto por tejido
            grafico.agregar(tejido.getKey(), frecuencias, modulos, GraficoPng.COLORES[indiceColor++], false);
            // Se imprime el módulo en el primer y en el último punto (100 Hz y 100 MHz) para ver de dónde a dónde baja
            System.out.printf("%-15s |Z| en 100 Hz = %.1f, en 100 MHz = %.1f%n",
                    tejido.getKey(), modulos[0], modulos[modulos.length - 1]);
        }
        // Paso 4: se guarda el gráfico con las 5 curvas
        grafico.guardarPng(carpeta + "/ej2_tejidos.png", 900, 600);
    }
}
