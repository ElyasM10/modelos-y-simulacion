package tp2.funciones;

public class Exponencial implements Funcion {

    private double amplitud;
    private double alfa;
    private double t0;

    public Exponencial(double amplitud, double alfa, double t0) {
        this.amplitud = amplitud;
        this.alfa = alfa;
        this.t0 = t0;
    }

    @Override
    public double calcular(double t) {

        return amplitud *
                Math.exp(alfa * (t - t0));
    }

    @Override
    public String getNombre() {
        return "Exponencial";
    }
}