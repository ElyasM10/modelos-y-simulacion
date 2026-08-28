package tp3;

import tp3.ajuste.CuadradosMinimos;
import tp3.ajuste.FormatoEcuacion;
import tp3.ajuste.Pearson;
import tp3.grafico.Curva;
import tp3.grafico.GraficoAjuste;
import tp3.grafico.VentanaGraficoAjuste;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        System.out.println();
        System.out.println("       TP3 - MODELOS Y SIMULACIÓN");
        System.out.println();

        ejercicio1();
        ejercicio2();
        ejercicio3();
        ejercicio4();

        System.out.println();
        System.out.println("========================================");
        System.out.println("Todos los ajustes fueron calculados.");
        System.out.println("========================================");
    }


    // EJERCICIO 1: recta f(x) = a.x + b

    private static void ejercicio1() {

        System.out.println("=== Ejercicio 1: recta f(x) = a.x + b ===");

        double[][] conjunto1 = {
                {-2, -1, 0, 1, 2},
                {1, 2, 3, 3, 4}
        };

        double[][] conjunto2 = {
                {-6, -2, 0, 2, 6},
                {7, 5, 3, 2, 0}
        };

        double[][] conjunto3 = {
                {-4, -1, 0, 2, 3},
                {-3, -1, 0, 1, 2}
        };

        double[][][] conjuntos = {conjunto1, conjunto2, conjunto3};

        for (int i = 0; i < conjuntos.length; i++) {

            double[] x = conjuntos[i][0];
            double[] y = conjuntos[i][1];

            double[] coeficientes = CuadradosMinimos.ajustarPolinomio(x, y, 1);

            double[] yPredicho = evaluarEnTodos(coeficientes, x);
            double r = Pearson.coeficiente(y, yPredicho);

            String ecuacion = FormatoEcuacion.polinomio(coeficientes);

            System.out.println(
                    "Conjunto " + (i + 1) + ": " + ecuacion
                            + "  |  Pearson r = " + String.format("%.4f", r)
            );

            List<Curva> curvas = new ArrayList<>();
            curvas.add(new Curva(
                    t -> CuadradosMinimos.evaluarPolinomio(coeficientes, t),
                    ecuacion + "  (r=" + String.format("%.4f", r) + ")",
                    Color.BLUE
            ));

            GraficoAjuste grafico = new GraficoAjuste(
                    "Ejercicio 1 - Conjunto " + (i + 1),
                    x,
                    y,
                    curvas
            );

            VentanaGraficoAjuste.mostrar(
                    "Ejercicio 1 - Conjunto " + (i + 1),
                    grafico,
                    "Ejercicio1_Conjunto" + (i + 1)
            );
        }

        System.out.println();
    }


    // EJERCICIO 2: y = C . e^(A.x)

    private static void ejercicio2() {

        System.out.println("=== Ejercicio 2: y = C · e^(A·x) ===");

        double[] x = {0, 1, 2, 3, 4};
        double[] y = {1.5, 2.5, 3.5, 5.0, 7.5};

        double[] lnY = new double[y.length];

        for (int i = 0; i < y.length; i++) {
            lnY[i] = Math.log(y[i]);
        }

        double[] coeficientesRecta = CuadradosMinimos.ajustarPolinomio(x, lnY, 1);

        double lnC = coeficientesRecta[0];
        double a = coeficientesRecta[1];
        double c = Math.exp(lnC);

        double[] lnYPredicho = evaluarEnTodos(coeficientesRecta, x);
        double r = Pearson.coeficiente(lnY, lnYPredicho);

        String ecuacion = FormatoEcuacion.exponencial(c, a);

        System.out.println(ecuacion + "  |  Pearson r (sobre ln y) = " + String.format("%.4f", r));
        System.out.println();

        List<Curva> curvas = new ArrayList<>();
        curvas.add(new Curva(
                t -> c * Math.exp(a * t),
                ecuacion + "  (r=" + String.format("%.4f", r) + ")",
                Color.RED
        ));

        GraficoAjuste grafico = new GraficoAjuste("Ejercicio 2 - Exponencial", x, y, curvas);

        VentanaGraficoAjuste.mostrar("Ejercicio 2 - Exponencial", grafico, "Ejercicio2_Exponencial");
    }


    // EJERCICIO 3: polinomios de grado 1, 2, 3 y 4

    private static void ejercicio3() {

        System.out.println("=== Ejercicio 3: polinomios de grado 1 a 4 ===");

        double[] x = {0, 0.15, 0.31, 0.5, 0.6, 0.75};
        double[] y = {1.0, 1.004, 1.031, 1.117, 1.223, 1.422};

        Color[] colores = {Color.BLUE, Color.RED, new Color(0, 150, 0), Color.MAGENTA};

        List<Curva> curvas = new ArrayList<>();

        for (int grado = 1; grado <= 4; grado++) {

            double[] coeficientes = CuadradosMinimos.ajustarPolinomio(x, y, grado);

            double[] yPredicho = evaluarEnTodos(coeficientes, x);
            double r = Pearson.coeficiente(y, yPredicho);

            String ecuacion = FormatoEcuacion.polinomio(coeficientes);

            System.out.println(
                    "Grado " + grado + ": " + ecuacion
                            + "  |  Pearson r = " + String.format("%.4f", r)
            );

            curvas.add(new Curva(
                    t -> CuadradosMinimos.evaluarPolinomio(coeficientes, t),
                    "Grado " + grado + " (r=" + String.format("%.4f", r) + ")",
                    colores[grado - 1]
            ));
        }

        System.out.println();

        GraficoAjuste grafico = new GraficoAjuste("Ejercicio 3 - Polinomios grado 1 a 4", x, y, curvas);

        VentanaGraficoAjuste.mostrar("Ejercicio 3 - Polinomios", grafico, "Ejercicio3_Polinomios");
    }

    // EJERCICIO 4: tercera ley de Kepler T = C . x^A

    private static void ejercicio4() {

        System.out.println("=== Ejercicio 4: tercera ley de Kepler T = C · x^A ===");

        double[] distancia = {57.59, 108.11, 149.57, 227.84, 778.14, 1427.0, 2870.3, 4499.9, 5909.0};
        double[] periodo = {87.99, 224.7, 365.26, 686.98, 4332.4, 10759, 30684, 60188, 90710};

        double[] lnX = new double[distancia.length];
        double[] lnT = new double[periodo.length];

        for (int i = 0; i < distancia.length; i++) {
            lnX[i] = Math.log(distancia[i]);
            lnT[i] = Math.log(periodo[i]);
        }

        double[] coeficientesRecta = CuadradosMinimos.ajustarPolinomio(lnX, lnT, 1);

        double lnC = coeficientesRecta[0];
        double a = coeficientesRecta[1];
        double c = Math.exp(lnC);

        double[] lnTPredicho = evaluarEnTodos(coeficientesRecta, lnX);
        double r = Pearson.coeficiente(lnT, lnTPredicho);

        String ecuacion = FormatoEcuacion.potencial(c, a);

        System.out.println(ecuacion + "  |  Pearson r (sobre ln-ln) = " + String.format("%.4f", r));
        System.out.println();

        List<Curva> curvas = new ArrayList<>();
        curvas.add(new Curva(
                t -> c * Math.pow(t, a),
                ecuacion + "  (r=" + String.format("%.4f", r) + ")",
                Color.RED
        ));

        GraficoAjuste grafico = new GraficoAjuste("Ejercicio 4 - Tercera ley de Kepler", distancia, periodo, curvas);

        VentanaGraficoAjuste.mostrar("Ejercicio 4 - Kepler", grafico, "Ejercicio4_Kepler");
    }


    // UTILIDAD

    private static double[] evaluarEnTodos(double[] coeficientes, double[] x) {

        double[] resultado = new double[x.length];

        for (int i = 0; i < x.length; i++) {
            resultado[i] = CuadradosMinimos.evaluarPolinomio(coeficientes, x[i]);
        }

        return resultado;
    }
}
