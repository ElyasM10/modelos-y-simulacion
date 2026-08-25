package tp2.funciones;

public class SenalAmortiguada implements Funcion {

    private double amplitud;
    private double frecuencia;
    private double fase;
    private double alfa;

    public SenalAmortiguada(
            double amplitud,
            double frecuencia,
            double fase,
            double alfa) {

        this.amplitud = amplitud;
        this.frecuencia = frecuencia;
        this.fase = fase;
        this.alfa = alfa;
    }

    @Override
    public double calcular(double t) {

        double seno = Math.sin(
                2 * Math.PI * frecuencia * t + fase
        );

        double exponencial = Math.exp(alfa * t);

        return amplitud * seno * exponencial;
    }

    @Override
    public String getNombre() {
        return "Senal Amortiguada";
    }
}