package tp6.consola;

import tp6.modelo.Cole;

// Ejercicio 3: ajuste del modelo a los registros de Dell'Osa et al.
public class Ejercicio3Consola {

    public static void ejecutar(String carpeta) throws Exception {
        System.out.println("\n=== Ejercicio 3: ajuste a datos de Dell'Osa et al. ===");
        // Se repite lo mismo para cada uno de los 4 registros (nombre -> archivo CSV)
        for (var registro : Cole.REGISTROS.entrySet()) {
            // Paso 1: se leen los datos medidos. datos[0] = frecuencias, datos[1] = parte real, datos[2] = parte imaginaria
            double[][] datos = Cole.leerCsv(registro.getValue());
            // Paso 2: se ajusta el modelo (método de Ayllon). Devuelve {R0, Rinf, fc, alpha}
            double[] parametros = Cole.ajustar(datos[0], datos[1], datos[2]);
            // Paso 3: se imprimen los valores característicos obtenidos
            System.out.printf("%-9s R0 = %7.2f ohm  Rinf = %7.2f ohm  fc = %10.1f Hz  alpha = %.3f%n",
                    registro.getKey(), parametros[0], parametros[1], parametros[2], parametros[3]);

            // Paso 4: Nyquist de la medición (puntos) contra el modelo ajustado (línea).
            // Para dibujar el modelo ajustado se evalúa Cole, con los parámetros obtenidos, en las mismas frecuencias medidas
            int cantidadPuntos = datos[0].length;
            double[] partesRealesAjuste = new double[cantidadPuntos];
            double[] partesImaginariasAjuste = new double[cantidadPuntos];
            double[] partesImaginariasMedidas = new double[cantidadPuntos];
            for (int i = 0; i < cantidadPuntos; i++) {
                double[] ajustada = Cole.modelo(datos[0][i], parametros[0], parametros[1], parametros[2], parametros[3]);
                partesRealesAjuste[i] = ajustada[0];
                // Se grafica -Im(Z): se cambia el signo tanto del modelo como de lo medido
                partesImaginariasAjuste[i] = -ajustada[1];
                partesImaginariasMedidas[i] = -datos[2][i];
            }
            // Paso 5: medición en rojo (solo puntos) y ajuste en verde (línea), y se guarda una imagen por registro
            GraficoPng grafico = new GraficoPng("Ajuste - " + registro.getKey(), "Re(Z) [ohm]", "-Im(Z) [ohm]", false);
            grafico.agregar("medición", datos[1], partesImaginariasMedidas, GraficoPng.COLORES[0], true);
            grafico.agregar("ajuste", partesRealesAjuste, partesImaginariasAjuste, GraficoPng.COLORES[1], false);
            grafico.guardarPng(carpeta + "/ej3_" + registro.getKey().replace(' ', '_') + ".png", 700, 500);
        }
    }
}
