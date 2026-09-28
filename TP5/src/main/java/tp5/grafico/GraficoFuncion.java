package tp5.grafico;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraficoFuncion extends JPanel {

    private String titulo;
    private double tMin;
    private double tMax;
    private List<Curva> curvas;
    private String etiquetaX;
    private String etiquetaY;

    public GraficoFuncion(
            String titulo,
            double tMin,
            double tMax,
            List<Curva> curvas,
            String etiquetaX,
            String etiquetaY) {

        this.titulo = titulo;
        this.tMin = tMin;
        this.tMax = tMax;
        this.curvas = curvas;
        this.etiquetaX = etiquetaX;
        this.etiquetaY = etiquetaY;

        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D grafico = (Graphics2D) g;

        grafico.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int ancho = getWidth();
        int alto = getHeight();

        int margenIzquierdo = 80;
        int margenDerecho = 40;
        int margenSuperior = 60;
        int margenInferior = 60;

        int cantidadMuestrasCurva = 400;

        // ==========================================
        // RANGO DE VALORES Y
        // ==========================================

        double yMinDatos = Double.POSITIVE_INFINITY;
        double yMaxDatos = Double.NEGATIVE_INFINITY;

        for (Curva curva : curvas) {
            for (int i = 0; i < cantidadMuestrasCurva; i++) {
                double t = tMin + (tMax - tMin) * i / (cantidadMuestrasCurva - 1);
                double y = curva.funcion.applyAsDouble(t);
                if (Double.isNaN(y) || Double.isInfinite(y)) continue;
                if (y > yMaxDatos) yMaxDatos = y;
                if (y < yMinDatos) yMinDatos = y;
            }
        }

        if (Double.isInfinite(yMinDatos) || Double.isInfinite(yMaxDatos)) {
            yMinDatos = 0;
            yMaxDatos = 1;
        }

        double margenY = (yMaxDatos - yMinDatos) * 0.12;
        if (margenY == 0) margenY = 1;

        double yMin = yMinDatos - margenY;
        double yMax = yMaxDatos + margenY;

        // ==========================================
        // TÍTULO
        // ==========================================

        grafico.setColor(Color.BLACK);
        grafico.setFont(new Font("Arial", Font.BOLD, 18));
        grafico.drawString(titulo, margenIzquierdo, 28);

        // ==========================================
        // POSICIÓN DEL GRÁFICO
        // ==========================================

        int izquierda = margenIzquierdo;
        int derecha = ancho - margenDerecho;
        int arriba = margenSuperior;
        int abajo = alto - margenInferior;

        // ==========================================
        // CUADRÍCULA
        // ==========================================

        grafico.setColor(new Color(220, 220, 220));
        grafico.setStroke(new BasicStroke(1));

        int lineasVerticales = 10;
        int lineasHorizontales = 8;

        for (int i = 0; i <= lineasVerticales; i++) {
            int x = izquierda + (derecha - izquierda) * i / lineasVerticales;
            grafico.drawLine(x, arriba, x, abajo);
        }

        for (int i = 0; i <= lineasHorizontales; i++) {
            int y = arriba + (abajo - arriba) * i / lineasHorizontales;
            grafico.drawLine(izquierda, y, derecha, y);
        }

        // ==========================================
        // EJES
        // ==========================================

        grafico.setColor(Color.BLACK);
        grafico.setStroke(new BasicStroke(2));

        int posicionCeroY = convertirY(0, arriba, abajo, yMin, yMax);
        if (posicionCeroY >= arriba && posicionCeroY <= abajo) {
            grafico.drawLine(izquierda, posicionCeroY, derecha, posicionCeroY);
        }

        grafico.drawLine(izquierda, arriba, izquierda, abajo);

        // ==========================================
        // VALORES DE LOS EJES
        // ==========================================

        grafico.setFont(new Font("Arial", Font.PLAIN, 11));
        grafico.setColor(Color.BLACK);

        for (int i = 0; i <= lineasVerticales; i++) {
            double t = tMin + (tMax - tMin) * i / lineasVerticales;
            int px = izquierda + (derecha - izquierda) * i / lineasVerticales;
            grafico.drawString(String.format("%.1f", t), px - 12, abajo + 18);
        }

        for (int i = 0; i <= lineasHorizontales; i++) {
            double valor = yMax - (yMax - yMin) * i / lineasHorizontales;
            int py = arriba + (abajo - arriba) * i / lineasHorizontales;
            grafico.drawString(String.format("%.2f", valor), 8, py + 4);
        }

        // ==========================================
        // CURVAS
        // ==========================================

        for (Curva curva : curvas) {

            grafico.setColor(curva.color);
            grafico.setStroke(new BasicStroke(2.2f));

            int xPixelAnterior = 0;
            int yPixelAnterior = 0;
            boolean hayPuntoAnterior = false;

            for (int i = 0; i < cantidadMuestrasCurva; i++) {

                double t = tMin + (tMax - tMin) * i / (cantidadMuestrasCurva - 1);
                double y = curva.funcion.applyAsDouble(t);

                if (Double.isNaN(y) || Double.isInfinite(y)) {
                    hayPuntoAnterior = false;
                    continue;
                }

                int xPixel = convertirX(t, izquierda, derecha, tMin, tMax);
                int yPixel = convertirY(y, arriba, abajo, yMin, yMax);

                if (hayPuntoAnterior) {
                    grafico.drawLine(xPixelAnterior, yPixelAnterior, xPixel, yPixel);
                }

                xPixelAnterior = xPixel;
                yPixelAnterior = yPixel;
                hayPuntoAnterior = true;
            }
        }

        // ==========================================
        // NOMBRE DE LOS EJES
        // ==========================================

        grafico.setColor(Color.BLACK);
        grafico.setFont(new Font("Arial", Font.BOLD, 13));
        grafico.drawString(etiquetaX, derecha - 10, abajo + 38);
        grafico.drawString(etiquetaY, izquierda - 20, arriba - 8);

        // ==========================================
        // LEYENDA
        // ==========================================

        if (curvas.size() > 1) {
            dibujarLeyenda(grafico, izquierda, arriba);
        }
    }

    private void dibujarLeyenda(Graphics2D grafico, int izquierda, int arriba) {

        int anchoCaja = 0;

        grafico.setFont(new Font("Arial", Font.PLAIN, 12));
        FontMetrics metrica = grafico.getFontMetrics();

        for (Curva curva : curvas) {
            anchoCaja = Math.max(anchoCaja, metrica.stringWidth(curva.etiqueta) + 30);
        }

        int filas = curvas.size();
        int altoFila = 18;
        int altoCaja = filas * altoFila + 10;

        int x0 = izquierda + 10;
        int y0 = arriba + 10;

        grafico.setColor(new Color(255, 255, 255, 220));
        grafico.fillRect(x0, y0, anchoCaja, altoCaja);
        grafico.setColor(Color.GRAY);
        grafico.drawRect(x0, y0, anchoCaja, altoCaja);

        int y = y0 + 16;

        for (Curva curva : curvas) {
            grafico.setColor(curva.color);
            grafico.setStroke(new BasicStroke(2.5f));
            grafico.drawLine(x0 + 4, y - 4, x0 + 16, y - 4);
            grafico.setColor(Color.BLACK);
            grafico.drawString(curva.etiqueta, x0 + 24, y);
            y += altoFila;
        }
    }

    private int convertirX(double t, int izquierda, int derecha, double tMin, double tMax) {
        double proporcion = (t - tMin) / (tMax - tMin);
        return (int) (izquierda + proporcion * (derecha - izquierda));
    }

    private int convertirY(double valor, int arriba, int abajo, double minimo, double maximo) {
        double proporcion = (valor - minimo) / (maximo - minimo);
        return (int) (abajo - proporcion * (abajo - arriba));
    }
}
