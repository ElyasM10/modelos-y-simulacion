package tp3.algebra;

public class SistemaLineal {

    // Resuelve A·x = b por eliminación de Gauss con pivoteo parcial.
    public static double[] resolver(double[][] a, double[] b) {

        int n = b.length;

        double[][] m = new double[n][n];
        double[] v = new double[n];

        for (int i = 0; i < n; i++) {
            v[i] = b[i];
            for (int j = 0; j < n; j++) {
                m[i][j] = a[i][j];
            }
        }

        for (int col = 0; col < n; col++) {

            int filaPivote = col;

            for (int fila = col + 1; fila < n; fila++) {
                if (Math.abs(m[fila][col]) > Math.abs(m[filaPivote][col])) {
                    filaPivote = fila;
                }
            }

            double[] filaTemp = m[col];
            m[col] = m[filaPivote];
            m[filaPivote] = filaTemp;

            double vTemp = v[col];
            v[col] = v[filaPivote];
            v[filaPivote] = vTemp;

            for (int fila = col + 1; fila < n; fila++) {

                double factor = m[fila][col] / m[col][col];

                for (int j = col; j < n; j++) {
                    m[fila][j] -= factor * m[col][j];
                }

                v[fila] -= factor * v[col];
            }
        }

        double[] x = new double[n];

        for (int fila = n - 1; fila >= 0; fila--) {

            double suma = v[fila];

            for (int j = fila + 1; j < n; j++) {
                suma -= m[fila][j] * x[j];
            }

            x[fila] = suma / m[fila][fila];
        }

        return x;
    }
}
