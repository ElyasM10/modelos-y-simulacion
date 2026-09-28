package tp5.diagrama;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

// Dibuja el diagrama en bloques del Ejercicio 4: para un sistema
// ẋ = a11·x + a12·y ,  ẏ = a21·x + a22·y
// arma, por cada variable de estado, un sumador (+) seguido de un integrador (1/s), y
// un lazo de realimentación con caja de ganancia por cada coeficiente no nulo. Layout
// (coordenadas "de datos", en un sistema propio con eje Y hacia arriba) portado 1 a 1
// desde el generador original en Python/matplotlib (ver generar_diagramas.py.bak).
public class DiagramaBloques extends JPanel {

    // ---- coordenadas del esqueleto (en unidades de datos, Y hacia arriba) ----
    private static final double[] S1 = {1.3, 4.6};
    private static final double[] I1 = {3.3, 4.6};
    private static final double[] BX = {6.3, 4.6};
    private static final double[] XOUT = {8.3, 4.6};

    private static final double[] S2 = {1.3, 1.0};
    private static final double[] I2 = {3.3, 1.0};
    private static final double[] BY = {6.3, 1.0};
    private static final double[] YOUT = {8.3, 1.0};

    private static final double LANE_A11 = 5.5;
    private static final double LANE_A12 = 3.3;
    private static final double LANE_A21 = 2.3;
    private static final double LANE_A22 = 0.1;

    private static final double X_MIN_D = -0.3, X_MAX_D = 9.8;
    private static final double Y_MIN_D = -0.6, Y_MAX_D = 6.1;

    private final String nombre;
    private final double a11, a12, a21, a22;
    private final String ecuacionTexto;

    private double escala;
    private double offX, offY;

    public DiagramaBloques(String nombre, double a11, double a12, double a21, double a22, String ecuacionTexto) {
        this.nombre = nombre;
        this.a11 = a11;
        this.a12 = a12;
        this.a21 = a21;
        this.a22 = a22;
        this.ecuacionTexto = ecuacionTexto;
        setBackground(Color.WHITE);
    }

    private static String fmt(double n) {
        if (n == Math.rint(n)) {
            return String.valueOf((long) n);
        }
        return String.valueOf(n);
    }

    private double px(double xDato) {
        return offX + (xDato - X_MIN_D) * escala;
    }

    private double py(double yDato) {
        return offY + (Y_MAX_D - yDato) * escala;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D gr = (Graphics2D) g;
        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int margen = 40;
        int tituloAlto = 40;

        double anchoDisp = getWidth() - 2.0 * margen;
        double altoDisp = getHeight() - 2.0 * margen - tituloAlto;

        escala = Math.min(anchoDisp / (X_MAX_D - X_MIN_D), altoDisp / (Y_MAX_D - Y_MIN_D));

        double anchoUsado = (X_MAX_D - X_MIN_D) * escala;
        double altoUsado = (Y_MAX_D - Y_MIN_D) * escala;

        offX = margen + (anchoDisp - anchoUsado) / 2.0;
        offY = margen + tituloAlto + (altoDisp - altoUsado) / 2.0;

        // ---- título ----
        gr.setColor(Color.BLACK);
        gr.setFont(new Font("Arial", Font.PLAIN, 15));
        String titulo = "(" + nombre + ")   " + ecuacionTexto;
        FontMetrics fm = gr.getFontMetrics();
        gr.drawString(titulo, (int) (getWidth() / 2 - fm.stringWidth(titulo) / 2.0), 26);

        gr.setStroke(new BasicStroke(1.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // ---- sumadores ----
        dibujarSumador(gr, S1);
        dibujarSumador(gr, S2);

        // ---- sumador -> integrador -> salida, por renglón ----
        dibujarRenglon(gr, S1, I1, BX, XOUT, "x");
        dibujarRenglon(gr, S2, I2, BY, YOUT, "y");

        // ---- lazos de realimentación ----
        // a11: x -> S1, entra por arriba (carril por encima del renglón de x)
        feedback(gr, BX, XOUT[1], LANE_A11, S1, true, a11);
        // a12: y -> S1, entra por abajo
        feedback(gr, BY, YOUT[1], LANE_A12, S1, false, a12);
        // a21: x -> S2, entra por arriba
        feedback(gr, BX, XOUT[1], LANE_A21, S2, true, a21);
        // a22: y -> S2, entra por abajo (carril por debajo del renglón de y)
        feedback(gr, BY, YOUT[1], LANE_A22, S2, false, a22);
    }

    private void dibujarSumador(Graphics2D gr, double[] c) {
        double r = 0.3 * escala;
        double cx = px(c[0]);
        double cy = py(c[1]);
        gr.setColor(Color.WHITE);
        Ellipse2D circulo = new Ellipse2D.Double(cx - r, cy - r, 2 * r, 2 * r);
        gr.fill(circulo);
        gr.setColor(Color.BLACK);
        gr.draw(circulo);
        gr.setFont(new Font("Arial", Font.PLAIN, (int) (0.5 * escala)));
        FontMetrics fm = gr.getFontMetrics();
        gr.drawString("+", (float) (cx - fm.stringWidth("+") / 2.0), (float) (cy + fm.getAscent() / 2.0 - 2));
    }

    private void dibujarRenglon(Graphics2D gr, double[] s, double[] i, double[] b, double[] out, String etiqueta) {

        // flecha sumador -> integrador
        flecha(gr, px(s[0] + 0.3), py(s[1]), px(i[0] - 0.5), py(i[1]));

        // caja del integrador
        double rx = px(i[0] - 0.5);
        double ry = py(i[1] + 0.35);
        double ancho = (1.0) * escala;
        double alto = (0.7) * escala;
        gr.setColor(Color.WHITE);
        Rectangle2D rect = new Rectangle2D.Double(rx, ry, ancho, alto);
        gr.fill(rect);
        gr.setColor(Color.BLACK);
        gr.draw(rect);
        gr.setFont(new Font("Arial", Font.PLAIN, (int) (0.42 * escala)));
        centrarTexto(gr, "1/s", px(i[0]), py(i[1]));

        // línea integrador -> punto de rama
        gr.setColor(Color.BLACK);
        gr.draw(new Line2D.Double(px(i[0] + 0.5), py(i[1]), px(b[0]), py(i[1])));

        // flecha punto de rama -> salida
        flecha(gr, px(b[0]), py(i[1]), px(out[0] - 0.05), py(out[1]));

        // etiqueta de salida (x/y), negrita
        gr.setFont(new Font("Arial", Font.BOLD, (int) (0.5 * escala)));
        gr.drawString(etiqueta, (float) px(out[0] + 0.25), (float) (py(out[1]) + gr.getFontMetrics().getAscent() / 2.0 - 2));

        // punto de rama (bolita rellena)
        double rb = 0.045 * escala;
        gr.setColor(Color.BLACK);
        gr.fill(new Ellipse2D.Double(px(b[0]) - rb, py(i[1]) - rb, 2 * rb, 2 * rb));
    }

    private void feedback(
            Graphics2D gr,
            double[] origenBranch,
            double origenY,
            double laneY,
            double[] sumador,
            boolean entraPorArriba,
            double coef) {

        if (coef == 0) {
            return;
        }

        double xBranch = origenBranch[0];

        // tramo vertical desde la rama hasta el carril
        gr.setColor(Color.BLACK);
        gr.draw(new Line2D.Double(px(xBranch), py(origenY), px(xBranch), py(laneY)));

        // tramo horizontal hasta la vertical del sumador
        double gx = (xBranch + sumador[0]) / 2.0;
        gr.draw(new Line2D.Double(px(xBranch), py(laneY), px(sumador[0] + 0.85), py(laneY)));

        // caja de ganancia, sobre el tramo horizontal
        dibujarGanancia(gr, gx + 0.6, laneY, fmt(coef));

        // tramo vertical final entrando al sumador (con flecha)
        double yDestino = entraPorArriba ? sumador[1] + 0.3 : sumador[1] - 0.3;
        flecha(gr, px(sumador[0] + 0.85), py(laneY), px(sumador[0] + 0.85), py(yDestino));
        gr.draw(new Line2D.Double(px(sumador[0] + 0.85), py(yDestino), px(sumador[0]), py(yDestino)));
        flecha(gr, px(sumador[0] + 0.15), py(yDestino), px(sumador[0]), py(yDestino));
    }

    private void dibujarGanancia(Graphics2D gr, double xDato, double yDato, String texto) {
        double w = 0.64 * escala;
        double h = 0.44 * escala;
        double x = px(xDato) - w / 2.0;
        double y = py(yDato) - h / 2.0;
        gr.setColor(Color.WHITE);
        Rectangle2D rect = new Rectangle2D.Double(x, y, w, h);
        gr.fill(rect);
        gr.setColor(Color.BLACK);
        gr.draw(rect);
        gr.setFont(new Font("Arial", Font.PLAIN, (int) (0.4 * escala)));
        centrarTexto(gr, texto, px(xDato), py(yDato));
    }

    private void centrarTexto(Graphics2D gr, String texto, double cx, double cy) {
        FontMetrics fm = gr.getFontMetrics();
        gr.drawString(texto, (float) (cx - fm.stringWidth(texto) / 2.0), (float) (cy + fm.getAscent() / 2.0 - 2));
    }

    // Línea con punta de flecha en el destino (estilo "-|>", como FancyArrowPatch).
    private void flecha(Graphics2D gr, double x1, double y1, double x2, double y2) {

        gr.setColor(Color.BLACK);
        gr.draw(new Line2D.Double(x1, y1, x2, y2));

        double angulo = Math.atan2(y2 - y1, x2 - x1);
        double largo = 10;
        double ancho = Math.toRadians(18);

        GeneralPath punta = new GeneralPath();
        punta.moveTo(x2, y2);
        punta.lineTo(x2 - largo * Math.cos(angulo - ancho), y2 - largo * Math.sin(angulo - ancho));
        punta.lineTo(x2 - largo * Math.cos(angulo + ancho), y2 - largo * Math.sin(angulo + ancho));
        punta.closePath();

        gr.fill(punta);
    }
}
