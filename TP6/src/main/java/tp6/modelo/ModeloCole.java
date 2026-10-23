package tp6.modelo;

import org.apache.commons.math3.complex.Complex;

/*
  El modelo de Cole: dado un tejido (sus 4 parámetros) calcula qué impedancia tiene a cada frecuencia.

  LA FÓRMULA
                     R0 - Rinf
      Z(ω) = Rinf + ------------------
                    1 + (j·ω·τ)^α

  y las dos fórmulas auxiliares que la acompañan:
      ω = 2π·f          frecuencia angular [rad/s], a partir de la frecuencia f en Hz
      τ = 1 / (2π·fc)   constante de tiempo [s], a partir de la frecuencia característica fc
  (por eso ω·τ = f/fc: lo único que importa es cuántas veces está f por encima o por debajo de fc)

  QUÉ ES CADA COSA
      Z    : impedancia, un número complejo  Z = Re + j·Im  [Ω]
             - Re (parte real) se comporta como una resistencia común
             - Im (parte imaginaria) es el efecto del capacitor de las membranas; da negativa
      j    : unidad imaginaria, j = √(-1)
      f    : frecuencia de la corriente aplicada [Hz]  (lo que va variando en cada punto del espectro)

  LOS 4 PARÁMETROS DEL TEJIDO
      R0   : resistencia cuando la frecuencia tiende a cero [Ω]
             (la membrana de las células bloquea la corriente, que pasa por el líquido de afuera)
      Rinf : resistencia cuando la frecuencia tiende a infinito [Ω]
             (la corriente atraviesa las células; siempre es menor que R0)
      fc   : frecuencia característica [Hz]; es donde ocurre el "cambio" de R0 a Rinf
             (en f = fc la parte real de Z queda justo en el punto medio, (R0 + Rinf)/2)
      α    : qué tan aplanada es esa transición, entre 0 y 1 [sin unidad]
             (con α = 1 la transición es lo más abrupta posible; cuanto más chico, más estirada)

  Sobre el signo: la primera versión del enunciado traía "1 − (jωτ)^α" (errata de tipeo); la versión corregida
  ("Tipeo corregido") trae "1 + (jωτ)^α", que es la que se usa acá. Con el menos, el denominador queda casi en
  cero cerca de ω·τ = 1 y salen impedancias absurdas (mayores que R0).
 */
public class ModeloCole {

    // los 4 parámetros del tejido (no cambian una vez creado el modelo)
    private final double r0;     // [Ω]
    private final double rInf;   // [Ω]
    private final double fc;     // [Hz]
    private final double alfa;   // sin unidad, entre 0 y 1

    // Crea el modelo de un tejido. Ejemplo (músculo longitudinal): new ModeloCole(385, 70, 37e3, 0.27)
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

        // Paso 1: constante de tiempo τ = 1/(2π·fc). Ej. con fc = 37 kHz: τ = 4.3 µs
        double tau = 1.0 / (2 * Math.PI * fc);

        // Paso 2: frecuencia angular ω = 2π·f
        double omega = 2 * Math.PI * f;

        // Paso 3: j·ω·τ, un imaginario puro (parte real 0 y parte imaginaria ω·τ = f/fc)
        Complex jwt = new Complex(0, omega * tau);

        // Paso 4: (jωτ)^α y le sumo 1, o sea el denominador completo: 1 + (jωτ)^α
        //         Ej. en f = fc con α = 0.27:  1 + (0.9114 + j0.4115) = 1.9114 + j0.4115
        Complex denominador = Complex.ONE.add(jwt.pow(alfa));

        // Paso 5: el numerador (R0 - Rinf) es un número real; lo divido por el denominador complejo.
        //         Ej. en f = fc: 315 / (1.9114 + j0.4115) = 157.50 - j33.91
        Complex fraccion = new Complex(r0 - rInf, 0).divide(denominador);

        // Paso 6: le sumo Rinf y queda Z.  Ej. en f = fc: 70 + 157.50 - j33.91 = 227.50 - j33.91 Ω
        return fraccion.add(rInf);
    }

    public double getR0() { return r0; }

    public double getRInf() { return rInf; }

    public double getFc() { return fc; }

    public double getAlfa() { return alfa; }
}
