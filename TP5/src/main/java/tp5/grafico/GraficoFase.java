package tp5.grafico;

import javax.swing.*;
import java.awt.*;

// A diferencia de GraficoFuncion (que grafica y=f(x)), una órbita cerrada del retrato de
// fase no es una función de x (hay dos valores de y para la mayoría de los x): se grafica
// directamente la secuencia de puntos (x[i], y[i]) de la trayectoria, uniendo consecutivos.
public class GraficoFase extends JPanel {

    private final String titulo;
    private final double[] x;
    private final double[] y;
    private final String etiquetaX;
    private final String etiquetaY;
    private final double[] puntoEquilibrio; // puede ser null

    public GraficoFase(String titulo, double[] x, double[] y, String etiquetaX, String etiquetaY, double[] puntoEquilibrio) {
        this.titulo = titulo;
        this.x = x;
        this.y = y;
        this.etiquetaX = etiquetaX;
        this.etiquetaY = etiquetaY;
        this.puntoEquilibrio = puntoEquilibrio;
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D grafico = (Graphics2D) g;
        grafico.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();

        int margenIzquierdo = 80;
        int margenDerecho = 40;
        int margenSuperior = 60;
        int margenInferior = 60;

        double xMin = min(x), xMax = max(x);
        double yMin = min(y), yMax = max(y);

        double margenX = (xMax - xMin) * 0.1;
        double margenY = (yMax - yMin) * 0.1;
        if (margenX == 0) margenX = 1;
        if (margenY == 0) margenY = 1;

        xMin -= margenX;
        xMax += margenX;
        yMin -= margenY;
        yMax += margenY;

        grafico.setColor(Color.BLACK);
        grafico.setFont(new Font("Arial", Font.BOLD, 18));
        grafico.drawString(titulo, margenIzquierdo, 28);

        int izquierda = margenIzquierdo;
        int derecha = ancho - margenDerecho;
        int arriba = margenSuperior;
        int abajo = alto - margenInferior;

        grafico.setColor(new Color(220, 220, 220));
        grafico.setStroke(new BasicStroke(1));
        int lineasVerticales = 10, lineasHorizontales = 8;
        for (int i = 0; i <= lineasVerticales; i++) {
            int px = izquierda + (derecha - izquierda) * i / lineasVerticales;
            grafico.drawLine(px, arriba, px, abajo);
        }
        for (int i = 0; i <= lineasHorizontales; i++) {
            int py = arriba + (abajo - arriba) * i / lineasHorizontales;
            grafico.drawLine(izquierda, py, derecha, py);
        }

        grafico.setColor(Color.BLACK);
        grafico.setStroke(new BasicStroke(2));
        grafico.drawLine(izquierda, abajo, derecha, abajo);
        grafico.drawLine(izquierda, arriba, izquierda, abajo);

        grafico.setFont(new Font("Arial", Font.PLAIN, 11));
        for (int i = 0; i <= lineasVerticales; i++) {
            double valor = xMin + (xMax - xMin) * i / lineasVerticales;
            int px = izquierda + (derecha - izquierda) * i / lineasVerticales;
            grafico.drawString(String.format("%.1f", valor), px - 12, abajo + 18);
        }
        for (int i = 0; i <= lineasHorizontales; i++) {
            double valor = yMax - (yMax - yMin) * i / lineasHorizontales;
            int py = arriba + (abajo - arriba) * i / lineasHorizontales;
            grafico.drawString(String.format("%.1f", valor), 8, py + 4);
        }

        // trayectoria
        grafico.setColor(new Color(90, 60, 160));
        grafico.setStroke(new BasicStroke(2.0f));
        int xPixelAnterior = convertirX(x[0], izquierda, derecha, xMin, xMax);
        int yPixelAnterior = convertirY(y[0], arriba, abajo, yMin, yMax);
        for (int i = 1; i < x.length; i++) {
            int xPixel = convertirX(x[i], izquierda, derecha, xMin, xMax);
            int yPixel = convertirY(y[i], arriba, abajo, yMin, yMax);
            grafico.drawLine(xPixelAnterior, yPixelAnterior, xPixel, yPixel);
            xPixelAnterior = xPixel;
            yPixelAnterior = yPixel;
        }

        // punto inicial
        int x0 = convertirX(x[0], izquierda, derecha, xMin, xMax);
        int y0 = convertirY(y[0], arriba, abajo, yMin, yMax);
        grafico.setColor(Color.BLACK);
        grafico.fillOval(x0 - 5, y0 - 5, 10, 10);

        // punto de equilibrio de coexistencia
        if (puntoEquilibrio != null) {
            int xe = convertirX(puntoEquilibrio[0], izquierda, derecha, xMin, xMax);
            int ye = convertirY(puntoEquilibrio[1], arriba, abajo, yMin, yMax);
            grafico.setColor(new Color(200, 60, 40));
            grafico.fillOval(xe - 5, ye - 5, 10, 10);
            grafico.drawString("equilibrio", xe + 8, ye - 8);
        }

        grafico.setColor(Color.BLACK);
        grafico.setFont(new Font("Arial", Font.BOLD, 13));
        int anchoEtiquetaX = grafico.getFontMetrics().stringWidth(etiquetaX);
        grafico.drawString(etiquetaX, derecha - anchoEtiquetaX, abajo + 38);
        grafico.drawString(etiquetaY, izquierda - 20, arriba - 8);
    }

    private int convertirX(double v, int izquierda, int derecha, double min, double max) {
        return (int) (izquierda + (v - min) / (max - min) * (derecha - izquierda));
    }

    private int convertirY(double v, int arriba, int abajo, double min, double max) {
        return (int) (abajo - (v - min) / (max - min) * (abajo - arriba));
    }

    private double min(double[] v) {
        double m = v[0];
        for (double d : v) if (d < m) m = d;
        return m;
    }

    private double max(double[] v) {
        double m = v[0];
        for (double d : v) if (d > m) m = d;
        return m;
    }
}
