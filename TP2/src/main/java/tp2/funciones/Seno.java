package tp2.funciones;

public class Seno implements Funcion {

    private double amplitud;
    private double frecuencia;
    private double fase;

    public Seno(double amplitud, double frecuencia, double fase) {
        this.amplitud = amplitud;
        this.frecuencia = frecuencia;
        this.fase = fase;
    }

    @Override
    public double calcular(double t) {

        return amplitud *
                Math.sin(2 * Math.PI * frecuencia * t + fase);
    }

    @Override
    public String getNombre() {
        return "Seno";
    }
}