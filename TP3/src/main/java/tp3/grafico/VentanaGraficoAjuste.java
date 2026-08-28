package tp3.grafico;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VentanaGraficoAjuste {

    private static final int ANCHO = 900;
    private static final int ALTO = 550;

    private static final String CARPETA_SALIDA = "graficos";

    public static void mostrar(String tituloVentana, GraficoAjuste grafico, String nombreArchivo) {

        JFrame ventana = new JFrame("TP3 - " + tituloVentana);

        ventana.add(grafico);

        ventana.setSize(ANCHO, ALTO);

        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ventana.setLocationByPlatform(true);

        ventana.setVisible(true);

        guardarImagen(grafico, nombreArchivo);
    }

    private static void guardarImagen(GraficoAjuste grafico, String nombreArchivo) {

        grafico.setSize(ANCHO, ALTO);

        BufferedImage imagen = new BufferedImage(ANCHO, ALTO, BufferedImage.TYPE_INT_RGB);

        Graphics2D g2d = imagen.createGraphics();
        grafico.paint(g2d);
        g2d.dispose();

        File carpeta = new File(CARPETA_SALIDA);
        carpeta.mkdirs();

        File archivo = new File(carpeta, nombreArchivo + ".png");

        try {
            ImageIO.write(imagen, "png", archivo);
            System.out.println("Imagen guardada: " + archivo.getPath());

        } catch (IOException e) {
            System.out.println(
                    "No se pudo guardar la imagen de " + nombreArchivo + ": " + e.getMessage()
            );
        }
    }
}
