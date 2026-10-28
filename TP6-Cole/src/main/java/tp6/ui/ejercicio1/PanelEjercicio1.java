package tp6.ui.ejercicio1;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import tp6.modelo.Cole;
import tp6.ui.componentes.BarraResultado;
import tp6.ui.componentes.GraficoFx;
import tp6.ui.componentes.Tarjeta;

// Ejercicio 1: el usuario ingresa los parámetros y ve el módulo, el Nyquist y Z en una frecuencia puntual.
public class PanelEjercicio1 extends VBox {

    private final FormularioParametros formulario = new FormularioParametros(this::graficar);
    private final HBox contenedorGraficos = new HBox(12);
    private final BarraResultado barraResultado = new BarraResultado();

    public PanelEjercicio1() {
        super(12);
        setPadding(new Insets(14));

        Tarjeta tarjetaFormulario = Tarjeta.conTitulo("Ejercicio 1 · Modelo de Cole",
                "Z(ω) = R∞ + (R0 − R∞) / (1 + (jωτ)^α)", formulario);

        contenedorGraficos.setAlignment(Pos.CENTER);
        Tarjeta tarjetaGraficos = new Tarjeta(contenedorGraficos);
        VBox.setVgrow(tarjetaGraficos, Priority.ALWAYS);
        VBox.setVgrow(contenedorGraficos, Priority.ALWAYS);

        getChildren().addAll(tarjetaFormulario, tarjetaGraficos, barraResultado);
        graficar();
    }

    private void graficar() {
        try {
            double[] parametros = formulario.leerParametros();
            double frecuenciaPuntual = formulario.leerFrecuenciaPuntual();

            // Curva de Cole de 100 Hz a 100 MHz
            double[] frecuencias = Cole.frecuencias(100, 100e6, 300);
            double[] modulos = new double[frecuencias.length];
            double[] partesReales = new double[frecuencias.length];
            double[] partesImaginarias = new double[frecuencias.length];
            for (int i = 0; i < frecuencias.length; i++) {
                double[] punto = Cole.modelo(frecuencias[i], parametros[0], parametros[1], parametros[2], parametros[3]);
                modulos[i] = Cole.modulo(punto);
                partesReales[i] = punto[0];
                partesImaginarias[i] = -punto[1];
            }
            GraficoFx graficoModuloFx = new GraficoFx("|Z| vs frecuencia", "f", "|Z| [Ω]", true);
            graficoModuloFx.agregarSerie("Cole", frecuencias, modulos, Color.BLUE, false);
            GraficoFx graficoNyquistFx = new GraficoFx("Diagrama de Nyquist", "Re(Z) [Ω]", "-Im(Z) [Ω]", false);
            graficoNyquistFx.agregarSerie("Cole", partesReales, partesImaginarias, Color.BLUE, false);
            HBox.setHgrow(graficoModuloFx, Priority.ALWAYS);
            HBox.setHgrow(graficoNyquistFx, Priority.ALWAYS);
            contenedorGraficos.getChildren().setAll(graficoModuloFx, graficoNyquistFx);

            double[] impedancia = Cole.modelo(frecuenciaPuntual, parametros[0], parametros[1], parametros[2], parametros[3]);
            barraResultado.mostrarOk(String.format("Z(%.0f Hz) = %.2f %+.2fj Ω    |Z| = %.2f Ω    fase = %.2f°",
                    frecuenciaPuntual, impedancia[0], impedancia[1], Cole.modulo(impedancia),
                    Math.toDegrees(Math.atan2(impedancia[1], impedancia[0]))));
        } catch (Exception error) {
            barraResultado.mostrarError("Dato inválido: " + error.getMessage());
        }
    }
}
