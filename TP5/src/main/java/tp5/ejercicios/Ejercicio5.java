package tp5.ejercicios;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import tp5.reporte.Reporte;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

/*
  EJERCICIO 5: Modelo Depredador-Presa (Lotka-Volterra)

      dx/dt = α·x − β·x·y        x = presas
      dy/dt = δ·x·y − γ·y        y = depredadores

   Estas ecuaciones no tienen una fórmula que dé x(t) e y(t), así que se calculan "paso a paso":
   desde la situación inicial se avanza un instante chico, se obtiene la nueva situación, y se repite.
  Eso lo hace un integrador de Runge-Kutta de orden 4 que ya trae la librería Apache Commons Math.
 */
public class Ejercicio5 {

    public static void ejecutar() {

        Reporte.titulo("Ejercicio 5: Depredador-Presa (Lotka-Volterra)");

        // 1) Datos del problema (valores de ejemplo)
        final double alfa = 1.0;     // α: cuánto crecen las presas por sí solas
        final double beta = 0.1;     // β: cuántas presas se pierden por cada encuentro con un depredador
        final double delta = 0.075;  // δ: cuántos depredadores nuevos nacen por cada presa comida
        final double gama = 1.5;     // γ: cuántos depredadores mueren (si no hay presas para comer)

        double[] estado = {10, 5};   // situación inicial: 10 presas y 5 depredadores
        double tiempoFinal = 60;

        // 2) Las ecuaciones: dado cómo están hoy las poblaciones, ¿cuánto está cambiando cada una? */
        FirstOrderDifferentialEquations ecuaciones = new FirstOrderDifferentialEquations() {

            public int getDimension() {
                return 2;   // dos incógnitas: presas y depredadores
            }

            public void computeDerivatives(double t, double[] y, double[] cambio) {
                double presas = y[0];
                double depredadores = y[1];
                cambio[0] = alfa * presas - beta * presas * depredadores;        // dx/dt
                cambio[1] = delta * presas * depredadores - gama * depredadores; // dy/dt
            }
        };

        // 3) Punto de equilibrio: las poblaciones donde nada cambia (dx/dt = 0 y dy/dt = 0)
        double presasEquilibrio = gama / delta;
        double depredadoresEquilibrio = alfa / beta;
        Reporte.linea("Equilibrio de coexistencia: presas = γ/δ, depredadores = α/β");
        Reporte.valor("presas", presasEquilibrio);
        Reporte.valor("depredadores", depredadoresEquilibrio);

        /*
          4) Simulación: se avanza de a 0.05 unidades de tiempo hasta llegar a 60,
              anotando en cada paso cuántas presas y depredadores hay.
         */
        ClassicalRungeKuttaIntegrator integrador = new ClassicalRungeKuttaIntegrator(0.01);

        XYSeries presas = new XYSeries("Presas");
        XYSeries depredadores = new XYSeries("Depredadores");
        XYSeries orbita = new XYSeries("Órbita", false);   // false = respetar el orden de los puntos

        presas.add(0, estado[0]);
        depredadores.add(0, estado[1]);
        orbita.add(estado[0], estado[1]);

        double paso = 0.05;
        int cantidadDePasos = (int) Math.round(tiempoFinal / paso);

        for (int i = 1; i <= cantidadDePasos; i++) {

            // avanza el estado de (i-1)*paso a i*paso; deja el resultado en el mismo arreglo "estado"
            integrador.integrate(ecuaciones, (i - 1) * paso, estado, i * paso, estado);

            double t = i * paso;
            presas.add(t, estado[0]);
            depredadores.add(t, estado[1]);
            orbita.add(estado[0], estado[1]);
        }

        Reporte.valor("presas a t=60", estado[0]);
        Reporte.valor("depredadores a t=60", estado[1]);
        System.out.println();

        // Gráficos

        // Poblaciones a lo largo del tiempo (dos curvas)
        XYSeriesCollection poblaciones = new XYSeriesCollection();
        poblaciones.addSeries(presas);
        poblaciones.addSeries(depredadores);
        JFreeChart graficoPoblaciones = ChartFactory.createXYLineChart(
                "Ejercicio 5 - Poblaciones vs. tiempo", "tiempo", "población", poblaciones);
        graficoPoblaciones.getXYPlot().getRenderer().setSeriesPaint(0, new Color(50, 130, 60));   /* presas: verde */
        graficoPoblaciones.getXYPlot().getRenderer().setSeriesPaint(1, new Color(200, 60, 40));   /* depredadores: rojo */
        guardar(graficoPoblaciones, "Ejercicio5_Poblaciones");

        /*
          Retrato de fase: depredadores en función de presas (sin mostrar el tiempo).
          Da una curva cerrada alrededor del equilibrio: las poblaciones oscilan para siempre.
         */
        guardar(ChartFactory.createXYLineChart(
                "Ejercicio 5 - Retrato de fase (equilibrio en " + presasEquilibrio + " presas, "
                        + depredadoresEquilibrio + " depredadores)",
                "presas", "depredadores", new XYSeriesCollection(orbita)),
                "Ejercicio5_RetratoFase");
    }

    // Guarda un gráfico como PNG dentro de la carpeta graficos/
    private static void guardar(JFreeChart grafico, String nombreArchivo) {

        grafico.getXYPlot().setBackgroundPaint(Color.WHITE);   /* fondo blanco (por defecto es gris) */
        grafico.getXYPlot().setDomainGridlinePaint(Color.LIGHT_GRAY);
        grafico.getXYPlot().setRangeGridlinePaint(Color.LIGHT_GRAY);

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
