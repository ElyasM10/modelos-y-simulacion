package tp2.funciones;

public class Pulso implements Funcion {

    private double amplitud;
    private double t0;
    private double duracion;

    public Pulso(double amplitud, double t0, double duracion) {
        this.amplitud = amplitud;
        this.t0 = t0;
        this.duracion = duracion;
    }

    @Override
    public double calcular(double t) {

        if (t >= t0 && t < t0 + duracion) {
            return amplitud;
        }

        return 0;
    }

    @Override
    public String getNombre() {
        return "Pulso";
    }
}