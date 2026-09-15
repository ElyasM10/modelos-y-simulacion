package tp4.grafico;

import javax.swing.*;
import java.awt.*;

public class GraficoBarras extends JPanel {

    private String titulo;
    private String[] categorias;
    private double[] valores;
    private String etiquetaY;
    private Color color;

    public GraficoBarras(
            String titulo,
            String[] categorias,
            double[] valores,
            String etiquetaY,
            Color color) {

        this.titulo = titulo;
        this.categorias = categorias;
        this.valores = valores;
        this.etiquetaY = etiquetaY;
        this.color = color;

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
        int margenInferior = 70;

        // ==========================================
        // RANGO DE VALORES
        // ==========================================

        double valorMax = max(valores);
        double valorMin = Math.min(0, min(valores));

        double margenSup = (valorMax - valorMin) * 0.15;

        if (margenSup == 0) {
            margenSup = 1;
        }

        double yMax = valorMax + margenSup;
        double yMin = valorMin;

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
        // CUADRÍCULA Y EJE Y
        // ==========================================

        int lineasHorizontales = 8;

        grafico.setColor(new Color(220, 220, 220));
        grafico.setStroke(new BasicStroke(1));

        for (int i = 0; i <= lineasHorizontales; i++) {
            int y = arriba + (abajo - arriba) * i / lineasHorizontales;
            grafico.drawLine(izquierda, y, derecha, y);
        }

        grafico.setFont(new Font("Arial", Font.PLAIN, 11));
        grafico.setColor(Color.BLACK);

        for (int i = 0; i <= lineasHorizontales; i++) {
            double valor = yMax - (yMax - yMin) * i / lineasHorizontales;
            int y = arriba + (abajo - arriba) * i / lineasHorizontales;
            grafico.drawString(String.format("%.1f", valor), 8, y + 4);
        }

        // ==========================================
        // EJES
        // ==========================================

        grafico.setColor(Color.BLACK);
        grafico.setStroke(new BasicStroke(2));

        int posicionCeroY = convertirY(0, arriba, abajo, yMin, yMax);

        grafico.drawLine(izquierda, posicionCeroY, derecha, posicionCeroY);
        grafico.drawLine(izquierda, arriba, izquierda, abajo);

        // ==========================================
        // BARRAS
        // ==========================================

        int cantidad = valores.length;
        int anchoDisponible = derecha - izquierda;
        int anchoCategoria = anchoDisponible / cantidad;
        int anchoBarra = (int) (anchoCategoria * 0.5);

        grafico.setFont(new Font("Arial", Font.PLAIN, 12));

        for (int i = 0; i < cantidad; i++) {

            int centroX = izquierda + anchoCategoria * i + anchoCategoria / 2;

            int yValor = convertirY(valores[i], arriba, abajo, yMin, yMax);

            int yBarra = Math.min(yValor, posicionCeroY);
            int altoBarra = Math.abs(posicionCeroY - yValor);

            grafico.setColor(color);
            grafico.fillRect(centroX - anchoBarra / 2, yBarra, anchoBarra, Math.max(altoBarra, 1));

            grafico.setColor(color.darker());
            grafico.drawRect(centroX - anchoBarra / 2, yBarra, anchoBarra, Math.max(altoBarra, 1));

            // valor sobre la barra
            grafico.setColor(Color.BLACK);
            String texto = String.format("%.2f", valores[i]);
            FontMetrics metrica = grafico.getFontMetrics();
            int anchoTexto = metrica.stringWidth(texto);
            grafico.drawString(texto, centroX - anchoTexto / 2, yBarra - 6);

            // etiqueta de la categoría
            String categoria = categorias[i];
            int anchoCategoriaTexto = metrica.stringWidth(categoria);
            grafico.drawString(categoria, centroX - anchoCategoriaTexto / 2, abajo + 20);
        }

        // ==========================================
        // NOMBRE DEL EJE Y
        // ==========================================

        grafico.setColor(Color.BLACK);
        grafico.setFont(new Font("Arial", Font.BOLD, 13));
        grafico.drawString(etiquetaY, izquierda - 20, arriba - 10);
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
