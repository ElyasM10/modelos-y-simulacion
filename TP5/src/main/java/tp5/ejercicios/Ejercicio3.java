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

// EJERCICIO 3: Decaimiento Radiactivo — dN/dt = -λ·N(t)
// Solución: N(t) = N0·e^(-λ·t)
public class Ejercicio3 {

    public static void ejecutar() {

        Reporte.titulo("Ejercicio 3: Decaimiento Radiactivo");

        double lambda = 0.1;  // constante de desintegración: cuanto mayor, más rápido decae la sustancia
        double n0 = 100;      // cantidad inicial de sustancia radiactiva en t=0

        Reporte.linea("N(t) = N0*e^(-λ*t)   con λ=" + lambda + ", N0=" + n0);
        // Vida media = ln(2)/λ: tiempo en que la cantidad se reduce a la mitad (N0/2)
        Reporte.valor("Vida media (ln2/λ)", ModelosExponenciales.vidaMedia(lambda));
        // Cantidad restante a los 10 y a los 30
        Reporte.valor("N(10)", ModelosExponenciales.decaimientoRadiactivo(lambda, n0, 10));
        Reporte.valor("N(30)", ModelosExponenciales.decaimientoRadiactivo(lambda, n0, 30));
        System.out.println();

        List<Curva> curvas = Arrays.asList(
                new Curva(t -> ModelosExponenciales.decaimientoRadiactivo(lambda, n0, t), "N(t)", new Color(60, 90, 200))
        );

        // Eje t de 0 a 40
        GraficoFuncion grafico = new GraficoFuncion(
                "Ejercicio 3 - Decaimiento Radiactivo", 0, 40, curvas, "t", "N"
        );

        VentanaGrafico.guardar(grafico, "Ejercicio3_Decaimiento");

        // (a) N0 fija (100), se varía λ: cuanto mayor λ, más rápido decae (menor vida media).
        double[] constantesLambda = {0.05, 0.1, 0.2, 0.4};
        GraficosComparativos.graficarFamilia("Ejercicio 3 - N0 fija, variando λ", 40, constantesLambda, "λ",
                (ll, t) -> ModelosExponenciales.decaimientoRadiactivo(ll, n0, t), "N",
                "Ejercicio3_VariandoLambda");
        for (double ll : constantesLambda) {
            Reporte.valor("Vida media λ=" + ll, ModelosExponenciales.vidaMedia(ll));
        }

        // (b) λ fija (0.1), se varía la cantidad inicial: misma vida media para todas, solo cambia la escala.
        double[] cantidadesIniciales = {25, 50, 100, 200};
        GraficosComparativos.graficarFamilia("Ejercicio 3 - λ fija, variando N0", 40, cantidadesIniciales, "N0",
                (nn, t) -> ModelosExponenciales.decaimientoRadiactivo(lambda, nn, t), "N",
                "Ejercicio3_VariandoN0");
        System.out.println();
    }

}
