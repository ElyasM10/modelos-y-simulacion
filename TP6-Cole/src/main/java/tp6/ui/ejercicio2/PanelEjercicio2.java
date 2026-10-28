package tp6.ui.ejercicio2;

import javafx.geometry.Insets;
import javafx.scene.layout.*;
import tp6.modelo.Cole;
import tp6.ui.componentes.GraficoFx;
import tp6.ui.componentes.Tarjeta;

// Ejercicio 2: los 5 tejidos de la Tabla 1 en un mismo espectro.
public class PanelEjercicio2 extends VBox {

    public PanelEjercicio2() {
        super(12);
        setPadding(new Insets(14));

        double[] frecuencias = Cole.frecuencias(100, 100e6, 300);
        GraficoFx graficoFx = new GraficoFx("Modelo de Cole - 5 tejidos", "f", "|Z| [Ω]", true);
        int indiceColor = 0;
        for (var tejido : Cole.TEJIDOS.entrySet()) {
            double[] parametros = tejido.getValue();
            double[] modulos = new double[frecuencias.length];
            for (int i = 0; i < frecuencias.length; i++)
                modulos[i] = Cole.modulo(Cole.modelo(frecuencias[i], parametros[0], parametros[1], parametros[2], parametros[3]));
            graficoFx.agregarSerie(tejido.getKey(), frecuencias, modulos, GraficoFx.COLORES[indiceColor++], false);
        }
        Tarjeta tarjeta = Tarjeta.conTitulo("Ejercicio 2 · Los 5 tejidos de la Tabla 1",
                "Módulo de la impedancia de cada tejido, de 100 Hz a 100 MHz.", graficoFx);
        VBox.setVgrow(graficoFx, Priority.ALWAYS);
        VBox.setVgrow(tarjeta, Priority.ALWAYS);
        getChildren().add(tarjeta);
    }
}
