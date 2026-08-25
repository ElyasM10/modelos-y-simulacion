package tp2.funciones;

public class Escalon implements Funcion {

    private double amplitud;
    private double t0;

    public Escalon(double amplitud, double t0) {
        this.amplitud = amplitud;
        this.t0 = t0;
    }

    @Override
    public double calcular(double t) {

        if (t < t0) {
            return 0;
        }

        return amplitud;
    }

    @Override
    public String getNombre() {
        return "Escalon";
    }
}