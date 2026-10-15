package tp6.modelo;

import org.apache.commons.math3.complex.Complex;

/*
  Modelo de Cole de la bioimpedancia:

                     R0 - Rinf
      Z(ω) = Rinf + ------------------        con  τ = 1 / (2π·fc)   y   ω = 2π·f
                    1 + (j·ω·τ)^α

  R0   : resistencia a frecuencia cero   [Ω]
  Rinf : resistencia a frecuencia infinita [Ω]
  fc   : frecuencia característica (de relajación) [Hz]
  α    : constante adimensional (0 < α ≤ 1); con α = 1 el modelo es un semicírculo perfecto (Debye)

  Nota: el enunciado escribe "1 − (jωτ)^α"; se usa "1 + (jωτ)^α", que es la forma del modelo de Cole
  (con el signo menos el denominador se anula cerca de ω·τ = 1 y la impedancia no tendría sentido físico).
 */
public class ModeloCole {

    private final double r0;
    private final double rInf;
    private final double fc;
    private final double alfa;

    public ModeloCole(double r0, double rInf, double fc, double alfa) {
        this.r0 = r0;
        this.rInf = rInf;
        this.fc = fc;
        this.alfa = alfa;
    }

    // Comprueba que los parámetros tengan sentido físico; si no, lanza IllegalArgumentException con el motivo
    public static void validar(double r0, double rInf, double fc, double alfa) {
        if (rInf < 0) throw new IllegalArgumentException("R∞ no puede ser negativa");
        if (r0 <= rInf) throw new IllegalArgumentException("R0 debe ser mayor que R∞");
        if (fc <= 0) throw new IllegalArgumentException("fc debe ser mayor que 0");
        if (alfa <= 0 || alfa > 1) throw new IllegalArgumentException("α debe estar entre 0 (sin incluir) y 1");
    }

    // Impedancia compleja Z a la frecuencia f [Hz]
    public Complex impedancia(double f) {
        double tau = 1.0 / (2 * Math.PI * fc);
        double omega = 2 * Math.PI * f;

        Complex jwt = new Complex(0, omega * tau);                       // j·ω·τ
        Complex denominador = Complex.ONE.add(jwt.pow(alfa));            // 1 + (jωτ)^α
        return new Complex(r0 - rInf, 0).divide(denominador).add(rInf);  // Rinf + (R0-Rinf)/denominador
    }

    public double getR0() { return r0; }

    public double getRInf() { return rInf; }

    public double getFc() { return fc; }

    public double getAlfa() { return alfa; }
}
