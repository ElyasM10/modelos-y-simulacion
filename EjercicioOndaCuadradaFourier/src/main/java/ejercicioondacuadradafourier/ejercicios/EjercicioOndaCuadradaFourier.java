package ejercicioondacuadradafourier.ejercicios;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

/** Aproxima una onda cuadrada simétrica usando su serie de Fourier. */
public class EjercicioOndaCuadradaFourier {
    private static final double AMPLITUD = 1.0;
    private static final double PERIODO = 2.0 * Math.PI;
    private static final int MUESTRAS = 2000;

    /**
     * Suma los primeros nTerminos armónicos impares:
     * f(t) = (4A/pi) * sum sin(2*pi*(2k+1)*t/T)/(2k+1).
     */
    public static double aproximar(double t, int nTerminos) {
        if (nTerminos < 1) {
            throw new IllegalArgumentException("La cantidad de términos debe ser al menos 1.");
        }

        double suma = 0.0;
        for (int k = 0; k < nTerminos; k++) {
            int armonicoImpar = 2 * k + 1;
            suma += Math.sin(2.0 * Math.PI * armonicoImpar * t / PERIODO) / armonicoImpar;
        }
        return (4.0 * AMPLITUD / Math.PI) * suma;
    }

    /** Onda ideal: +A durante la primera mitad del período y -A durante la segunda. */
    public static double ondaIdeal(double t) {
        double fase = t % PERIODO;
        if (fase < 0) fase += PERIODO;
        return fase < PERIODO / 2.0 ? AMPLITUD : -AMPLITUD;
    }

    public static void ejecutar(int nTerminos) {
        if (nTerminos < 1) {
            throw new IllegalArgumentException("La cantidad de términos debe ser al menos 1.");
        }

        XYSeries ideal = new XYSeries("Onda cuadrada ideal");
        XYSeries aproximada = new XYSeries("Serie de Fourier (" + nTerminos + " términos)");
        double tiempoFinal = 2.0 * PERIODO;
        for (int i = 0; i <= MUESTRAS; i++) {
            double t = tiempoFinal * i / MUESTRAS;
            ideal.add(t, ondaIdeal(t));
            aproximada.add(t, aproximar(t, nTerminos));
        }

        XYSeriesCollection datos = new XYSeriesCollection();
        datos.addSeries(ideal);
        datos.addSeries(aproximada);
        JFreeChart grafico = ChartFactory.createXYLineChart(
                "Aproximación de onda cuadrada por Fourier",
                "Tiempo (s)", "Amplitud", datos);
        grafico.getXYPlot().getRenderer().setSeriesPaint(0, new Color(45, 95, 160));
        grafico.getXYPlot().getRenderer().setSeriesPaint(1, new Color(210, 75, 55));

        File carpeta = new File("graficos");
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            throw new IllegalStateException("No se pudo crear la carpeta graficos.");
        }
        File archivo = new File(carpeta, "OndaCuadradaFourier.png");
        try {
            ChartUtils.saveChartAsPNG(archivo, grafico, 1000, 600);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar el gráfico.", e);
        }

        System.out.println("Serie de Fourier de onda cuadrada");
        System.out.println("Amplitud: " + AMPLITUD + " | Período: 2π | Términos: " + nTerminos);
        System.out.println("Gráfico generado: " + archivo.getAbsolutePath());
        System.out.println("En los puntos de salto, la serie converge al valor medio (0).");
    }
}
