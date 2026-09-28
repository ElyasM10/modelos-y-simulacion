package tp5.edo;

import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;

// Versión CON librería (Apache Commons Math): integra el sistema con
// ClassicalRungeKuttaIntegrator (el mismo método de Runge-Kutta de orden 4, ya
// implementado en la librería). La versión sin librería (a mano) queda comentada en
// RungeKutta4SinLibreria.java, en este mismo paquete.
public class RungeKutta4 {

    // Resuelve el sistema desde t0 con estado inicial x0, con paso h, durante
    // "pasos" iteraciones. Devuelve una matriz [paso][0]=t, [paso][1..n]=estado.
    public static double[][] resolver(SistemaEDO sistema, double t0, double[] x0, double h, int pasos) {

        int n = x0.length;

        FirstOrderDifferentialEquations ecuaciones = new FirstOrderDifferentialEquations() {

            @Override
            public int getDimension() {
                return n;
            }

            @Override
            public void computeDerivatives(double t, double[] y, double[] yDot) {
                double[] derivada = sistema.derivada(t, y);
                System.arraycopy(derivada, 0, yDot, 0, n);
            }
        };

        FirstOrderIntegrator integrador = new ClassicalRungeKuttaIntegrator(h);

        double[][] resultado = new double[pasos + 1][n + 1];

        double t = t0;
        double[] estado = x0.clone();

        resultado[0][0] = t;
        System.arraycopy(estado, 0, resultado[0], 1, n);

        for (int paso = 1; paso <= pasos; paso++) {

            double[] siguiente = new double[n];
            integrador.integrate(ecuaciones, t, estado, t + h, siguiente);

            t = t + h;
            estado = siguiente;

            resultado[paso][0] = t;
            System.arraycopy(estado, 0, resultado[paso], 1, n);
        }

        return resultado;
    }
}
