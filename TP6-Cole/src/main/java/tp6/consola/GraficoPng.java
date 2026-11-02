package tp6.consola;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/*
 * Gráfico 2D para la versión consola: varias series, eje X lineal o logarítmico, líneas verticales y puntos destacados.
 * Se dibuja a mano con Java 2D (java.awt) y se guarda como imagen PNG, sola (guardarPng) o junto con otros paneles (LaminaPng).
 *
 * Cómo se usa:
 *   1) new GraficoPng(titulo, etiquetaEjeX, etiquetaEjeY, ejeXLogaritmico)
 *   2) agregar(...) una vez por cada curva (línea) o conjunto de mediciones (puntos)
 *   3) opcionalmente: agregarLineaVertical(...), agregarPunto(...), fijarLimitesX(...), fijarLimitesY(...), sinLeyenda()
 *   4) guardarPng(...) o pasarlo a LaminaPng.guardar(...)
 */
public class GraficoPng implements PanelPng {

    // Colores de las series, en orden: rojo, verde, azul, naranja, violeta
    public static final Color[] COLORES = {new Color(0xC0392B), new Color(0x1B7F3B), new Color(0x2F6FED), new Color(0xE08A00), new Color(0x8E44AD)};
    private static final Color TEXTO = new Color(0x1B2430);
    private static final Color SECUNDARIO = new Color(0x566577);
    private static final Color GRILLA = new Color(0xE3E8EE);
    private static final int FUENTE = 21; // tamaño de la letra, en píxeles

    // Una curva (línea) o un conjunto de mediciones (puntos)
    private static class Serie {
        String nombre; double[] valoresX, valoresY; Color color; boolean dibujarPuntos;
    }

    // Una recta vertical punteada en x (por ejemplo, la frecuencia característica fc), con un texto al lado
    private static class LineaVertical {
        double x; String texto;
    }

    // Un punto resaltado con un círculo y un texto. El texto se dibuja desplazado (desplazamientoX, desplazamientoY) píxeles
    private static class Punto {
        double x, y; String texto; Color color; int desplazamientoX, desplazamientoY;
    }

    private final String titulo, etiquetaEjeX, etiquetaEjeY;
    private final boolean ejeXLogaritmico;
    private final List<Serie> series = new ArrayList<>();
    private final List<LineaVertical> lineasVerticales = new ArrayList<>();
    private final List<Punto> puntos = new ArrayList<>();
    private boolean mostrarLeyenda = true;
    private boolean leyendaAbajoCentro = false; // por defecto la leyenda va arriba a la derecha
    // Límites fijados a mano (en unidades reales, no en log10). NaN significa "calcular solos a partir de los datos"
    private double limiteMinimoX = Double.NaN, limiteMaximoX = Double.NaN, limiteMinimoY = Double.NaN, limiteMaximoY = Double.NaN;

    public GraficoPng(String titulo, String etiquetaEjeX, String etiquetaEjeY, boolean ejeXLogaritmico) {
        this.titulo = titulo;
        this.etiquetaEjeX = etiquetaEjeX;
        this.etiquetaEjeY = etiquetaEjeY;
        this.ejeXLogaritmico = ejeXLogaritmico;
    }

    // Agrega una serie de datos. dibujarPuntos = true: círculos (mediciones); false: línea (curva del modelo)
    public void agregar(String nombre, double[] valoresX, double[] valoresY, Color color, boolean dibujarPuntos) {
        Serie serie = new Serie();
        serie.nombre = nombre;
        serie.valoresX = valoresX;
        serie.valoresY = valoresY;
        serie.color = color;
        serie.dibujarPuntos = dibujarPuntos;
        series.add(serie);
    }

    public void agregarLineaVertical(double x, String texto) {
        LineaVertical linea = new LineaVertical();
        linea.x = x;
        linea.texto = texto;
        lineasVerticales.add(linea);
    }

    // Si desplazamientoX es negativo, el texto termina (alineado a la derecha) antes del punto
    public void agregarPunto(double x, double y, String texto, Color color, int desplazamientoX, int desplazamientoY) {
        Punto punto = new Punto();
        punto.x = x;
        punto.y = y;
        punto.texto = texto;
        punto.color = color;
        punto.desplazamientoX = desplazamientoX;
        punto.desplazamientoY = desplazamientoY;
        puntos.add(punto);
    }

    public void fijarLimitesX(double minimo, double maximo) { limiteMinimoX = minimo; limiteMaximoX = maximo; }

    public void fijarLimitesY(double minimo, double maximo) { limiteMinimoY = minimo; limiteMaximoY = maximo; }

    public void sinLeyenda() { mostrarLeyenda = false; }

    // Pone la leyenda abajo, en el centro (útil en el Nyquist: el arco deja ese lugar libre)
    public void leyendaAbajoCentro() { leyendaAbajoCentro = true; }

    // Guarda el gráfico solo, en una imagen del tamaño indicado
    public void guardarPng(String ruta, int ancho, int alto) throws IOException {
        LaminaPng.guardar(ruta, ancho, alto, null, 1, this);
    }

    // Convierte un valor del eje X a la escala con que se dibuja (log10 si el eje es logarítmico)
    private double escalaX(double valor) { return ejeXLogaritmico ? Math.log10(valor) : valor; }

    @Override
    public void dibujar(Graphics2D lienzo, int x0, int y0, int ancho, int alto) {
        Font normal = lienzo.getFont().deriveFont(Font.PLAIN, (float) FUENTE);
        Font negrita = normal.deriveFont(Font.BOLD, FUENTE + 2f);
        lienzo.setFont(normal);
        FontMetrics medidas = lienzo.getFontMetrics();

        // Paso 1: zona del gráfico (la zona útil del panel menos los márgenes donde van los títulos y los números de los ejes)
        int margenIzquierdo = 112, margenDerecho = 30, margenSuperior = 58, margenInferior = 84;
        int px0 = x0 + margenIzquierdo, py0 = y0 + margenSuperior;
        int anchoGrafico = ancho - margenIzquierdo - margenDerecho;
        int altoGrafico = alto - margenSuperior - margenInferior;

        // Paso 2: rango de cada eje. X primero; Y después, mirando solo los puntos que caen dentro del rango de X
        double[] rangoX = rangoX();
        double minimoX = rangoX[0], maximoX = rangoX[1];
        double[] rangoY = rangoY(minimoX, maximoX);
        double minimoY = rangoY[0], maximoY = rangoY[1];

        // Paso 3: título del gráfico, centrado sobre la zona del gráfico
        lienzo.setFont(negrita);
        lienzo.setColor(TEXTO);
        lienzo.drawString(titulo, px0 + (anchoGrafico - lienzo.getFontMetrics().stringWidth(titulo)) / 2, y0 + 36);
        lienzo.setFont(normal);

        // Paso 4: marcas de los ejes. Se calculan los valores donde va cada número y se dibuja la grilla
        List<Double> marcasX = ejeXLogaritmico ? marcasLogaritmicas(minimoX, maximoX) : marcasLineales(minimoX, maximoX, Math.max(3, anchoGrafico / 150));
        double pasoY = pasoBonito((maximoY - minimoY) / Math.max(3, altoGrafico / 75));
        List<Double> marcasY = marcasLineales(minimoY, maximoY, pasoY);
        double pasoX = marcasX.size() > 1 ? marcasX.get(1) - marcasX.get(0) : 1;

        lienzo.setStroke(new BasicStroke(1.4f));
        for (double valor : marcasX) {
            int pixelX = pixelX(valor, px0, anchoGrafico, minimoX, maximoX);
            lienzo.setColor(GRILLA);
            lienzo.drawLine(pixelX, py0, pixelX, py0 + altoGrafico);
            lienzo.setColor(TEXTO);
            String texto = ejeXLogaritmico ? formatoFrecuencia(Math.pow(10, valor)) : numero(valor, pasoX);
            // El número va centrado bajo la marca, pero sin pasarse de los bordes del panel (si no, se cortaría en la imagen)
            int xTexto = Math.max(x0 + 4, Math.min(pixelX - medidas.stringWidth(texto) / 2, x0 + ancho - medidas.stringWidth(texto) - 4));
            lienzo.drawString(texto, xTexto, py0 + altoGrafico + FUENTE + 10);
        }
        for (double valor : marcasY) {
            int pixelY = pixelY(valor, py0, altoGrafico, minimoY, maximoY);
            lienzo.setColor(GRILLA);
            lienzo.drawLine(px0, pixelY, px0 + anchoGrafico, pixelY);
            lienzo.setColor(TEXTO);
            String texto = numero(valor, pasoY);
            lienzo.drawString(texto, px0 - 12 - medidas.stringWidth(texto), pixelY + FUENTE / 3);
        }
        // En el eje logarítmico se agregan marcas chicas en 2, 3... 9 de cada década (como en un papel logarítmico)
        if (ejeXLogaritmico) {
            lienzo.setColor(SECUNDARIO);
            for (int decada = (int) Math.floor(minimoX) - 1; decada <= (int) Math.ceil(maximoX); decada++)
                for (int multiplo = 2; multiplo <= 9; multiplo++) {
                    double valor = decada + Math.log10(multiplo);
                    if (valor > minimoX && valor < maximoX) {
                        int pixelX = pixelX(valor, px0, anchoGrafico, minimoX, maximoX);
                        lienzo.drawLine(pixelX, py0 + altoGrafico, pixelX, py0 + altoGrafico - 7);
                    }
                }
        }

        // Paso 5: marco del gráfico y nombres de los ejes (el del eje Y va girado 90 grados)
        lienzo.setColor(SECUNDARIO);
        lienzo.setStroke(new BasicStroke(1.8f));
        lienzo.drawRect(px0, py0, anchoGrafico, altoGrafico);
        lienzo.setColor(TEXTO);
        lienzo.drawString(etiquetaEjeX, px0 + (anchoGrafico - medidas.stringWidth(etiquetaEjeX)) / 2, y0 + alto - 14);
        AffineTransform original = lienzo.getTransform();
        lienzo.rotate(-Math.PI / 2, x0 + 30, py0 + altoGrafico / 2.0);
        lienzo.drawString(etiquetaEjeY, x0 + 30 - medidas.stringWidth(etiquetaEjeY) / 2, py0 + altoGrafico / 2 + 7);
        lienzo.setTransform(original);

        // Paso 6: series, líneas verticales y puntos. Todo lo que se pasa del marco se recorta (setClip)
        Shape recorteAnterior = lienzo.getClip();
        lienzo.setClip(px0, py0, anchoGrafico, altoGrafico);
        for (LineaVertical linea : lineasVerticales) {
            int pixelX = pixelX(escalaX(linea.x), px0, anchoGrafico, minimoX, maximoX);
            lienzo.setColor(SECUNDARIO);
            lienzo.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[]{8f, 8f}, 0f));
            lienzo.drawLine(pixelX, py0, pixelX, py0 + altoGrafico);
            lienzo.setFont(normal);
            lienzo.drawString(linea.texto, pixelX + 8, py0 + FUENTE + 8);
        }
        for (Serie serie : series) {
            lienzo.setColor(serie.color);
            if (serie.dibujarPuntos) {
                // Mediciones: un círculo relleno en cada dato
                for (int i = 0; i < serie.valoresX.length; i++)
                    lienzo.fill(new Ellipse2D.Double(pixelX(escalaX(serie.valoresX[i]), px0, anchoGrafico, minimoX, maximoX) - 4.5,
                            pixelY(serie.valoresY[i], py0, altoGrafico, minimoY, maximoY) - 4.5, 9, 9));
            } else {
                // Curvas: una línea continua que pasa por todos los datos
                // Acá los píxeles se dejan con decimales (sin redondear) para que la curva salga suave y no escalonada
                Path2D linea = new Path2D.Double();
                for (int i = 0; i < serie.valoresX.length; i++) {
                    double pixelX = px0 + (escalaX(serie.valoresX[i]) - minimoX) / (maximoX - minimoX) * anchoGrafico;
                    double pixelY = py0 + altoGrafico - (serie.valoresY[i] - minimoY) / (maximoY - minimoY) * altoGrafico;
                    if (i == 0) linea.moveTo(pixelX, pixelY); else linea.lineTo(pixelX, pixelY);
                }
                lienzo.setStroke(new BasicStroke(3.4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                lienzo.draw(linea);
            }
        }
        lienzo.setClip(recorteAnterior);
        for (Punto punto : puntos) {
            int pixelX = pixelX(escalaX(punto.x), px0, anchoGrafico, minimoX, maximoX);
            int pixelY = pixelY(punto.y, py0, altoGrafico, minimoY, maximoY);
            lienzo.setColor(punto.color);
            lienzo.fill(new Ellipse2D.Double(pixelX - 7, pixelY - 7, 14, 14));
            lienzo.setColor(TEXTO);
            lienzo.setFont(normal);
            int xTexto = punto.desplazamientoX >= 0 ? pixelX + punto.desplazamientoX : pixelX + punto.desplazamientoX - medidas.stringWidth(punto.texto);
            lienzo.drawString(punto.texto, xTexto, pixelY + punto.desplazamientoY);
        }

        // Paso 7: leyenda dentro del gráfico, arriba a la derecha (nombre y color de cada serie)
        if (mostrarLeyenda && !series.isEmpty()) {
            int anchoMaximo = 0;
            for (Serie serie : series) anchoMaximo = Math.max(anchoMaximo, medidas.stringWidth(serie.nombre));
            int anchoLeyenda = anchoMaximo + 70, altoLeyenda = series.size() * (FUENTE + 10) + 14;
            int xLeyenda = leyendaAbajoCentro ? px0 + (anchoGrafico - anchoLeyenda) / 2 : px0 + anchoGrafico - anchoLeyenda - 14;
            int yLeyenda = leyendaAbajoCentro ? py0 + altoGrafico - altoLeyenda - 14 : py0 + 14;
            lienzo.setColor(new Color(255, 255, 255, 225));
            lienzo.fillRoundRect(xLeyenda, yLeyenda, anchoLeyenda, altoLeyenda, 10, 10);
            lienzo.setColor(GRILLA);
            lienzo.setStroke(new BasicStroke(1.4f));
            lienzo.drawRoundRect(xLeyenda, yLeyenda, anchoLeyenda, altoLeyenda, 10, 10);
            int yFila = yLeyenda + 10 + FUENTE;
            for (Serie serie : series) {
                lienzo.setColor(serie.color);
                if (serie.dibujarPuntos) lienzo.fill(new Ellipse2D.Double(xLeyenda + 20, yFila - 12, 9, 9));
                else { lienzo.setStroke(new BasicStroke(3.4f)); lienzo.drawLine(xLeyenda + 10, yFila - 8, xLeyenda + 40, yFila - 8); }
                lienzo.setColor(TEXTO);
                lienzo.drawString(serie.nombre, xLeyenda + 52, yFila);
                yFila += FUENTE + 10;
            }
        }
    }

    // ---------- rangos ----------

    // Rango del eje X (en la escala de dibujo): el fijado a mano o, si no hay, de lo mínimo a lo máximo de los datos
    private double[] rangoX() {
        if (!Double.isNaN(limiteMinimoX)) return new double[]{escalaX(limiteMinimoX), escalaX(limiteMaximoX)};
        double minimo = Double.MAX_VALUE, maximo = -Double.MAX_VALUE;
        for (Serie serie : series)
            for (double valor : serie.valoresX) {
                minimo = Math.min(minimo, escalaX(valor));
                maximo = Math.max(maximo, escalaX(valor));
            }
        for (LineaVertical linea : lineasVerticales) { minimo = Math.min(minimo, escalaX(linea.x)); maximo = Math.max(maximo, escalaX(linea.x)); }
        for (Punto punto : puntos) { minimo = Math.min(minimo, escalaX(punto.x)); maximo = Math.max(maximo, escalaX(punto.x)); }
        double margen = ejeXLogaritmico ? 0 : (maximo - minimo) * 0.05;
        return new double[]{minimo - margen, maximo + margen};
    }

    // Rango del eje Y: el fijado a mano o, si no hay, el de los datos que quedan dentro del rango de X, con un 6 % de aire
    private double[] rangoY(double minimoX, double maximoX) {
        if (!Double.isNaN(limiteMinimoY)) return new double[]{limiteMinimoY, limiteMaximoY};
        double minimo = Double.MAX_VALUE, maximo = -Double.MAX_VALUE;
        for (Serie serie : series)
            for (int i = 0; i < serie.valoresX.length; i++) {
                double x = escalaX(serie.valoresX[i]);
                if (x < minimoX || x > maximoX) continue;
                minimo = Math.min(minimo, serie.valoresY[i]);
                maximo = Math.max(maximo, serie.valoresY[i]);
            }
        for (Punto punto : puntos) { minimo = Math.min(minimo, punto.y); maximo = Math.max(maximo, punto.y); }
        double margen = (maximo - minimo) * 0.06 + 1e-9;
        return new double[]{minimo - margen, maximo + margen};
    }

    // Pasa un valor (en la escala de dibujo) a la columna de píxeles que le corresponde, ubicándolo entre el mínimo y el máximo
    private static int pixelX(double valor, int px0, int anchoGrafico, double minimo, double maximo) {
        return px0 + (int) Math.round((valor - minimo) / (maximo - minimo) * anchoGrafico);
    }

    // Igual, para Y. Se invierte porque en una imagen el 0 está arriba y en un gráfico el mínimo va abajo
    private static int pixelY(double valor, int py0, int altoGrafico, double minimo, double maximo) {
        return py0 + altoGrafico - (int) Math.round((valor - minimo) / (maximo - minimo) * altoGrafico);
    }

    // ---------- marcas y formatos ----------

    // Un paso "lindo" (1, 2 o 5 por una potencia de 10) cercano al valor pedido
    private static double pasoBonito(double bruto) {
        double exponente = Math.floor(Math.log10(bruto));
        double fraccion = bruto / Math.pow(10, exponente);
        double base = fraccion < 1.5 ? 1 : fraccion < 3 ? 2 : fraccion < 7 ? 5 : 10;
        return base * Math.pow(10, exponente);
    }

    private static List<Double> marcasLineales(double minimo, double maximo, int objetivo) {
        return marcasLineales(minimo, maximo, pasoBonito((maximo - minimo) / objetivo));
    }

    // Valores múltiplos del paso que caen dentro del rango
    private static List<Double> marcasLineales(double minimo, double maximo, double paso) {
        List<Double> marcas = new ArrayList<>();
        for (double valor = Math.ceil(minimo / paso - 1e-9) * paso; valor <= maximo + paso * 1e-9; valor += paso) marcas.add(valor);
        return marcas;
    }

    // Una marca por década (10^k) que cae dentro del rango; si hay demasiadas, una de cada dos
    private static List<Double> marcasLogaritmicas(double minimo, double maximo) {
        List<Double> marcas = new ArrayList<>();
        int primera = (int) Math.ceil(minimo - 1e-9), ultima = (int) Math.floor(maximo + 1e-9);
        int salto = (ultima - primera + 1) > 6 ? 2 : 1;
        for (int decada = primera; decada <= ultima; decada += salto) marcas.add((double) decada);
        return marcas;
    }

    // Escribe un número con los decimales que pide el paso (paso 0.5 -> un decimal; paso 25 -> ninguno)
    private static String numero(double valor, double paso) {
        int decimales = Math.max(0, (int) Math.ceil(-Math.log10(paso) - 1e-9));
        if (Math.abs(valor) < paso * 1e-6) valor = 0;
        return String.format(Locale.US, "%." + decimales + "f", valor);
    }

    // Escribe una frecuencia con la unidad que corresponde, sin ceros de más: 100 Hz, 1 kHz, 72 kHz, 38.96 kHz, 2 MHz
    public static String formatoFrecuencia(double frecuencia) {
        if (frecuencia >= 1e6) return sinCeros(frecuencia / 1e6) + " MHz";
        if (frecuencia >= 1e3) return sinCeros(frecuencia / 1e3) + " kHz";
        return sinCeros(frecuencia) + " Hz";
    }

    private static String sinCeros(double valor) {
        String texto = String.format(Locale.US, "%.2f", valor);
        return texto.contains(".") ? texto.replaceAll("0+$", "").replaceAll("\\.$", "") : texto;
    }
}
