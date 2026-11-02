package tp6.consola;

import tp6.modelo.Cole;

// Ejercicio 1: el modelo de Cole evaluado en una frecuencia puntual, más su módulo, su fase y su Nyquist.
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

        // Paso 4: curva de Cole de 100 Hz a 100 MHz. Se evalúa el modelo en 300 frecuencias y se guarda, para cada una,
        // el módulo y la fase (gráficos 1 y 2) y las partes real e imaginaria (gráfico 3, el Nyquist)
        double[] frecuencias = Cole.frecuencias(100, 100e6, 300);
        double[] modulos = new double[frecuencias.length];
        double[] fases = new double[frecuencias.length];
        double[] partesReales = new double[frecuencias.length];
        double[] partesImaginarias = new double[frecuencias.length];
        for (int i = 0; i < frecuencias.length; i++) {
            double[] punto = Cole.modelo(frecuencias[i], parametros[0], parametros[1], parametros[2], parametros[3]);
            modulos[i] = Cole.modulo(punto);
            fases[i] = Math.toDegrees(Math.atan2(punto[1], punto[0]));
            partesReales[i] = punto[0];
            // El Nyquist se dibuja con -Im(Z) para que el arco quede hacia arriba (Im(Z) es negativa)
            partesImaginarias[i] = -punto[1];
        }
        String textoFc = "fc = " + GraficoPng.formatoFrecuencia(parametros[2]);

        // Paso 5: una sola imagen con dos gráficos, módulo y fase contra frecuencia (eje X logarítmico), con una línea punteada en fc
        GraficoPng graficoModulo = new GraficoPng("Módulo", "Frecuencia", "|Z| [Ω]", true);
        graficoModulo.agregar("Cole", frecuencias, modulos, GraficoPng.COLORES[2], false);
        graficoModulo.agregarLineaVertical(parametros[2], textoFc);
        graficoModulo.sinLeyenda();
        GraficoPng graficoFase = new GraficoPng("Fase", "Frecuencia", "Fase [°]", true);
        graficoFase.agregar("Cole", frecuencias, fases, GraficoPng.COLORES[0], false);
        graficoFase.agregarLineaVertical(parametros[2], textoFc);
        graficoFase.sinLeyenda();
        LaminaPng.guardar(carpeta + "/ej1_modulo_fase.png", 1440, 544, null, 2, graficoModulo, graficoFase);

        // Paso 6: diagrama de Nyquist (-Im contra Re, eje X lineal). Se marcan R0 (f -> 0), R∞ (f -> ∞) y el punto f = fc
        GraficoPng graficoNyquist = new GraficoPng("Diagrama de Nyquist (Hígado)", "Re(Z) [Ω]", "−Im(Z) [Ω]", false);
        graficoNyquist.agregar("Cole", partesReales, partesImaginarias, GraficoPng.COLORES[2], false);
        graficoNyquist.sinLeyenda();
        double[] enFc = Cole.modelo(parametros[2], parametros[0], parametros[1], parametros[2], parametros[3]);
        graficoNyquist.agregarPunto(enFc[0], -enFc[1], String.format("f = fc   %.0f − %.0fj", enFc[0], -enFc[1]), GraficoPng.COLORES[0], 12, -12);
        graficoNyquist.agregarPunto(parametros[0], 0, "R0 (f → 0)", GraficoPng.COLORES[1], -12, 36);
        graficoNyquist.agregarPunto(parametros[1], 0, "R∞ (f → ∞)", GraficoPng.COLORES[1], 12, 36);
        graficoNyquist.fijarLimitesY(-18, 98);
        graficoNyquist.guardarPng(carpeta + "/ej1_nyquist.png", 960, 544);
    }
}
