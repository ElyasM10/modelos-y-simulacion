package tp6.consola;

import java.awt.*;

// Tabla de texto para dibujar dentro de una imagen (por ejemplo, los parámetros obtenidos en el ejercicio 3).
public class TablaPng implements PanelPng {

    private static final int FUENTE = 21;
    private static final Color ENCABEZADO = new Color(0x7B241C);
    private static final Color BORDE = new Color(0xCCCCCC);

    private final String titulo;
    private final String[] encabezado;
    private final String[][] filas;

    public TablaPng(String titulo, String[] encabezado, String[][] filas) {
        this.titulo = titulo;
        this.encabezado = encabezado;
        this.filas = filas;
    }

    @Override
    public void dibujar(Graphics2D lienzo, int x, int y, int ancho, int alto) {
        // Paso 1: título de la tabla, centrado arriba (igual que el de los gráficos)
        lienzo.setFont(lienzo.getFont().deriveFont(Font.BOLD, FUENTE + 2f));
        lienzo.setColor(new Color(0x1B2430));
        int anchoTitulo = lienzo.getFontMetrics().stringWidth(titulo);
        lienzo.drawString(titulo, x + (ancho - anchoTitulo) / 2, y + 36);

        // Paso 2: medidas. La tabla ocupa casi todo el ancho y queda centrada verticalmente debajo del título.
        int altoFila = 54;
        int anchoTabla = ancho - 80;
        int cantidadFilas = filas.length + 1;
        int xTabla = x + 40;
        int yTabla = y + 56 + (alto - 56 - cantidadFilas * altoFila) / 2;
        // La primera columna (nombres) es más ancha; el resto se reparte en partes iguales.
        // Con 2 columnas (Parámetro | Valor) ocupa el 45 %; con más columnas, el 18 %
        int anchoPrimera = (int) (anchoTabla * (encabezado.length == 2 ? 0.45 : 0.18));
        int anchoOtras = (anchoTabla - anchoPrimera) / (encabezado.length - 1);

        // Paso 3: fila de encabezado (fondo rojo oscuro, texto blanco en negrita) y filas de datos (fondo blanco)
        for (int fila = 0; fila < cantidadFilas; fila++) {
            boolean esEncabezado = fila == 0;
            String[] textos = esEncabezado ? encabezado : filas[fila - 1];
            int xCelda = xTabla;
            for (int columna = 0; columna < encabezado.length; columna++) {
                int anchoCelda = columna == 0 ? anchoPrimera : anchoOtras;
                int yCelda = yTabla + fila * altoFila;
                lienzo.setColor(esEncabezado ? ENCABEZADO : Color.WHITE);
                lienzo.fillRect(xCelda, yCelda, anchoCelda, altoFila);
                lienzo.setColor(BORDE);
                lienzo.drawRect(xCelda, yCelda, anchoCelda, altoFila);
                // Texto centrado en la celda
                lienzo.setFont(lienzo.getFont().deriveFont(esEncabezado ? Font.BOLD : Font.PLAIN, (float) FUENTE));
                lienzo.setColor(esEncabezado ? Color.WHITE : new Color(0x1B2430));
                FontMetrics medidas = lienzo.getFontMetrics();
                lienzo.drawString(textos[columna], xCelda + (anchoCelda - medidas.stringWidth(textos[columna])) / 2,
                        yCelda + (altoFila + medidas.getAscent()) / 2 - 4);
                xCelda += anchoCelda;
            }
        }
    }
}
