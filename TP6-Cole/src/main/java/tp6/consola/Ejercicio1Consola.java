package tp6.consola;

import tp6.modelo.Cole;

// Ejercicio 1: el modelo de Cole evaluado en una frecuencia puntual, más su módulo y su Nyquist.
public class Ejercicio1Consola {

    public static void ejecutar(String carpeta) throws Exception {
        System.out.println("=== Ejercicio 1: modelo de Cole (Hígado, f = 10 kHz) ===");
        // Paso 1: se toman los parámetros del hígado de la Tabla 1: {R0, Rinf, fc, alpha}
        double[] parametros = Cole.TEJIDOS.get("Hígado");
        // Paso 2: se calcula la impedancia a 10 kHz. Devuelve {parte real, parte imaginaria}
        double[] impedancia = Cole.modelo(10e3, parametros[0], parametros[1], parametros[2], parametros[3]);
        // Paso 3: se imprime Z, su módulo y su fase (el ángulo se saca con atan2 y se pasa de radianes a grados)
        System.out.printf("Z = %.2f %+.2fj ohm   |Z| = %.2f   fase = %.2f°%n",
                impedancia[0], impedancia[1], Cole.modulo(impedancia),
                Math.toDegrees(Math.atan2(impedancia[1], impedancia[0])));

        // Paso 4: curva de Cole de 100 Hz a 100 MHz. Se evalúa el modelo en 300 frecuencias y se guarda,
        // para cada una, el módulo (gráfico 1) y las partes real e imaginaria (gráfico 2, el Nyquist)
        double[] frecuencias = Cole.frecuencias(100, 100e6, 300);
        double[] modulos = new double[frecuencias.length];
        double[] partesReales = new double[frecuencias.length];
        double[] partesImaginarias = new double[frecuencias.length];
        for (int i = 0; i < frecuencias.length; i++) {
            double[] punto = Cole.modelo(frecuencias[i], parametros[0], parametros[1], parametros[2], parametros[3]);
            modulos[i] = Cole.modulo(punto);
            partesReales[i] = punto[0];
            // El Nyquist se dibuja con -Im(Z) para que el arco quede hacia arriba (Im(Z) es negativa)
            partesImaginarias[i] = -punto[1];
        }

        // Paso 5: gráfico del módulo contra frecuencia (eje X logarítmico) y se guarda como imagen
        GraficoPng graficoModulo = new GraficoPng("Hígado - |Z| vs frecuencia", "f [Hz] (log)", "|Z| [ohm]", true);
        graficoModulo.agregar("Cole", frecuencias, modulos, GraficoPng.COLORES[2], false);
        graficoModulo.guardarPng(carpeta + "/ej1_modulo.png", 700, 500);

        // Paso 6: diagrama de Nyquist (-Im contra Re, eje X lineal) y se guarda como imagen
        GraficoPng graficoNyquist = new GraficoPng("Hígado - Nyquist", "Re(Z) [ohm]", "-Im(Z) [ohm]", false);
        graficoNyquist.agregar("Cole", partesReales, partesImaginarias, GraficoPng.COLORES[2], false);
        graficoNyquist.guardarPng(carpeta + "/ej1_nyquist.png", 700, 500);
    }
}
