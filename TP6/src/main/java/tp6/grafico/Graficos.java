package tp6.grafico;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.text.DecimalFormat;
import java.io.IOException;

// Utilidades para armar gráficos con JFreeChart y guardarlos como PNG en la carpeta graficos/
public class Graficos {

    // colores de las curvas (se reparten en orden)
    public static final Color[] COLORES = {
            new Color(200, 60, 40), new Color(50, 130, 60), new Color(40, 80, 190),
            new Color(140, 70, 170), new Color(230, 140, 20)
    };

    // Gráfico de líneas; si logX es true el eje horizontal (frecuencia) es logarítmico
    public static JFreeChart lineas(String titulo, String ejeX, String ejeY, XYSeriesCollection datos, boolean logX) {

        JFreeChart grafico = ChartFactory.createXYLineChart(titulo, ejeX, ejeY, datos);
        XYPlot plot = grafico.getXYPlot();

        if (logX) {
            LogAxis eje = new LogAxis(ejeX);
            eje.setNumberFormatOverride(new DecimalFormat("0E0"));   // 1E3, 1E4, ... en vez de 10^3.0
            plot.setDomainAxis(eje);
        }
        for (int i = 0; i < datos.getSeriesCount(); i++) {
            plot.getRenderer().setSeriesPaint(i, COLORES[i % COLORES.length]);
            plot.getRenderer().setSeriesStroke(i, new BasicStroke(2f));
        }
        estilo(grafico);
        return grafico;
    }

    // Igual que lineas(), pero la serie 0 (las mediciones) se dibuja como puntos y la serie 1 (el ajuste) como línea
    public static JFreeChart medicionYAjuste(String titulo, String ejeX, String ejeY, XYSeriesCollection datos, boolean logX) {

        JFreeChart grafico = lineas(titulo, ejeX, ejeY, datos, logX);
        XYPlot plot = grafico.getXYPlot();

        XYLineAndShapeRenderer render = new XYLineAndShapeRenderer(true, true);
        render.setSeriesLinesVisible(0, false);
        render.setSeriesShape(0, new Ellipse2D.Double(-2.5, -2.5, 5, 5));
        render.setSeriesPaint(0, Color.GRAY);
        render.setSeriesShapesVisible(1, false);
        render.setSeriesPaint(1, COLORES[0]);
        render.setSeriesStroke(1, new BasicStroke(2.5f));
        plot.setRenderer(render);
        return grafico;
    }

    // Fondo blanco (por defecto es gris) y grilla suave
    public static void estilo(JFreeChart grafico) {
        XYPlot plot = grafico.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
    }

    public static void guardar(JFreeChart grafico, String nombreArchivo) {

        estilo(grafico);

        File archivo = new File("graficos", nombreArchivo + ".png");
        archivo.getParentFile().mkdirs();

        try {
            ChartUtils.saveChartAsPNG(archivo, grafico, 800, 500);
            System.out.println("Imagen guardada: " + archivo.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("No se pudo guardar " + nombreArchivo + ": " + e.getMessage());
        }
    }
}
