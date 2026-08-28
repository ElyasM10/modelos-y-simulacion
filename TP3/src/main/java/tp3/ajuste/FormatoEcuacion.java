package tp3.ajuste;

public class FormatoEcuacion {

    // Arma "f(x) = cn·x^n + ... + c1·x + c0" a partir de los coeficientes
    // en orden ascendente [c0, c1, ..., cn].
    public static String polinomio(double[] coeficientes) {

        StringBuilder sb = new StringBuilder("f(x) = ");

        int grado = coeficientes.length - 1;
        boolean primero = true;

        for (int i = grado; i >= 0; i--) {

            double c = coeficientes[i];

            String termino;

            if (i == 0) {
                termino = String.format("%.4f", Math.abs(c));
            } else if (i == 1) {
                termino = String.format("%.4f·x", Math.abs(c));
            } else {
                termino = String.format("%.4f·x^%d", Math.abs(c), i);
            }

            if (primero) {
                sb.append(c < 0 ? "-" : "").append(termino);
                primero = false;
            } else {
                sb.append(c < 0 ? " - " : " + ").append(termino);
            }
        }

        return sb.toString();
    }

    public static String exponencial(double c, double a) {
        return String.format("y = %.4f · e^(%.4f·x)", c, a);
    }

    public static String potencial(double c, double a) {
        return String.format("T = %.6f · x^%.4f", c, a);
    }
}
