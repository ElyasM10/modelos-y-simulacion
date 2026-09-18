package tp4.leontief;

import tp4.algebra.SistemaLineal;

// Versión SIN librerías externas: todo el álgebra lineal (armado de I-A y
// resolución del sistema) está escrito a mano con arrays de double, igual
// que en TP2/TP3. La versión equivalente con Apache Commons Math queda
// comentada en LeontiefApacheCommonsMath.java, en este mismo paquete.
public class Leontief {

    // Resuelve x = (I - A)^-1 · y planteando y resolviendo (I - A)·x = y por
    // eliminación de Gauss con pivoteo parcial (ver SistemaLineal), sin
    // invertir la matriz.
    public static double[] produccion(double[][] a, double[] y) {

        int n = a.length;
        double[][] iMenosA = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                iMenosA[i][j] = (i == j ? 1.0 : 0.0) - a[i][j];
            }
        }

        return SistemaLineal.resolver(iMenosA, y);
    }

    // Aplica una inversa (I - A)^-1 ya conocida a un vector: producción total,
    // o el efecto Δx = (I - A)^-1 · Δy de una variación de demanda final.
    public static double[] aplicarInversa(double[][] inversa, double[] v) {

        int n = inversa.length;
        double[] resultado = new double[n];

        for (int i = 0; i < n; i++) {
            double suma = 0.0;
            for (int j = 0; j < n; j++) {
                suma += inversa[i][j] * v[j];
            }
            resultado[i] = suma;
        }

        return resultado;
    }
}
