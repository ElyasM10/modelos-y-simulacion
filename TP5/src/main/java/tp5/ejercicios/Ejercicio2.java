package tp5.ejercicios;

import tp5.grafico.Curva;
import tp5.grafico.GraficoFuncion;
import tp5.grafico.GraficosComparativos;
import tp5.grafico.VentanaGrafico;
import tp5.modelos.ModelosExponenciales;
import tp5.reporte.Reporte;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

// EJERCICIO 2: Crecimiento Poblacional (Modelo de Malthus) — dP/dt = r·P(t)
// Solución: P(t) = P0·e^(r·t)
public class Ejercicio2 {

    public static void ejecutar() {

        Reporte.titulo("Ejercicio 2: Crecimiento Poblacional (Malthus)");

        double r = 0.08;    // tasa de crecimiento: r > 0 la población crece, r < 0 decrece
        double p0 = 100;    // población inicial en t=0

        Reporte.linea("P(t) = P0*e^(r*t)   con r=" + r + ", P0=" + p0);
        // Población a los 10 y a los 30 (crece exponencialmente, sin límite de recursos)
        Reporte.valor("P(10)", ModelosExponenciales.malthus(r, p0, 10));
        Reporte.valor("P(30)", ModelosExponenciales.malthus(r, p0, 30));
        System.out.println();

        List<Curva> curvas = Arrays.asList(
                new Curva(t -> ModelosExponenciales.malthus(r, p0, t), "P(t)", new Color(50, 130, 60))
        );

        // Eje t de 0 a 30
        GraficoFuncion grafico = new GraficoFuncion(
                "Ejercicio 2 - Crecimiento de Malthus", 0, 30, curvas, "t", "P"
        );

        VentanaGrafico.guardar(grafico, "Ejercicio2_Malthus");

        // (a) P0 fija (100), se varía la tasa r: r<0 la población decrece, r>0 crece, y más rápido cuanto mayor r.
        double[] tasas = {-0.05, 0.02, 0.05, 0.08};
        GraficosComparativos.graficarFamilia("Ejercicio 2 - P0 fija, variando r", 30, tasas, "r",
                (rr, t) -> ModelosExponenciales.malthus(rr, p0, t), "P",
                "Ejercicio2_VariandoR");

        // (b) r fija (0.08), se varía la población inicial: la forma de la curva es la misma,
        //     solo se escala verticalmente (el tiempo de duplicación ln2/r no depende de P0).
        double[] poblacionesIniciales = {50, 100, 200, 400};
        GraficosComparativos.graficarFamilia("Ejercicio 2 - r fija, variando P0", 30, poblacionesIniciales, "P0",
                (pp, t) -> ModelosExponenciales.malthus(r, pp, t), "P",
                "Ejercicio2_VariandoP0");
        System.out.println();
    }

}
