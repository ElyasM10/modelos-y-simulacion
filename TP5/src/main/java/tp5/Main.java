package tp5;

import tp5.diagrama.DiagramaBloques;
import tp5.edo.LotkaVolterra;
import tp5.edo.RungeKutta4;
import tp5.grafico.Curva;
import tp5.grafico.GraficoFase;
import tp5.grafico.GraficoFuncion;
import tp5.grafico.VentanaGrafico;
import tp5.modelos.ModelosExponenciales;
import tp5.reporte.Reporte;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println();
        System.out.println("       TP5 - MODELOS Y SIMULACIÓN");
        System.out.println();

        ejercicio1();
        ejercicio2();
        ejercicio3();
        ejercicio4();
        ejercicio5();

        System.out.println();
        System.out.println("========================================");
        System.out.println("Los 5 ejercicios fueron resueltos.");
        System.out.println("========================================");
    }


    // EJERCICIO 1: Ley del Enfriamiento de Newton — dT/dt = -k·(T - Tamb)
    // Solución: T(t) = Tamb + (T0 - Tamb)·e^(-k·t)

    private static void ejercicio1() {

        Reporte.titulo("Ejercicio 1: Ley del Enfriamiento de Newton");

        double k = 0.05;
        double tAmb = 20;
        double t0 = 90;

        Reporte.linea("T(t) = Tamb + (T0 - Tamb)*e^(-k*t)   con k=" + k + ", Tamb=" + tAmb + ", T0=" + t0);
        Reporte.valor("Vida media (ln2/k)", ModelosExponenciales.vidaMedia(k));
        Reporte.valor("T(10)", ModelosExponenciales.enfriamientoNewton(k, tAmb, t0, 10));
        Reporte.valor("T(40)", ModelosExponenciales.enfriamientoNewton(k, tAmb, t0, 40));
        System.out.println();

        List<Curva> curvas = Arrays.asList(
                new Curva(t -> ModelosExponenciales.enfriamientoNewton(k, tAmb, t0, t), "T(t)", new Color(200, 60, 40))
        );

        GraficoFuncion grafico = new GraficoFuncion(
                "Ejercicio 1 - Enfriamiento de Newton", 0, 80, curvas, "t", "T"
        );

        VentanaGrafico.mostrar("Ejercicio 1 - Enfriamiento", grafico, "Ejercicio1_Enfriamiento");
    }


    // EJERCICIO 2: Crecimiento Poblacional (Modelo de Malthus) — dP/dt = r·P(t)
    // Solución: P(t) = P0·e^(r·t)

    private static void ejercicio2() {

        Reporte.titulo("Ejercicio 2: Crecimiento Poblacional (Malthus)");

        double r = 0.08;
        double p0 = 100;

        Reporte.linea("P(t) = P0*e^(r*t)   con r=" + r + ", P0=" + p0);
        Reporte.valor("P(10)", ModelosExponenciales.malthus(r, p0, 10));
        Reporte.valor("P(30)", ModelosExponenciales.malthus(r, p0, 30));
        System.out.println();

        List<Curva> curvas = Arrays.asList(
                new Curva(t -> ModelosExponenciales.malthus(r, p0, t), "P(t)", new Color(50, 130, 60))
        );

        GraficoFuncion grafico = new GraficoFuncion(
                "Ejercicio 2 - Crecimiento de Malthus", 0, 30, curvas, "t", "P"
        );

        VentanaGrafico.mostrar("Ejercicio 2 - Malthus", grafico, "Ejercicio2_Malthus");
    }


    // EJERCICIO 3: Decaimiento Radiactivo — dN/dt = -λ·N(t)
    // Solución: N(t) = N0·e^(-λ·t)

    private static void ejercicio3() {

        Reporte.titulo("Ejercicio 3: Decaimiento Radiactivo");

        double lambda = 0.1;
        double n0 = 100;

        Reporte.linea("N(t) = N0*e^(-λ*t)   con λ=" + lambda + ", N0=" + n0);
        Reporte.valor("Vida media (ln2/λ)", ModelosExponenciales.vidaMedia(lambda));
        Reporte.valor("N(10)", ModelosExponenciales.decaimientoRadiactivo(lambda, n0, 10));
        Reporte.valor("N(30)", ModelosExponenciales.decaimientoRadiactivo(lambda, n0, 30));
        System.out.println();

        List<Curva> curvas = Arrays.asList(
                new Curva(t -> ModelosExponenciales.decaimientoRadiactivo(lambda, n0, t), "N(t)", new Color(60, 90, 200))
        );

        GraficoFuncion grafico = new GraficoFuncion(
                "Ejercicio 3 - Decaimiento Radiactivo", 0, 40, curvas, "t", "N"
        );

        VentanaGrafico.mostrar("Ejercicio 3 - Decaimiento", grafico, "Ejercicio3_Decaimiento");
    }


    // EJERCICIO 4: diagrama en bloques de los 6 sistemas lineales (a) a (f)

    private static void ejercicio4() {

        Reporte.titulo("Ejercicio 4: Diagramas en bloques");

        Object[][] sistemas = {
                {"a", 1.0, 2.0, -5.0, 2.0, "ẋ = x + 2y,  ẏ = -5x + 2y"},
                {"b", -2.0, 0.0, 0.0, -3.0, "ẋ = -2x,  ẏ = -3y"},
                {"c", -3.0, 2.0, -1.0, 0.0, "ẋ = -3x + 2y,  ẏ = -x"},
                {"d", 10.0, -18.0, 6.0, -11.0, "ẋ = 10x - 18y,  ẏ = 6x - 11y"},
                {"e", 0.0, -1.0, 1.0, -2.0, "ẋ = -y,  ẏ = x - 2y"},
                {"f", 0.0, 1.0, 0.0, -1.0, "ẋ = y,  ẏ = -y"},
        };

        for (Object[] s : sistemas) {

            String nombre = (String) s[0];
            double a11 = (double) s[1];
            double a12 = (double) s[2];
            double a21 = (double) s[3];
            double a22 = (double) s[4];
            String ecuacion = (String) s[5];

            Reporte.linea("(" + nombre + ")  " + ecuacion);

            DiagramaBloques diagrama = new DiagramaBloques(nombre, a11, a12, a21, a22, ecuacion);

            VentanaGrafico.mostrar("Ejercicio 4 - Sistema " + nombre, diagrama, "Ejercicio4_Sistema" + nombre.toUpperCase());
        }

        System.out.println();
    }


    // EJERCICIO 5: Modelo Depredador-Presa (Lotka-Volterra) — sin solución cerrada:
    // se clasifican los puntos de equilibrio y se integra numéricamente (Runge-Kutta 4).

    private static void ejercicio5() {

        Reporte.titulo("Ejercicio 5: Depredador-Presa (Lotka-Volterra)");

        double alfa = 1.0;
        double beta = 0.1;
        double delta = 0.075;
        double gama = 1.5;

        LotkaVolterra sistema = new LotkaVolterra(alfa, beta, delta, gama);

        Reporte.linea("dx/dt = α·x - β·x·y,  dy/dt = δ·x·y - γ·y   (α=" + alfa + ", β=" + beta + ", δ=" + delta + ", γ=" + gama + ")");
        Reporte.linea("Puntos de equilibrio: (0,0) y (x*,y*) = (γ/δ, α/β)");
        Reporte.valor("x* = γ/δ", sistema.equilibrioX());
        Reporte.valor("y* = α/β", sistema.equilibrioY());
        Reporte.linea("Clasificación: (0,0) es punto silla (autovalores α=" + alfa + " y -γ=" + (-gama) + ", signos opuestos).");
        Reporte.linea("(x*,y*) es un centro (autovalores imaginarios puros ±i·sqrt(α·γ) = ±i·" + String.format("%.4f", Math.sqrt(alfa * gama)) + ").");

        double t0 = 0;
        double[] estadoInicial = {10, 5}; // x0 (presas), y0 (depredadores)
        double h = 0.01;
        int pasos = 6000; // t final = 60

        double[][] trayectoria = RungeKutta4.resolver(sistema, t0, estadoInicial, h, pasos);

        Reporte.valor("x(60) [presas]", trayectoria[pasos][1]);
        Reporte.valor("y(60) [depredadores]", trayectoria[pasos][2]);
        System.out.println();

        // Poblaciones vs. tiempo
        List<Curva> curvasTiempo = Arrays.asList(
                new Curva(t -> interpolar(trayectoria, t, 1), "x(t) - presas", new Color(50, 130, 60)),
                new Curva(t -> interpolar(trayectoria, t, 2), "y(t) - depredadores", new Color(200, 60, 40))
        );

        GraficoFuncion graficoTiempo = new GraficoFuncion(
                "Ejercicio 5 - Poblaciones vs. tiempo", 0, pasos * h, curvasTiempo, "t", "población"
        );

        VentanaGrafico.mostrar("Ejercicio 5 - Poblaciones", graficoTiempo, "Ejercicio5_Poblaciones");

        // Retrato de fase: la órbita (x(t), y(t)) es una curva cerrada, no una función de x,
        // así que se grafica como trayectoria paramétrica (ver GraficoFase).
        double[] xs = new double[trayectoria.length];
        double[] ys = new double[trayectoria.length];
        for (int i = 0; i < trayectoria.length; i++) {
            xs[i] = trayectoria[i][1];
            ys[i] = trayectoria[i][2];
        }

        GraficoFase graficoFase = new GraficoFase(
                "Ejercicio 5 - Retrato de fase",
                xs, ys,
                "x (presas)", "y (depredadores)",
                new double[]{sistema.equilibrioX(), sistema.equilibrioY()}
        );

        VentanaGrafico.mostrar("Ejercicio 5 - Retrato de fase", graficoFase, "Ejercicio5_RetratoFase");
    }

    // Interpola linealmente la columna `col` (1=x, 2=y) de la trayectoria en el tiempo t.
    private static double interpolar(double[][] trayectoria, double t, int col) {

        int n = trayectoria.length;
        double t0 = trayectoria[0][0];
        double h = trayectoria[1][0] - t0;

        int i = (int) ((t - t0) / h);
        if (i < 0) i = 0;
        if (i >= n - 1) i = n - 2;

        double tA = trayectoria[i][0];
        double frac = (t - tA) / h;

        return trayectoria[i][col] + frac * (trayectoria[i + 1][col] - trayectoria[i][col]);
    }

}
