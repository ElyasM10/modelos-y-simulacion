package tp6.ui.ejercicio3;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import tp6.modelo.Cole;
import tp6.ui.componentes.BarraResultado;
import tp6.ui.componentes.GraficoFx;
import tp6.ui.componentes.Tarjeta;

/*
 * Ejercicio 3: ajuste del modelo a los registros de Dell'Osa et al.
 *
 * El panel apila (de arriba hacia abajo):
 *   1) una tarjeta con el título y la lista desplegable de registros
 *   2) una tarjeta con el gráfico del ajuste (medición contra modelo ajustado)
 *   3) la barra de resultado, con R0, R∞, fc y α obtenidos
 *
 * Cada vez que se elige otro registro en la lista, se vuelve a ajustar y se reemplaza el gráfico.
 */
public class PanelEjercicio3 extends VBox {

    private final ComboBox<String> selectorRegistro = new ComboBox<>();
    // Caja que contiene el gráfico actual; se vacía y se vuelve a llenar cada vez que cambia el registro
    private final VBox contenedorGrafico = new VBox();
    private final BarraResultado barraResultado = new BarraResultado();

    public PanelEjercicio3() {
        // 12 = separación entre cajas; el padding deja un margen alrededor del panel
        super(12);
        setPadding(new Insets(14));

        // Paso 1: la lista se llena con los nombres de los registros, empieza en el primero y, al elegir otro, se vuelve a ajustar
        selectorRegistro.getItems().addAll(Cole.REGISTROS.keySet());
        selectorRegistro.setValue(selectorRegistro.getItems().get(0));
        selectorRegistro.setOnAction(evento -> ajustar());

        // Paso 2: tarjeta de arriba con el título y la lista (una etiqueta y la lista una al lado de la otra, en un HBox)
        Label etiquetaSelector = new Label("Registro");
        etiquetaSelector.getStyleClass().add("etiqueta-campo");
        Tarjeta tarjetaSuperior = Tarjeta.conTitulo("Ejercicio 3 · Ajuste a datos reales",
                "Registros de Dell'Osa et al. (2025). Ajuste por el método de Ayllon.",
                new HBox(10, etiquetaSelector, selectorRegistro));

        // Paso 3: tarjeta del gráfico, que ocupa todo el espacio libre
        Tarjeta tarjetaGrafico = new Tarjeta(contenedorGrafico);
        VBox.setVgrow(tarjetaGrafico, Priority.ALWAYS);
        VBox.setVgrow(contenedorGrafico, Priority.ALWAYS);

        // Paso 4: se agrega todo al panel y se hace el primer ajuste (con el primer registro)
        getChildren().addAll(tarjetaSuperior, tarjetaGrafico, barraResultado);
        ajustar();
    }

    // Ajusta el modelo al registro elegido y actualiza el gráfico y la barra de resultado.
    private void ajustar() {
        try {
            // Paso 1: se lee el CSV del registro elegido: datos[0] = frecuencias, datos[1] = parte real, datos[2] = parte imaginaria
            String nombreRegistro = selectorRegistro.getValue();
            double[][] datos = Cole.leerCsv(Cole.REGISTROS.get(nombreRegistro));
            // Paso 2: se ajusta el modelo (método de Ayllon). Devuelve {R0, R∞, fc, α}
            double[] parametros = Cole.ajustar(datos[0], datos[1], datos[2]);
            // Paso 3: Nyquist de la medición (puntos) contra el modelo ajustado (línea).
            // El modelo ajustado se calcula con los parámetros obtenidos, en las mismas frecuencias que se midieron
            int cantidadPuntos = datos[0].length;
            double[] partesRealesAjuste = new double[cantidadPuntos];
            double[] partesImaginariasAjuste = new double[cantidadPuntos];
            double[] partesImaginariasMedidas = new double[cantidadPuntos];
            for (int i = 0; i < cantidadPuntos; i++) {
                double[] ajustada = Cole.modelo(datos[0][i], parametros[0], parametros[1], parametros[2], parametros[3]);
                partesRealesAjuste[i] = ajustada[0];
                // Se grafica -Im(Z): se cambia el signo tanto del modelo como de lo medido
                partesImaginariasAjuste[i] = -ajustada[1];
                partesImaginariasMedidas[i] = -datos[2][i];
            }
            // Paso 4: se crea el gráfico nuevo con dos series: "medición" (true = solo puntos) y "ajuste" (false = solo línea)
            GraficoFx graficoFx = new GraficoFx("Ajuste - " + nombreRegistro, "Re(Z) [Ω]", "-Im(Z) [Ω]", false);
            graficoFx.agregarSerie("medición", datos[1], partesImaginariasMedidas, GraficoFx.COLORES[0], true);
            graficoFx.agregarSerie("ajuste", partesRealesAjuste, partesImaginariasAjuste, GraficoFx.COLORES[1], false);
            // Paso 5: se reemplaza el gráfico anterior por el nuevo (setAll deja solo el nuevo dentro del contenedor)
            VBox.setVgrow(graficoFx, Priority.ALWAYS);
            contenedorGrafico.getChildren().setAll(graficoFx);
            // Paso 6: se muestran los parámetros obtenidos en la barra de abajo
            barraResultado.mostrarOk(String.format("R0 = %.2f Ω    R∞ = %.2f Ω    fc = %.1f Hz    α = %.3f",
                    parametros[0], parametros[1], parametros[2], parametros[3]));
        } catch (Exception error) {
            // Si algo falló (por ejemplo, no se pudo leer el archivo), se muestra el motivo en rojo
            barraResultado.mostrarError("Error: " + error.getMessage());
        }
    }
}
