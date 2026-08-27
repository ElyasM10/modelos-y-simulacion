package tp2.grafico;

import tp2.funciones.Funcion;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class VentanaGrafico {

    private static final int ANCHO = 900;
    private static final int ALTO = 500;

    private static final String CARPETA_SALIDA = "graficos";

    public static void mostrar(Funcion funcion, double periodoMuestreo) {

        JFrame ventana =
                new JFrame(
                        "TP2 - " + funcion.getNombre()
                );

        Grafico grafico =
                new Grafico(
                        funcion,
                        0,
                        10,
                        periodoMuestreo
                );

        ventana.add(grafico);

        ventana.setSize(
                ANCHO,
                ALTO
        );

        ventana.setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        ventana.setLocationByPlatform(true);

        ventana.setVisible(true);

        guardarImagen(grafico, funcion.getNombre());
    }

    // =================================================
    // GUARDAR EL GRÁFICO COMO PNG (para la entrega)
    // =================================================

    private static void guardarImagen(Grafico grafico, String nombreFuncion) {

        grafico.setSize(ANCHO, ALTO);

        BufferedImage imagen =
                new BufferedImage(
                        ANCHO,
                        ALTO,
                        BufferedImage.TYPE_INT_RGB
                );

        Graphics2D g2d = imagen.createGraphics();
        grafico.paint(g2d);
        g2d.dispose();

        File carpeta = new File(CARPETA_SALIDA);
        carpeta.mkdirs();

        String nombreArchivo =
                nombreFuncion.replace(" ", "_") + ".png";

        File archivo = new File(carpeta, nombreArchivo);

        try {
            ImageIO.write(imagen, "png", archivo);
            System.out.println("Imagen guardada: " + archivo.getPath());

        } catch (IOException e) {
            System.out.println(
                    "No se pudo guardar la imagen de "
                            + nombreFuncion
                            + ": "
                            + e.getMessage()
            );
        }
    }
}
