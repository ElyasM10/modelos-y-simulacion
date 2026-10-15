package tp6.grafico;

import org.apache.commons.math3.complex.Complex;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import tp6.modelo.ModeloCole;
import tp6.modelo.Tejido;

import java.util.List;

// Arma los gráficos del TP (los usan tanto la consola como la interfaz)
public class ConstructorGraficos {

    // Módulo, fase y Nyquist de uno o más tejidos en el mismo espectro [fMin, fMax] (en ese orden)
    public static JFreeChart[] espectro(String titulo, List<Tejido> tejidos, double fMin, double fMax) {

        int puntos = 300;   // frecuencias equiespaciadas en escala logarítmica
        XYSeriesCollection modulo = new XYSeriesCollection();
        XYSeriesCollection fase = new XYSeriesCollection();
        XYSeriesCollection nyquist = new XYSeriesCollection();

        for (Tejido t : tejidos) {
            XYSeries sModulo = new XYSeries(t.getNombre());
            XYSeries sFase = new XYSeries(t.getNombre());
            XYSeries sNyquist = new XYSeries(t.getNombre(), false);

            for (int i = 0; i < puntos; i++) {
                double f = fMin * Math.pow(fMax / fMin, i / (puntos - 1.0));
                Complex z = t.getModelo().impedancia(f);
                sModulo.add(f, z.abs());
                sFase.add(f, Math.toDegrees(z.getArgument()));
                sNyquist.add(z.getReal(), -z.getImaginary());
            }
            modulo.addSeries(sModulo);
            fase.addSeries(sFase);
            nyquist.addSeries(sNyquist);
        }

        return new JFreeChart[]{
                Graficos.lineas(titulo + " - Módulo de la impedancia", "frecuencia [Hz]", "|Z| [Ω]", modulo, true),
                Graficos.lineas(titulo + " - Fase de la impedancia", "frecuencia [Hz]", "fase [grados]", fase, true),
                Graficos.lineas(titulo + " - Diagrama de Nyquist", "Re(Z) [Ω]", "-Im(Z) [Ω]", nyquist, false)
        };
    }

    // Medición (puntos) contra modelo ajustado (línea): Nyquist, módulo y fase (en ese orden)
    public static JFreeChart[] ajuste(String descripcion, double[] f, double[] re, double[] im, ModeloCole m) {

        XYSeriesCollection nyquist = new XYSeriesCollection();
        XYSeriesCollection modulo = new XYSeriesCollection();
        XYSeriesCollection fase = new XYSeriesCollection();

        XYSeries[] medido = {new XYSeries("Medición", false), new XYSeries("Medición"), new XYSeries("Medición")};
        XYSeries[] ajustado = {new XYSeries("Ajuste de Cole", false), new XYSeries("Ajuste de Cole"),
                new XYSeries("Ajuste de Cole")};

        for (int i = 0; i < f.length; i++) {
            Complex z = m.impedancia(f[i]);
            medido[0].add(re[i], -im[i]);
            ajustado[0].add(z.getReal(), -z.getImaginary());
            medido[1].add(f[i], Math.hypot(re[i], im[i]));
            ajustado[1].add(f[i], z.abs());
            medido[2].add(f[i], Math.toDegrees(Math.atan2(im[i], re[i])));
            ajustado[2].add(f[i], Math.toDegrees(z.getArgument()));
        }
        XYSeriesCollection[] colecciones = {nyquist, modulo, fase};
        for (int k = 0; k < 3; k++) {
            colecciones[k].addSeries(medido[k]);
            colecciones[k].addSeries(ajustado[k]);
        }

        return new JFreeChart[]{
                Graficos.medicionYAjuste("Nyquist - " + descripcion, "Re(Z) [Ω]", "-Im(Z) [Ω]", nyquist, false),
                Graficos.medicionYAjuste("Módulo - " + descripcion, "frecuencia [Hz]", "|Z| [Ω]", modulo, true),
                Graficos.medicionYAjuste("Fase - " + descripcion, "frecuencia [Hz]", "fase [grados]", fase, true)
        };
    }
}
