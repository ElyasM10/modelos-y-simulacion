package tp6.consola;

import tp6.modelo.Cole;

// Ejercicio 3: ajuste del modelo a los registros de Dell'Osa et al.
public class Ejercicio3Consola {

    public static void ejecutar(String carpeta) throws Exception {
        System.out.println("\n=== Ejercicio 3: ajuste a datos de Dell'Osa et al. ===");
        for (var registro : Cole.REGISTROS.entrySet()) {
            double[][] datos = Cole.leerCsv(registro.getValue());
            double[] parametros = Cole.ajustar(datos[0], datos[1], datos[2]);
            System.out.printf("%-9s R0 = %7.2f ohm  Rinf = %7.2f ohm  fc = %10.1f Hz  alpha = %.3f%n",
                    registro.getKey(), parametros[0], parametros[1], parametros[2], parametros[3]);

            // Nyquist de la medición (puntos) contra el modelo ajustado (línea)
            int cantidadPuntos = datos[0].length;
            double[] partesRealesAjuste = new double[cantidadPuntos];
            double[] partesImaginariasAjuste = new double[cantidadPuntos];
            double[] partesImaginariasMedidas = new double[cantidadPuntos];
            for (int i = 0; i < cantidadPuntos; i++) {
                double[] ajustada = Cole.modelo(datos[0][i], parametros[0], parametros[1], parametros[2], parametros[3]);
                partesRealesAjuste[i] = ajustada[0];
                partesImaginariasAjuste[i] = -ajustada[1];
                partesImaginariasMedidas[i] = -datos[2][i];
            }
            GraficoPng grafico = new GraficoPng("Ajuste - " + registro.getKey(), "Re(Z) [ohm]", "-Im(Z) [ohm]", false);
            grafico.agregar("medición", datos[1], partesImaginariasMedidas, GraficoPng.COLORES[0], true);
            grafico.agregar("ajuste", partesRealesAjuste, partesImaginariasAjuste, GraficoPng.COLORES[1], false);
            grafico.guardarPng(carpeta + "/ej3_" + registro.getKey().replace(' ', '_') + ".png", 700, 500);
        }
    }
}
