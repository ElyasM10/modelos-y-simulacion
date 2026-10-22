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
  Ajuste del modelo de Cole a una medición: hace el camino inverso al resto del programa.
  Le das las mediciones (frecuencia, parte real, parte imaginaria) y te devuelve los 4 parámetros
  (R0, Rinf, fc, α) que hacen que el modelo se parezca lo más posible a esos datos.

  Cómo lo hace: mínimos cuadrados. Prueba parámetros, mira cuánto se aleja el modelo de la medición
  (en parte real e imaginaria a la vez) y los va corrigiendo hasta que ya no mejora. De la corrección se
  encarga el algoritmo Levenberg-Marquardt que ya trae Apache Commons Math.

  Problema: según el punto de partida puede quedarse atascado en un resultado malo (un "mínimo local").
  Para evitarlo se prueba desde varias frecuencias características iniciales y se queda con el mejor.
 */
public class AjusteCole {

    private final ModeloCole modelo;
    private final double rmse;   // error medio del ajuste en ohm (más chico = mejor)

    private AjusteCole(ModeloCole modelo, double rmse) {
        this.modelo = modelo;
        this.rmse = rmse;
    }

    public ModeloCole getModelo() { return modelo; }

    public double getRmse() { return rmse; }

    public static AjusteCole ajustar(final double[] f, final double[] re, final double[] im) {

        /*
          PASO A PASO de un ajuste:
            1) Se arma el vector de lo medido (partes reales + partes imaginarias).
            2) Se calculan datos útiles de la medición para armar un punto de partida razonable.
            3) Se repite 8 veces, cada una con una fc inicial distinta:
                 a) se le dice al optimizador cómo calcular lo que predice el modelo y cómo cambia al mover cada parámetro
                 b) Levenberg-Marquardt corrige los parámetros una y otra vez hasta que el error ya no baja
                 c) se calcula el error medio y se descarta si los parámetros no tienen sentido físico
            4) De las 8 vueltas, se devuelve la de menor error.
         */
        final int n = f.length;   // cantidad de frecuencias medidas

        // Paso 1: lo que queremos alcanzar: las n partes reales seguidas de las n imaginarias, en un solo vector
        double[] objetivo = new double[2 * n];
        System.arraycopy(re, 0, objetivo, 0, n);
        System.arraycopy(im, 0, objetivo, n, n);

        // Paso 2: datos útiles para armar el punto de partida (la parte real va entre R0 y Rinf)
        double reMax = max(re);
        double reMin = min(re);
        double fMin = min(f);
        double fMax = max(f);

        AjusteCole mejor = null;

        // Paso 3: se repite el ajuste 8 veces, cada una arrancando con una fc distinta (repartidas en escala logarítmica)
        for (int k = 0; k < 8; k++) {
            double fc0 = fMin * Math.pow(fMax / fMin, k / 7.0);

            // Punto de partida (a): R0 = el mayor Re medido, Rinf = el menor, fc = la de esta vuelta, α = 0.5.
            // Ojo: el optimizador no trabaja con fc sino con log10(fc), porque fc varía en órdenes de magnitud.
            double[] inicio = {reMax, reMin, Math.log10(fc0), 0.5};

            LeastSquaresProblem problema = new LeastSquaresBuilder()
                    .start(inicio)
                    // "model" (a): le explicamos al optimizador cómo calcular los valores del modelo y su jacobiana
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
                // (b) acá trabaja Levenberg-Marquardt: itera moviendo R0, Rinf, log10(fc) y α hasta ajustar
                LeastSquaresOptimizer.Optimum optimo = new LevenbergMarquardtOptimizer().optimize(problema);
                double[] p = optimo.getPoint().toArray();
                // (c) error medio (RMSE) = √( suma de residuos² / (2n) ), en ohm.
                //     getCost() es √(suma de residuos²); se divide por 2n porque hay n valores reales + n imaginarios
                double rmse = Math.sqrt(optimo.getCost() * optimo.getCost() / (2.0 * n));

                // Solo vale si el resultado tiene sentido físico: 0.05 < α <= 1 y R0 > Rinf >= 0
                boolean valido = p[3] > 0.05 && p[3] <= 1.0 && p[0] > p[1] && p[1] >= 0;
                if (valido && (mejor == null || rmse < mejor.rmse)) {
                    mejor = new AjusteCole(new ModeloCole(p[0], p[1], Math.pow(10, p[2]), p[3]), rmse);
                }
            } catch (TooManyEvaluationsException e) {
                // esta vuelta no llegó a ningún lado: se sigue con la próxima
            } catch (ConvergenceException e) {
                // idem
            }
        }

        // Paso 4: si ninguna de las 8 vueltas dio algo válido, no hay ajuste
        if (mejor == null) {
            throw new IllegalStateException("No se pudo ajustar el modelo de Cole a estos datos");
        }
        return mejor;
    }

    // Para unos parámetros p devuelve dos cosas: lo que "predice" el modelo y la jacobiana
    // (cuánto cambia cada valor predicho si se mueve un poquito cada parámetro).
    // La jacobiana se calcula "a lo bruto": se mueve un parámetro un poquito, se recalcula y se ve la diferencia.
    private static Pair<RealVector, RealMatrix> evaluar(double[] p, double[] f) {

        double[] valores = valoresModelo(p, f);   // lo que predice el modelo con los parámetros actuales
        double[][] jacobiana = new double[valores.length][4];

        for (int j = 0; j < 4; j++) {
            double paso = 1e-6 * Math.max(1.0, Math.abs(p[j]));   // "un poquito" proporcional al parámetro
            double[] q = p.clone();
            q[j] += paso;
            double[] desplazados = valoresModelo(q, f);
            for (int i = 0; i < valores.length; i++) {
                jacobiana[i][j] = (desplazados[i] - valores[i]) / paso;
            }
        }
        return new Pair<RealVector, RealMatrix>(new ArrayRealVector(valores), new Array2DRowRealMatrix(jacobiana));
    }

    // Calcula Z con el modelo en todas las frecuencias medidas y lo devuelve como: [partes reales..., partes imaginarias...]
    private static double[] valoresModelo(double[] p, double[] f) {
        ModeloCole m = new ModeloCole(p[0], p[1], Math.pow(10, p[2]), p[3]);   // p[2] es log10(fc)
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
