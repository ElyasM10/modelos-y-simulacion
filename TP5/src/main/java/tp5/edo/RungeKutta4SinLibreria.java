package tp5.edo;

// Versión SIN librerías externas, comentada a propósito.
//
// Esta es la alternativa a RungeKutta4.java: en vez de delegar en
// ClassicalRungeKuttaIntegrator de Apache Commons Math, implementa a mano el método
// de Runge-Kutta de orden 4 (mismas fórmulas k1..k4). Da exactamente los mismos
// resultados numéricos que la versión con librería.
//
// Para probarla:
//   1. Descomentar toda la clase de acá abajo.
//   2. Comentar tp5/edo/RungeKutta4.java, ya que ambas clases se llaman igual
//      (RungeKutta4) y no pueden coexistir sin conflicto.
//   3. La dependencia de Apache Commons Math puede quitarse del pom.xml si no se
//      usa en ningún otro lado (no hace falta para esta versión).
//
/*
public class RungeKutta4 {

    // Resuelve el sistema desde t0 con estado inicial x0, con paso h, durante
    // "pasos" iteraciones. Devuelve una matriz [paso][0]=t, [paso][1..n]=estado.
    public static double[][] resolver(SistemaEDO sistema, double t0, double[] x0, double h, int pasos) {

        int n = x0.length;
        double[][] resultado = new double[pasos + 1][n + 1];

        double t = t0;
        double[] x = x0.clone();

        resultado[0][0] = t;
        System.arraycopy(x, 0, resultado[0], 1, n);

        for (int paso = 1; paso <= pasos; paso++) {

            double[] k1 = sistema.derivada(t, x);
            double[] x2 = sumar(x, escalar(k1, h / 2));
            double[] k2 = sistema.derivada(t + h / 2, x2);
            double[] x3 = sumar(x, escalar(k2, h / 2));
            double[] k3 = sistema.derivada(t + h / 2, x3);
            double[] x4 = sumar(x, escalar(k3, h));
            double[] k4 = sistema.derivada(t + h, x4);

            double[] xSiguiente = new double[n];
            for (int i = 0; i < n; i++) {
                xSiguiente[i] = x[i] + (h / 6.0) * (k1[i] + 2 * k2[i] + 2 * k3[i] + k4[i]);
            }

            t = t + h;
            x = xSiguiente;

            resultado[paso][0] = t;
            System.arraycopy(x, 0, resultado[paso], 1, n);
        }

        return resultado;
    }

    private static double[] sumar(double[] a, double[] b) {
        double[] r = new double[a.length];
        for (int i = 0; i < a.length; i++) r[i] = a[i] + b[i];
        return r;
    }

    private static double[] escalar(double[] a, double factor) {
        double[] r = new double[a.length];
        for (int i = 0; i < a.length; i++) r[i] = a[i] * factor;
        return r;
    }
}
*/
