package tp6.ui.ejercicio2;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import tp6.modelo.Cole;
import tp6.ui.componentes.GraficoFx;
import tp6.ui.componentes.Tarjeta;

/*
 * Ejercicio 2: los 5 tejidos de la Tabla 1 en un mismo espectro.
 *
 * Es el panel más simple: no tiene botones ni campos, así que se calcula una sola vez al abrir la pestaña.
 * Contiene una única tarjeta (título + descripción + el gráfico con las 5 curvas).
 */
public class PanelEjercicio2 extends VBox {

    public PanelEjercicio2() {
        // 12 = separación entre cajas; el padding deja un margen alrededor del panel
        super(12);
        setPadding(new Insets(14));

        // Paso 1: las mismas 300 frecuencias (100 Hz a 100 MHz) para todos los tejidos, así las curvas se pueden comparar
        double[] frecuencias = Cole.frecuencias(100, 100e6, 300);
        // Paso 2: un solo gráfico (eje X logarítmico) donde van a ir las 5 curvas
        GraficoFx graficoFx = new GraficoFx("Modelo de Cole - 5 tejidos", "f", "|Z| [Ω]", true);
        int indiceColor = 0;
        // Paso 3: por cada tejido de la Tabla 1 se calcula el módulo de Z en todas las frecuencias y se agrega como una serie
        for (var tejido : Cole.TEJIDOS.entrySet()) {
            double[] parametros = tejido.getValue();
            double[] modulos = new double[frecuencias.length];
            for (int i = 0; i < frecuencias.length; i++)
                modulos[i] = Cole.modulo(Cole.modelo(frecuencias[i], parametros[0], parametros[1], parametros[2], parametros[3]));
            // Cada serie lleva el nombre del tejido (aparece en la leyenda) y un color distinto
            graficoFx.agregarSerie(tejido.getKey(), frecuencias, modulos, GraficoFx.COLORES[indiceColor++], false);
        }
        // Paso 4: se mete el gráfico en una tarjeta con título y se la agrega al panel
        Tarjeta tarjeta = Tarjeta.conTitulo("Ejercicio 2 · Los 5 tejidos de la Tabla 1",
                "Módulo de la impedancia de cada tejido, de 100 Hz a 100 MHz.", graficoFx);
        // Priority.ALWAYS = "si sobra lugar, que crezca": así el gráfico ocupa toda la pestaña
        VBox.setVgrow(graficoFx, Priority.ALWAYS);
        VBox.setVgrow(tarjeta, Priority.ALWAYS);
        getChildren().add(tarjeta);
    }
}
