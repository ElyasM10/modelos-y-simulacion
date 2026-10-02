package tp5.grafico;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VentanaGrafico {

    private static final int ANCHO = 800;
    private static final int ALTO = 500;

    private static final String CARPETA_SALIDA = "graficos";

    public static void guardar(JPanel grafico, String nombreArchivo) {

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
            System.out.println("Imagen guardada: " + archivo.getAbsolutePath());

        } catch (IOException e) {
            System.out.println(
                    "No se pudo guardar la imagen de " + nombreArchivo + ": " + e.getMessage()
            );
        }
    }

    // Cartel único al finalizar, en lugar de una ventana por gráfico.
    public static void avisarFinalizacion() {
        JOptionPane.showMessageDialog(
                null,
                "Resultados generados.\nRevisar la carpeta graficos:\n" + new File(CARPETA_SALIDA).getAbsolutePath(),
                "TP5 - Modelos y Simulación",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}
