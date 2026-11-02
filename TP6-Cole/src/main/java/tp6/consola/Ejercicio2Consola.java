package tp6.consola;

import tp6.modelo.Cole;

// Ejercicio 2: los 5 tejidos de la Tabla 1 en un mismo espectro (módulo) y en el plano complejo (Nyquist).
public class Ejercicio2Consola {

    public static void ejecutar(String carpeta) throws Exception {
        System.out.println("\n=== Ejercicio 2: los 5 tejidos ===");
        // Paso 1: las mismas 300 frecuencias (100 Hz a 100 MHz) para todos los tejidos, así las curvas son comparables
        double[] frecuencias = Cole.frecuencias(100, 100e6, 300);
        // Paso 2: dos gráficos donde van a ir las 5 curvas: el módulo contra frecuencia y el Nyquist
        GraficoPng graficoModulo = new GraficoPng("Modelo de Cole: 5 tejidos en un mismo espectro", "Frecuencia", "|Z| [Ω]", true);
        GraficoPng graficoNyquist = new GraficoPng("Diagrama de Nyquist de los 5 tejidos", "Re(Z) [Ω]", "−Im(Z) [Ω]", false);
        int indiceColor = 0;
        // Paso 3: se recorre cada tejido de la Tabla 1 (nombre -> {R0, Rinf, fc, alpha})
        for (var tejido : Cole.TEJIDOS.entrySet()) {
            double[] parametros = tejido.getValue();
            // Se calcula Z en cada frecuencia con los parámetros de este tejido
            double[] modulos = new double[frecuencias.length];
            double[] partesReales = new double[frecuencias.length];
            double[] partesImaginarias = new double[frecuencias.length];
            for (int i = 0; i < frecuencias.length; i++) {
                double[] punto = Cole.modelo(frecuencias[i], parametros[0], parametros[1], parametros[2], parametros[3]);
                modulos[i] = Cole.modulo(punto);
                partesReales[i] = punto[0];
                partesImaginarias[i] = -punto[1];
            }
            // Se agrega la curva a cada gráfico con un color distinto por tejido
            graficoModulo.agregar(tejido.getKey(), frecuencias, modulos, GraficoPng.COLORES[indiceColor], false);
            graficoNyquist.agregar(tejido.getKey(), partesReales, partesImaginarias, GraficoPng.COLORES[indiceColor], false);
            indiceColor++;
            // Se imprime el módulo en el primer y en el último punto (100 Hz y 100 MHz) para ver de dónde a dónde baja
            System.out.printf("%-15s |Z| en 100 Hz = %.1f, en 100 MHz = %.1f%n",
                    tejido.getKey(), modulos[0], modulos[modulos.length - 1]);
        }
        // Paso 4: se guardan los dos gráficos
        graficoModulo.guardarPng(carpeta + "/ej2_tejidos.png", 1280, 672);
        graficoNyquist.guardarPng(carpeta + "/ej2_nyquist.png", 1280, 672);
    }
}
