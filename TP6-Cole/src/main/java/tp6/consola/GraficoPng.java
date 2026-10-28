package tp6.consola;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

// Gráfico 2D simple para la versión consola: varias series, eje X lineal o logarítmico, que se guarda como imagen PNG.
public class GraficoPng {

    private static class Serie {
        String nombre; double[] valoresX, valoresY; Color color; boolean dibujarPuntos;
    }

    public String titulo = "", etiquetaEjeX = "", etiquetaEjeY = "";
    public boolean ejeXLogaritmico = false;
    private final List<Serie> series = new ArrayList<>();

    public static final Color[] COLORES = {Color.RED, new Color(0, 130, 0), Color.BLUE, Color.ORANGE, Color.MAGENTA};

    public GraficoPng(String titulo, String etiquetaEjeX, String etiquetaEjeY, boolean ejeXLogaritmico) {
        this.titulo = titulo;
        this.etiquetaEjeX = etiquetaEjeX;
        this.etiquetaEjeY = etiquetaEjeY;
        this.ejeXLogaritmico = ejeXLogaritmico;
    }

    public void agregar(String nombre, double[] valoresX, double[] valoresY, Color color, boolean dibujarPuntos) {
        Serie serie = new Serie();
        serie.nombre = nombre;
        serie.valoresX = valoresX;
        serie.valoresY = valoresY;
        serie.color = color;
        serie.dibujarPuntos = dibujarPuntos;
        series.add(serie);
    }

    public void guardarPng(String ruta, int ancho, int alto) throws IOException {
        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D lienzo = imagen.createGraphics();
        lienzo.setColor(Color.WHITE);
        lienzo.fillRect(0, 0, ancho, alto);
        dibujar(lienzo, ancho, alto);
        lienzo.dispose();
        ImageIO.write(imagen, "png", new File(ruta));
    }

    // Convierte un valor del eje X a la escala usada para dibujar (log10 si el eje es logarítmico).
    private double escalaX(double valor) { return ejeXLogaritmico ? Math.log10(valor) : valor; }

    private void dibujar(Graphics2D lienzo, int ancho, int alto) {
        lienzo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int margenIzquierdo = 70, margenDerecho = 20, margenSuperior = 35, margenInferior = 50;
        int anchoGrafico = ancho - margenIzquierdo - margenDerecho;
        int altoGrafico = alto - margenSuperior - margenInferior;
        lienzo.setColor(Color.BLACK);
        lienzo.setFont(lienzo.getFont().deriveFont(Font.BOLD, 13f));
        lienzo.drawString(titulo, margenIzquierdo, 22);
        lienzo.setFont(lienzo.getFont().deriveFont(Font.PLAIN, 11f));
        if (series.isEmpty()) return;

        // Rango de datos
        double minimoX = Double.MAX_VALUE, maximoX = -Double.MAX_VALUE;
        double minimoY = Double.MAX_VALUE, maximoY = -Double.MAX_VALUE;
        for (Serie serie : series)
            for (int i = 0; i < serie.valoresX.length; i++) {
                minimoX = Math.min(minimoX, escalaX(serie.valoresX[i]));
                maximoX = Math.max(maximoX, escalaX(serie.valoresX[i]));
                minimoY = Math.min(minimoY, serie.valoresY[i]);
                maximoY = Math.max(maximoY, serie.valoresY[i]);
            }
        double margenY = (maximoY - minimoY) * 0.05 + 1e-9;
        minimoY -= margenY;
        maximoY += margenY;
        if (maximoX == minimoX) maximoX = minimoX + 1;

        // Marco y grilla (5 divisiones en cada eje)
        lienzo.drawRect(margenIzquierdo, margenSuperior, anchoGrafico, altoGrafico);
        for (int division = 0; division <= 5; division++) {
            int pixelX = margenIzquierdo + anchoGrafico * division / 5;
            int pixelY = margenSuperior + altoGrafico - altoGrafico * division / 5;
            lienzo.setColor(new Color(225, 225, 225));
            lienzo.drawLine(pixelX, margenSuperior, pixelX, margenSuperior + altoGrafico);
            lienzo.drawLine(margenIzquierdo, pixelY, margenIzquierdo + anchoGrafico, pixelY);
            lienzo.setColor(Color.BLACK);
            double valorX = minimoX + (maximoX - minimoX) * division / 5;
            double valorY = minimoY + (maximoY - minimoY) * division / 5;
            lienzo.drawString(ejeXLogaritmico ? String.format("1e%.1f", valorX) : String.format("%.4g", valorX),
                    pixelX - 18, margenSuperior + altoGrafico + 16);
            lienzo.drawString(String.format("%.4g", valorY), 8, pixelY + 4);
        }
        lienzo.drawString(etiquetaEjeX, margenIzquierdo + anchoGrafico / 2 - 30, alto - 8);
        lienzo.drawString(etiquetaEjeY, 8, margenSuperior - 6);

        // Series
        int posicionLeyendaY = margenSuperior + 14;
        for (Serie serie : series) {
            lienzo.setColor(serie.color);
            int pixelXAnterior = 0, pixelYAnterior = 0;
            for (int i = 0; i < serie.valoresX.length; i++) {
                int pixelX = margenIzquierdo + (int) ((escalaX(serie.valoresX[i]) - minimoX) / (maximoX - minimoX) * anchoGrafico);
                int pixelY = margenSuperior + altoGrafico - (int) ((serie.valoresY[i] - minimoY) / (maximoY - minimoY) * altoGrafico);
                if (serie.dibujarPuntos) lienzo.drawOval(pixelX - 2, pixelY - 2, 4, 4);
                else if (i > 0) lienzo.drawLine(pixelXAnterior, pixelYAnterior, pixelX, pixelY);
                pixelXAnterior = pixelX;
                pixelYAnterior = pixelY;
            }
            // Leyenda
            lienzo.fillRect(margenIzquierdo + anchoGrafico - 130, posicionLeyendaY - 8, 10, 10);
            lienzo.setColor(Color.BLACK);
            lienzo.drawString(serie.nombre, margenIzquierdo + anchoGrafico - 115, posicionLeyendaY + 1);
            posicionLeyendaY += 15;
        }
    }
}
