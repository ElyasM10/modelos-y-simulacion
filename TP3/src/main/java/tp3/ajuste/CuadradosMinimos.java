package tp3.ajuste;

import tp3.algebra.SistemaLineal;

public class CuadradosMinimos {

    // Ajusta y = c0 + c1.x + c2.x^2 + ... + cN.x^N por cuadrados mínimos.
    // Devuelve los coeficientes [c0, c1, ..., cN] (orden ascendente de potencia).
    public static double[] ajustarPolinomio(double[] x, double[] y, int grado) {

        int n = grado + 1;
        int m = x.length;

        double[][] a = new double[n][n];
        double[] b = new double[n];

        for (int i = 0; i < n; i++) {

            for (int j = 0; j < n; j++) {

                double suma = 0;

                for (int k = 0; k < m; k++) {
                    suma += Math.pow(x[k], i + j);
                }

                a[i][j] = suma;
            }

            double sumaB = 0;

            for (int k = 0; k < m; k++) {
                sumaB += Math.pow(x[k], i) * y[k];
            }

            b[i] = sumaB;
        }

        return SistemaLineal.resolver(a, b);
    }

    // Evalúa un polinomio (coeficientes en orden ascendente) en un punto x.
    public static double evaluarPolinomio(double[] coeficientes, double x) {

        double resultado = 0;

        for (int i = 0; i < coeficientes.length; i++) {
            resultado += coeficientes[i] * Math.pow(x, i);
        }

        return resultado;
    }
}
