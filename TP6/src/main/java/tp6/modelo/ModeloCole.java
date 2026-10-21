package tp6.modelo;

import org.apache.commons.math3.complex.Complex;

/*
  El modelo de Cole: dado un tejido (sus 4 parámetros) calcula qué impedancia tiene a cada frecuencia.

                     R0 - Rinf
      Z(ω) = Rinf + ------------------        con  τ = 1 / (2π·fc)   y   ω = 2π·f
                    1 + (j·ω·τ)^α

  R0   : resistencia cuando la frecuencia es casi cero [Ω]  (la corriente va por afuera de las células)
  Rinf : resistencia cuando la frecuencia es altísima [Ω]   (la corriente atraviesa las células)
  fc   : frecuencia característica [Hz], donde ocurre la transición de R0 a Rinf
  α    : qué tan "aplanada" es esa transición (entre 0 y 1; con 1 es un semicírculo perfecto)

  Ojo con el signo: el enunciado escribe "1 − (jωτ)^α", pero acá se usa "1 + (jωτ)^α", que es la forma
  correcta de Cole. Con el menos, el denominador queda casi en cero cerca de ω·τ = 1 y salen impedancias
  absurdas (mayores que R0).
 */
public class ModeloCole {

    // los 4 parámetros del tejido (no cambian una vez creado el modelo)
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

    // Revisa que los parámetros tengan sentido físico; si alguno no, tira un error explicando cuál
    public static void validar(double r0, double rInf, double fc, double alfa) {
        if (rInf < 0) throw new IllegalArgumentException("R∞ no puede ser negativa");
        if (r0 <= rInf) throw new IllegalArgumentException("R0 debe ser mayor que R∞");
        if (fc <= 0) throw new IllegalArgumentException("fc debe ser mayor que 0");
        if (alfa <= 0 || alfa > 1) throw new IllegalArgumentException("α debe estar entre 0 (sin incluir) y 1");
    }

    /*
      La cuenta principal: devuelve Z (un número complejo: parte real + j·parte imaginaria) a la frecuencia f [Hz].

      Lo único difícil de la fórmula es (j·ω·τ)^α, un número imaginario elevado a una potencia con coma.
      Se resuelve así: si x = ω·τ = f/fc, entonces
          (j·x)^α = x^α · [ cos(α·90°) + j·sin(α·90°) ]
      (elevar a la α multiplica el ángulo de j, que son 90°, por α y eleva el módulo a la α).
      Esa cuenta la hace sola la librería con Complex.pow().

      Después, del resultado se sacan: z.getReal() (parte real), z.getImaginary() (parte imaginaria),
      z.abs() (módulo, √(Re² + Im²)) y z.getArgument() (fase en radianes, atan2(Im, Re)).
      Como el tejido se comporta como capacitor, la parte imaginaria da siempre negativa.
     */
    public Complex impedancia(double f) {
        double tau = 1.0 / (2 * Math.PI * fc);   // τ: constante de tiempo, sale de la frecuencia característica
        double omega = 2 * Math.PI * f;          // ω: frecuencia angular

        Complex jwt = new Complex(0, omega * tau);                       // j·ω·τ: un imaginario puro (parte real 0)
        Complex denominador = Complex.ONE.add(jwt.pow(alfa));            // 1 + (jωτ)^α  (la librería hace la potencia compleja)
        return new Complex(r0 - rInf, 0).divide(denominador).add(rInf);  // Rinf + (R0-Rinf) / denominador
    }

    public double getR0() { return r0; }

    public double getRInf() { return rInf; }

    public double getFc() { return fc; }

    public double getAlfa() { return alfa; }
}
