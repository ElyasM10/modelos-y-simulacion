package tp3.grafico;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraficoAjuste extends JPanel {

    private String titulo;
    private double[] xDatos;
    private double[] yDatos;
    private List<Curva> curvas;

    public GraficoAjuste(
            String titulo,
            double[] xDatos,
            double[] yDatos,
            List<Curva> curvas) {

        this.titulo = titulo;
        this.xDatos = xDatos;
        this.yDatos = yDatos;
        this.curvas = curvas;

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
        int margenSuperior = 90;
        int margenInferior = 60;

        // ==========================================
        // RANGO DE VALORES
        // ==========================================

        double xMinDatos = min(xDatos);

        double xMin = xMinDatos;
        double xMax = max(xDatos);

        double margenX = (xMax - xMin) * 0.1;

        if (margenX == 0) {
            margenX = 1;
        }

        xMin -= margenX;
        xMax += margenX;

        // Si los datos no tienen valores negativos, no extendemos el rango
        // por debajo de 0: evita evaluar curvas con dominio restringido
        // (por ejemplo x^A con A no entero) en x negativos, donde no están
        // definidas.
        if (xMinDatos >= 0) {
            xMin = Math.max(xMin, 0);
        }

        double yMin = min(yDatos);
        double yMax = max(yDatos);

        int cantidadMuestrasCurva = 300;

        for (Curva curva : curvas) {

            for (int i = 0; i < cantidadMuestrasCurva; i++) {

                double x =
                        xMin
                                + (xMax - xMin)
                                * i
                                / (cantidadMuestrasCurva - 1);

                double y = curva.funcion.applyAsDouble(x);

                if (Double.isNaN(y) || Double.isInfinite(y)) {
                    continue;
                }

                if (y > yMax) yMax = y;
                if (y < yMin) yMin = y;
            }
        }

        double margenY = (yMax - yMin) * 0.15;

        if (margenY == 0) {
            margenY = 1;
        }

        yMax += margenY;
        yMin -= margenY;

        // ==========================================
        // TÍTULO
        // ==========================================

        grafico.setColor(Color.BLACK);

        grafico.setFont(new Font("Arial", Font.BOLD, 20));

        grafico.drawString(titulo, margenIzquierdo, 30);

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

        for (int i = 0; i <= lineasVerticales; i++) {

            double x = xMin + (xMax - xMin) * i / lineasVerticales;
            int px = izquierda + (derecha - izquierda) * i / lineasVerticales;

            grafico.drawString(String.format("%.1f", x), px - 12, abajo + 18);
        }

        for (int i = 0; i <= lineasHorizontales; i++) {

            double valor = yMax - (yMax - yMin) * i / lineasHorizontales;
            int py = arriba + (abajo - arriba) * i / lineasHorizontales;

            grafico.drawString(String.format("%.2f", valor), 8, py + 4);
        }

        // ==========================================
        // CURVAS AJUSTADAS
        // ==========================================

        for (Curva curva : curvas) {

            grafico.setColor(curva.color);
            grafico.setStroke(new BasicStroke(2.2f));

            int xPixelAnterior = 0;
            int yPixelAnterior = 0;
            boolean hayPuntoAnterior = false;

            for (int i = 0; i < cantidadMuestrasCurva; i++) {

                double x =
                        xMin
                                + (xMax - xMin)
                                * i
                                / (cantidadMuestrasCurva - 1);

                double y = curva.funcion.applyAsDouble(x);

                if (Double.isNaN(y) || Double.isInfinite(y)) {
                    hayPuntoAnterior = false;
                    continue;
                }

                int xPixel = convertirX(x, izquierda, derecha, xMin, xMax);
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
        // PUNTOS DE DATOS
        // ==========================================

        int radioPunto = 5;

        for (int i = 0; i < xDatos.length; i++) {

            int xPixel = convertirX(xDatos[i], izquierda, derecha, xMin, xMax);
            int yPixel = convertirY(yDatos[i], arriba, abajo, yMin, yMax);

            grafico.setColor(Color.BLACK);
            grafico.fillOval(xPixel - radioPunto, yPixel - radioPunto, radioPunto * 2, radioPunto * 2);

            grafico.setColor(Color.WHITE);
            grafico.setStroke(new BasicStroke(1.5f));
            grafico.drawOval(xPixel - radioPunto, yPixel - radioPunto, radioPunto * 2, radioPunto * 2);
        }

        // ==========================================
        // NOMBRE DE LOS EJES
        // ==========================================

        grafico.setColor(Color.BLACK);
        grafico.setFont(new Font("Arial", Font.BOLD, 13));

        grafico.drawString("x", derecha - 10, abajo + 38);
        grafico.drawString("y", izquierda - 20, arriba - 8);

        // ==========================================
        // LEYENDA
        // ==========================================

        dibujarLeyenda(grafico, izquierda, arriba);
    }

    private void dibujarLeyenda(Graphics2D grafico, int izquierda, int arriba) {

        int anchoCaja = 0;

        grafico.setFont(new Font("Arial", Font.PLAIN, 12));

        FontMetrics metrica = grafico.getFontMetrics();

        anchoCaja = Math.max(anchoCaja, metrica.stringWidth("Datos") + 30);

        for (Curva curva : curvas) {
            anchoCaja = Math.max(anchoCaja, metrica.stringWidth(curva.etiqueta) + 30);
        }

        int filas = curvas.size() + 1;
        int altoFila = 18;
        int altoCaja = filas * altoFila + 10;

        int x0 = izquierda + 10;
        int y0 = arriba + 10;

        grafico.setColor(new Color(255, 255, 255, 220));
        grafico.fillRect(x0, y0, anchoCaja, altoCaja);

        grafico.setColor(Color.GRAY);
        grafico.drawRect(x0, y0, anchoCaja, altoCaja);

        int y = y0 + 16;

        grafico.setColor(Color.BLACK);
        grafico.fillOval(x0 + 8, y - 8, 8, 8);
        grafico.setColor(Color.BLACK);
        grafico.drawString("Datos", x0 + 24, y);

        y += altoFila;

        for (Curva curva : curvas) {

            grafico.setColor(curva.color);
            grafico.setStroke(new BasicStroke(2.5f));
            grafico.drawLine(x0 + 4, y - 4, x0 + 16, y - 4);

            grafico.setColor(Color.BLACK);
            grafico.drawString(curva.etiqueta, x0 + 24, y);

            y += altoFila;
        }
    }

    private int convertirX(double t, int izquierda, int derecha, double xMin, double xMax) {

        double proporcion = (t - xMin) / (xMax - xMin);

        return (int) (izquierda + proporcion * (derecha - izquierda));
    }

    private int convertirY(double valor, int arriba, int abajo, double minimo, double maximo) {

        double proporcion = (valor - minimo) / (maximo - minimo);

        return (int) (abajo - proporcion * (abajo - arriba));
    }

    private double min(double[] valores) {

        double m = valores[0];

        for (double v : valores) {
            if (v < m) m = v;
        }

        return m;
    }

    private double max(double[] valores) {

        double m = valores[0];

        for (double v : valores) {
            if (v > m) m = v;
        }

        return m;
    }
}
