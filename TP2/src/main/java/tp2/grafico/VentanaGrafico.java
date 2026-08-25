package tp2.grafico;

import tp2.funciones.Funcion;

import javax.swing.*;

public class VentanaGrafico {

    public static void mostrar(Funcion funcion) {

        JFrame ventana =
                new JFrame(
                        "TP2 - " + funcion.getNombre()
                );

        Grafico grafico =
                new Grafico(
                        funcion,
                        0,
                        10
                );

        ventana.add(grafico);

        ventana.setSize(
                900,
                500
        );

        ventana.setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE
        );

        ventana.setLocationByPlatform(true);

        ventana.setVisible(true);
    }
}