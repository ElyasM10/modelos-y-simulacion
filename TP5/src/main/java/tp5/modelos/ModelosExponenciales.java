package tp5.modelos;

// Los tres modelos de los Ejercicios 1 a 3 son ecuaciones diferenciales lineales de
// primer orden de la forma dz/dt = k·z (separables), cuya solución general es una
// exponencial. Ver el desarrollo completo (separación de variables) en el informe.
public class ModelosExponenciales {

    // Ejercicio 1 — Ley del Enfriamiento de Newton: dT/dt = -k·(T - Tamb)
    // Solución: T(t) = Tamb + (T0 - Tamb)·e^(-k·t)
    public static double enfriamientoNewton(double k, double tAmb, double t0, double t) {
        return tAmb + (t0 - tAmb) * Math.exp(-k * t);
    }

    // Ejercicio 2 — Crecimiento Poblacional (Modelo de Malthus): dP/dt = r·P(t)
    // Solución: P(t) = P0·e^(r·t)
    public static double malthus(double r, double p0, double t) {
        return p0 * Math.exp(r * t);
    }

    // Ejercicio 3 — Decaimiento Radiactivo: dN/dt = -λ·N(t)
    // Solución: N(t) = N0·e^(-λ·t)
    public static double decaimientoRadiactivo(double lambda, double n0, double t) {
        return n0 * Math.exp(-lambda * t);
    }

    // Vida media: tiempo en el que N(t) = N0/2 (o, para Malthus con r<0, análogo).
    public static double vidaMedia(double constante) {
        return Math.log(2) / constante;
    }
}
