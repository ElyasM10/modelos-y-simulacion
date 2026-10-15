package tp6.modelo;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.exception.TooManyEvaluationsException;
import org.apache.commons.math3.exception.ConvergenceException;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresBuilder;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresProblem;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.Pair;

/*
  Ajuste del modelo de Cole a mediciones (f, parte real, parte imaginaria).

  Se buscan los 4 parámetros (R0, Rinf, fc, α) que minimizan la suma de los cuadrados de las diferencias entre
  la medición y el modelo, tanto de la parte real como de la imaginaria (mínimos cuadrados no lineales).
  Lo hace el optimizador Levenberg-Marquardt de Apache Commons Math.

  Como el resultado depende del punto de partida, se prueba desde varias frecuencias características iniciales
  y se queda el mejor ajuste.
 */
public class AjusteCole {

    private final ModeloCole modelo;
    private final double rmse;   // error medio del ajuste [Ω]

    private AjusteCole(ModeloCole modelo, double rmse) {
        this.modelo = modelo;
        this.rmse = rmse;
    }

    public ModeloCole getModelo() { return modelo; }

    public double getRmse() { return rmse; }

    public static AjusteCole ajustar(final double[] f, final double[] re, final double[] im) {

        final int n = f.length;

        // datos a ajustar: primero las n partes reales, después las n imaginarias
        double[] objetivo = new double[2 * n];
        System.arraycopy(re, 0, objetivo, 0, n);
        System.arraycopy(im, 0, objetivo, n, n);

        double reMax = max(re);
        double reMin = min(re);
        double fMin = min(f);
        double fMax = max(f);

        AjusteCole mejor = null;

        // 8 frecuencias características iniciales repartidas (en escala logarítmica) en el rango medido
        for (int k = 0; k < 8; k++) {
            double fc0 = fMin * Math.pow(fMax / fMin, k / 7.0);
            double[] inicio = {reMax, reMin, Math.log10(fc0), 0.5};   // R0, Rinf, log10(fc), α

            LeastSquaresProblem problema = new LeastSquaresBuilder()
                    .start(inicio)
                    .model(new org.apache.commons.math3.fitting.leastsquares.MultivariateJacobianFunction() {
                        public Pair<RealVector, RealMatrix> value(RealVector p) {
                            return evaluar(p.toArray(), f);
                        }
                    })
                    .target(objetivo)
                    .lazyEvaluation(false)
                    .maxEvaluations(2000)
                    .maxIterations(2000)
                    .build();

            try {
                LeastSquaresOptimizer.Optimum optimo = new LevenbergMarquardtOptimizer().optimize(problema);
                double[] p = optimo.getPoint().toArray();
                double rmse = Math.sqrt(optimo.getCost() * optimo.getCost() / (2.0 * n));

                boolean valido = p[3] > 0.05 && p[3] <= 1.0 && p[0] > p[1] && p[1] >= 0;
                if (valido && (mejor == null || rmse < mejor.rmse)) {
                    mejor = new AjusteCole(new ModeloCole(p[0], p[1], Math.pow(10, p[2]), p[3]), rmse);
                }
            } catch (TooManyEvaluationsException e) {
                // ese punto de partida no convergió: se prueba con el siguiente
            } catch (ConvergenceException e) {
                // idem
            }
        }

        if (mejor == null) {
            throw new IllegalStateException("No se pudo ajustar el modelo de Cole a estos datos");
        }
        return mejor;
    }

    // Valores del modelo y matriz jacobiana (derivadas respecto de cada parámetro, por diferencias finitas)
    private static Pair<RealVector, RealMatrix> evaluar(double[] p, double[] f) {

        double[] valores = valoresModelo(p, f);
        double[][] jacobiana = new double[valores.length][4];

        for (int j = 0; j < 4; j++) {
            double paso = 1e-6 * Math.max(1.0, Math.abs(p[j]));
            double[] q = p.clone();
            q[j] += paso;
            double[] desplazados = valoresModelo(q, f);
            for (int i = 0; i < valores.length; i++) {
                jacobiana[i][j] = (desplazados[i] - valores[i]) / paso;
            }
        }
        return new Pair<RealVector, RealMatrix>(new ArrayRealVector(valores), new Array2DRowRealMatrix(jacobiana));
    }

    private static double[] valoresModelo(double[] p, double[] f) {
        ModeloCole m = new ModeloCole(p[0], p[1], Math.pow(10, p[2]), p[3]);
        int n = f.length;
        double[] v = new double[2 * n];
        for (int i = 0; i < n; i++) {
            Complex z = m.impedancia(f[i]);
            v[i] = z.getReal();
            v[n + i] = z.getImaginary();
        }
        return v;
    }

    private static double max(double[] v) {
        double m = v[0];
        for (double x : v) m = Math.max(m, x);
        return m;
    }

    private static double min(double[] v) {
        double m = v[0];
        for (double x : v) m = Math.min(m, x);
        return m;
    }
}
