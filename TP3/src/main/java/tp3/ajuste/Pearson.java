package tp3.ajuste;

public class Pearson {

    // Coeficiente de correlación de Pearson entre los valores reales
    // y los valores predichos por el modelo ajustado.
    public static double coeficiente(double[] yReal, double[] yPredicho) {

        int n = yReal.length;

        double mediaReal = promedio(yReal);
        double mediaPredicho = promedio(yPredicho);

        double covarianza = 0;
        double varianzaReal = 0;
        double varianzaPredicho = 0;

        for (int i = 0; i < n; i++) {

            double diferenciaReal = yReal[i] - mediaReal;
            double diferenciaPredicho = yPredicho[i] - mediaPredicho;

            covarianza += diferenciaReal * diferenciaPredicho;
            varianzaReal += diferenciaReal * diferenciaReal;
            varianzaPredicho += diferenciaPredicho * diferenciaPredicho;
        }

        return covarianza / Math.sqrt(varianzaReal * varianzaPredicho);
    }

    private static double promedio(double[] valores) {

        double suma = 0;

        for (double v : valores) {
            suma += v;
        }

        return suma / valores.length;
    }
}
