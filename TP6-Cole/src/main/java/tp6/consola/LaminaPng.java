package tp6.consola;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

// Arma una imagen PNG con uno o varios paneles (gráficos o tablas) acomodados en una grilla.
public class LaminaPng {

    // Grilla pareja: columnas = cuántos paneles hay por fila. Si titulo no es null, se escribe arriba de todo.
    public static void guardar(String ruta, int ancho, int alto, String titulo, int columnas, PanelPng... paneles) throws IOException {
        // Se reparten los paneles en filas de "columnas" paneles cada una y se dibuja con guardarFilas
        int cantidadFilas = (paneles.length + columnas - 1) / columnas;
        PanelPng[][] filas = new PanelPng[cantidadFilas][];
        for (int fila = 0; fila < cantidadFilas; fila++) {
            int desde = fila * columnas, hasta = Math.min(desde + columnas, paneles.length);
            filas[fila] = java.util.Arrays.copyOfRange(paneles, desde, hasta);
        }
        guardarFilas(ruta, ancho, alto, titulo, filas);
    }

    // Cada fila puede tener una cantidad distinta de paneles; los de una misma fila se reparten el ancho en partes iguales.
    // Por ejemplo, una fila con 1 panel (que ocupa todo el ancho) y otra con 2 (la mitad cada uno).
    public static void guardarFilas(String ruta, int ancho, int alto, String titulo, PanelPng[]... filas) throws IOException {
        // Paso 1: imagen vacía en memoria con fondo blanco
        BufferedImage imagen = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D lienzo = imagen.createGraphics();
        lienzo.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        lienzo.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        lienzo.setColor(Color.WHITE);
        lienzo.fillRect(0, 0, ancho, alto);

        // Paso 2: título general (opcional), centrado arriba
        int cabecera = 0;
        if (titulo != null) {
            cabecera = 64;
            lienzo.setFont(lienzo.getFont().deriveFont(Font.BOLD, 27f));
            lienzo.setColor(new Color(0x1B2430));
            int anchoTexto = lienzo.getFontMetrics().stringWidth(titulo);
            lienzo.drawString(titulo, (ancho - anchoTexto) / 2, 44);
        }

        // Paso 3: la zona restante se divide en filas de igual altura; dentro de cada fila, los paneles se reparten el ancho
        int altoFila = (alto - cabecera) / filas.length;
        for (int fila = 0; fila < filas.length; fila++) {
            int anchoPanel = ancho / filas[fila].length;
            for (int columna = 0; columna < filas[fila].length; columna++) {
                filas[fila][columna].dibujar(lienzo, columna * anchoPanel, cabecera + fila * altoFila, anchoPanel, altoFila);
            }
        }
        lienzo.dispose();

        // Paso 4: se escribe la imagen en el archivo
        ImageIO.write(imagen, "png", new File(ruta));
    }
}
