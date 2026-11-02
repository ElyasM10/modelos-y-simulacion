package tp6.consola;

import tp6.modelo.Cole;

// Ejercicio 3: ajuste del modelo a los registros de Dell'Osa et al.
public class Ejercicio3Consola {

    public static void ejecutar(String carpeta) throws Exception {
        System.out.println("\n=== Ejercicio 3: ajuste a datos de Dell'Osa et al. ===");
        // Una fila de la tabla de parámetros por cada registro; se llena en el bucle y se dibuja al final
        java.util.List<String[]> filasTabla = new java.util.ArrayList<>();
        // Se repite lo mismo para cada uno de los 4 registros (nombre -> archivo CSV)
        for (var registro : Cole.REGISTROS.entrySet()) {
            // Paso 1: se leen los datos medidos. datos[0] = frecuencias, datos[1] = parte real, datos[2] = parte imaginaria
            double[][] datos = Cole.leerCsv(registro.getValue());
            double[] frecuenciasMedidas = datos[0];
            int cantidadPuntos = frecuenciasMedidas.length;
            // Paso 2: se ajusta el modelo (método de Ayllon). Devuelve {R0, Rinf, fc, alpha}
            double[] parametros = Cole.ajustar(frecuenciasMedidas, datos[1], datos[2]);

            // Paso 3: error relativo RMS: qué tanto se aparta el modelo de la medición, en porcentaje.
            // Es la raíz del promedio de (|Z medido - Z del modelo| / |Z medido|)^2 sobre todos los puntos
            double sumaCuadrados = 0;
            for (int i = 0; i < cantidadPuntos; i++) {
                double[] ajustada = Cole.modelo(frecuenciasMedidas[i], parametros[0], parametros[1], parametros[2], parametros[3]);
                double diferencia = Math.hypot(ajustada[0] - datos[1][i], ajustada[1] - datos[2][i]);
                double moduloMedido = Math.hypot(datos[1][i], datos[2][i]);
                sumaCuadrados += (diferencia / moduloMedido) * (diferencia / moduloMedido);
            }
            double errorRelativo = 100 * Math.sqrt(sumaCuadrados / cantidadPuntos);

            // Paso 4: se imprimen los valores característicos obtenidos
            System.out.printf("%-9s R0 = %7.2f ohm  Rinf = %7.2f ohm  fc = %10.1f Hz  alpha = %.3f  error rel. RMS = %.2f %%%n",
                    registro.getKey(), parametros[0], parametros[1], parametros[2], parametros[3], errorRelativo);

            // Paso 5: datos medidos para dibujar (módulo, fase y -Im) y curva del modelo ajustado.
            // La curva se calcula en frecuencias más separadas que las medidas (de fmin/5 a fmax*5) para que se vea el arco completo
            double[] modulosMedidos = new double[cantidadPuntos];
            double[] fasesMedidas = new double[cantidadPuntos];
            double[] imaginariasMedidas = new double[cantidadPuntos];
            for (int i = 0; i < cantidadPuntos; i++) {
                modulosMedidos[i] = Math.hypot(datos[1][i], datos[2][i]);
                fasesMedidas[i] = Math.toDegrees(Math.atan2(datos[2][i], datos[1][i]));
                imaginariasMedidas[i] = -datos[2][i];
            }
            double frecuenciaMinima = frecuenciasMedidas[0], frecuenciaMaxima = frecuenciasMedidas[cantidadPuntos - 1];
            double[] frecuenciasCurva = Cole.frecuencias(frecuenciaMinima / 5, frecuenciaMaxima * 5, 800);
            double[] modulosCurva = new double[frecuenciasCurva.length];
            double[] fasesCurva = new double[frecuenciasCurva.length];
            double[] realesCurva = new double[frecuenciasCurva.length];
            double[] imaginariasCurva = new double[frecuenciasCurva.length];
            for (int i = 0; i < frecuenciasCurva.length; i++) {
                double[] punto = Cole.modelo(frecuenciasCurva[i], parametros[0], parametros[1], parametros[2], parametros[3]);
                modulosCurva[i] = Cole.modulo(punto);
                fasesCurva[i] = Math.toDegrees(Math.atan2(punto[1], punto[0]));
                realesCurva[i] = punto[0];
                imaginariasCurva[i] = -punto[1];
            }

            // Paso 6: tres gráficos. Medición en rojo (solo puntos) y ajuste en verde (línea).
            // En módulo y fase el eje X se limita al rango medido; lo que la curva tiene de más queda recortado
            GraficoPng graficoNyquist = new GraficoPng("Nyquist", "Re(Z) [Ω]", "−Im(Z) [Ω]", false);
            graficoNyquist.agregar("Medición", datos[1], imaginariasMedidas, GraficoPng.COLORES[0], true);
            graficoNyquist.agregar("Ajuste (Ayllon)", realesCurva, imaginariasCurva, GraficoPng.COLORES[1], false);
            graficoNyquist.leyendaAbajoCentro(); // el arco deja libre la parte de abajo
            GraficoPng graficoModulo = new GraficoPng("Módulo", "Frecuencia", "|Z| [Ω]", true);
            graficoModulo.agregar("Medición", frecuenciasMedidas, modulosMedidos, GraficoPng.COLORES[0], true);
            graficoModulo.agregar("Ajuste", frecuenciasCurva, modulosCurva, GraficoPng.COLORES[1], false);
            graficoModulo.fijarLimitesX(frecuenciaMinima, frecuenciaMaxima);
            graficoModulo.sinLeyenda();
            GraficoPng graficoFase = new GraficoPng("Fase", "Frecuencia", "Fase [°]", true);
            graficoFase.agregar("Medición", frecuenciasMedidas, fasesMedidas, GraficoPng.COLORES[0], true);
            graficoFase.agregar("Ajuste", frecuenciasCurva, fasesCurva, GraficoPng.COLORES[1], false);
            graficoFase.fijarLimitesX(frecuenciaMinima, frecuenciaMaxima);
            graficoFase.sinLeyenda();

            // Paso 7: los parámetros de este registro se guardan para armar, al final, una tabla con los cuatro registros
            filasTabla.add(new String[]{registro.getKey(), String.format("%.2f", parametros[0]), String.format("%.2f", parametros[1]),
                    String.format("%.1f", parametros[2]), String.format("%.3f", parametros[3]),
                    String.format("%.2f", 1e6 / (2 * Math.PI * parametros[2])), String.format("%.2f %%", errorRelativo)});

            // Paso 8: una imagen por registro, solo con gráficos, para que se vean grandes. Dos filas:
            // arriba el Nyquist ocupando todo el ancho y abajo el módulo y la fase, uno al lado del otro
            LaminaPng.guardarFilas(carpeta + "/ej3_" + registro.getKey().replace(' ', '_') + ".png", 1296, 1200, registro.getKey(),
                    new PanelPng[]{graficoNyquist}, new PanelPng[]{graficoModulo, graficoFase});
        }

        // Paso 9: la tabla con los parámetros de los cuatro registros, en una imagen aparte de los gráficos
        TablaPng tabla = new TablaPng("Parámetros obtenidos (método de Ayllon)",
                new String[]{"Registro", "R0 [Ω]", "R∞ [Ω]", "fc [Hz]", "α", "τ [µs]", "Error RMS"},
                filasTabla.toArray(new String[0][]));
        LaminaPng.guardarFilas(carpeta + "/ej3_parametros.png", 1296, 420, null, new PanelPng[]{tabla});
    }
}
