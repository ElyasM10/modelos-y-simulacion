package tp6.ui.ejercicio1;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import tp6.modelo.Cole;
import tp6.ui.componentes.BarraResultado;
import tp6.ui.componentes.GraficoFx;
import tp6.ui.componentes.Tarjeta;

/*
 * Ejercicio 1: el usuario ingresa los parámetros y ve el módulo, el Nyquist y Z en una frecuencia puntual.
 *
 * Este panel es un VBox: una caja que apila lo que tiene adentro de arriba hacia abajo. Contiene, en orden:
 *   1) una tarjeta con el formulario (FormularioParametros)
 *   2) una tarjeta con los dos gráficos, uno al lado del otro (un HBox)
 *   3) la barra de resultado (verde si está bien, roja si hay un error)
 *
 * Qué pasa cuando se aprieta "Graficar": el formulario ejecuta el método graficar() de esta clase
 * (se lo pasamos al crearlo con this::graficar) y graficar() recalcula todo y reemplaza los gráficos.
 */
public class PanelEjercicio1 extends VBox {

    // this::graficar es una referencia a un método: "cuando pase algo en el formulario, ejecutá graficar()"
    private final FormularioParametros formulario = new FormularioParametros(this::graficar);
    // HBox: caja que acomoda lo que tiene adentro uno al lado del otro; 12 es la separación en píxeles
    private final HBox contenedorGraficos = new HBox(12);
    private final BarraResultado barraResultado = new BarraResultado();

    public PanelEjercicio1() {
        // 12 = separación vertical entre las cajas apiladas; el padding deja un margen alrededor
        super(12);
        setPadding(new Insets(14));

        // Tarjeta de arriba: título + descripción (la ecuación) + el formulario
        Tarjeta tarjetaFormulario = Tarjeta.conTitulo("Ejercicio 1 · Modelo de Cole",
                "Z(ω) = R∞ + (R0 − R∞) / (1 + (jωτ)^α)", formulario);

        // Tarjeta del medio: contiene los gráficos. Priority.ALWAYS = "si sobra lugar, que esta caja crezca"
        contenedorGraficos.setAlignment(Pos.CENTER);
        Tarjeta tarjetaGraficos = new Tarjeta(contenedorGraficos);
        VBox.setVgrow(tarjetaGraficos, Priority.ALWAYS);
        VBox.setVgrow(contenedorGraficos, Priority.ALWAYS);

        // Se agregan las tres cajas al panel (de arriba hacia abajo) y se dibuja por primera vez con los valores iniciales
        getChildren().addAll(tarjetaFormulario, tarjetaGraficos, barraResultado);
        graficar();
    }

    // Recalcula y redibuja todo con los valores que hay en el formulario.
    private void graficar() {
        try {
            // Paso 1: se leen y validan los datos del formulario. Si algo es inválido, lanza una excepción y se va al catch
            double[] parametros = formulario.leerParametros();
            double frecuenciaPuntual = formulario.leerFrecuenciaPuntual();

            // Paso 2: curva de Cole de 100 Hz a 100 MHz (300 puntos)
            double[] frecuencias = Cole.frecuencias(100, 100e6, 300);
            double[] modulos = new double[frecuencias.length];
            double[] partesReales = new double[frecuencias.length];
            double[] partesImaginarias = new double[frecuencias.length];
            for (int i = 0; i < frecuencias.length; i++) {
                double[] punto = Cole.modelo(frecuencias[i], parametros[0], parametros[1], parametros[2], parametros[3]);
                modulos[i] = Cole.modulo(punto);
                partesReales[i] = punto[0];
                // Se grafica -Im(Z) para que el arco del Nyquist quede hacia arriba
                partesImaginarias[i] = -punto[1];
            }
            // Paso 3: se crean dos gráficos nuevos (módulo con eje X logarítmico y Nyquist con eje X lineal) y se les carga la curva
            GraficoFx graficoModuloFx = new GraficoFx("|Z| vs frecuencia", "f", "|Z| [Ω]", true);
            graficoModuloFx.agregarSerie("Cole", frecuencias, modulos, Color.BLUE, false);
            GraficoFx graficoNyquistFx = new GraficoFx("Diagrama de Nyquist", "Re(Z) [Ω]", "-Im(Z) [Ω]", false);
            graficoNyquistFx.agregarSerie("Cole", partesReales, partesImaginarias, Color.BLUE, false);
            // Paso 4: se reemplazan los gráficos viejos por los nuevos. Cada uno se estira para ocupar la mitad del ancho.
            HBox.setHgrow(graficoModuloFx, Priority.ALWAYS);
            HBox.setHgrow(graficoNyquistFx, Priority.ALWAYS);
            contenedorGraficos.getChildren().setAll(graficoModuloFx, graficoNyquistFx);

            // Paso 5: se calcula Z en la frecuencia puntual y se muestra en la barra de abajo (en verde)
            double[] impedancia = Cole.modelo(frecuenciaPuntual, parametros[0], parametros[1], parametros[2], parametros[3]);
            barraResultado.mostrarOk(String.format("Z(%.0f Hz) = %.2f %+.2fj Ω    |Z| = %.2f Ω    fase = %.2f°",
                    frecuenciaPuntual, impedancia[0], impedancia[1], Cole.modulo(impedancia),
                    Math.toDegrees(Math.atan2(impedancia[1], impedancia[0]))));
        } catch (Exception error) {
            // Si algún dato era inválido (texto que no es un número, o valores fuera de rango), se muestra el motivo en rojo
            barraResultado.mostrarError("Dato inválido: " + error.getMessage());
        }
    }
}
