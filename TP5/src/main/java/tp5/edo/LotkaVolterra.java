package tp5.edo;

// Ejercicio 5 — Modelo Depredador-Presa (Lotka-Volterra):
//   dx/dt = α·x - β·x·y   (presas)
//   dy/dt = δ·x·y - γ·y   (depredadores)
// Sistema no lineal, sin solución cerrada en funciones elementales (ver análisis de
// puntos de equilibrio y linealización en el informe): se integra numéricamente.
public class LotkaVolterra implements SistemaEDO {

    private final double alfa;
    private final double beta;
    private final double delta;
    private final double gama;

    public LotkaVolterra(double alfa, double beta, double delta, double gama) {
        this.alfa = alfa;
        this.beta = beta;
        this.delta = delta;
        this.gama = gama;
    }

    @Override
    public double[] derivada(double t, double[] estado) {

        double x = estado[0];
        double y = estado[1];

        double dx = alfa * x - beta * x * y;
        double dy = delta * x * y - gama * y;

        return new double[]{dx, dy};
    }

    // Equilibrio de coexistencia (x*, y*) = (γ/δ, α/β). El otro equilibrio es (0,0).
    public double equilibrioX() {
        return gama / delta;
    }

    public double equilibrioY() {
        return alfa / beta;
    }
}
