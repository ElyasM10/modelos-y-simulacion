package tp6.ui.ejercicio3;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import tp6.modelo.Cole;
import tp6.ui.componentes.BarraResultado;
import tp6.ui.componentes.GraficoFx;
import tp6.ui.componentes.Tarjeta;

// Ejercicio 3: ajuste del modelo a los registros de Dell'Osa et al.
public class PanelEjercicio3 extends VBox {

    private final ComboBox<String> selectorRegistro = new ComboBox<>();
    private final VBox contenedorGrafico = new VBox();
    private final BarraResultado barraResultado = new BarraResultado();

    public PanelEjercicio3() {
        super(12);
        setPadding(new Insets(14));

        selectorRegistro.getItems().addAll(Cole.REGISTROS.keySet());
        selectorRegistro.setValue(selectorRegistro.getItems().get(0));
        selectorRegistro.setOnAction(evento -> ajustar());

        Label etiquetaSelector = new Label("Registro");
        etiquetaSelector.getStyleClass().add("etiqueta-campo");
        Tarjeta tarjetaSuperior = Tarjeta.conTitulo("Ejercicio 3 · Ajuste a datos reales",
                "Registros de Dell'Osa et al. (2025). Ajuste por el método de Ayllon.",
                new HBox(10, etiquetaSelector, selectorRegistro));

        Tarjeta tarjetaGrafico = new Tarjeta(contenedorGrafico);
        VBox.setVgrow(tarjetaGrafico, Priority.ALWAYS);
        VBox.setVgrow(contenedorGrafico, Priority.ALWAYS);

        getChildren().addAll(tarjetaSuperior, tarjetaGrafico, barraResultado);
        ajustar();
    }

    private void ajustar() {
        try {
            String nombreRegistro = selectorRegistro.getValue();
            double[][] datos = Cole.leerCsv(Cole.REGISTROS.get(nombreRegistro));
            double[] parametros = Cole.ajustar(datos[0], datos[1], datos[2]);
            // Nyquist de la medición (puntos) contra el modelo ajustado (línea)
            int cantidadPuntos = datos[0].length;
            double[] partesRealesAjuste = new double[cantidadPuntos];
            double[] partesImaginariasAjuste = new double[cantidadPuntos];
            double[] partesImaginariasMedidas = new double[cantidadPuntos];
            for (int i = 0; i < cantidadPuntos; i++) {
                double[] ajustada = Cole.modelo(datos[0][i], parametros[0], parametros[1], parametros[2], parametros[3]);
                partesRealesAjuste[i] = ajustada[0];
                partesImaginariasAjuste[i] = -ajustada[1];
                partesImaginariasMedidas[i] = -datos[2][i];
            }
            GraficoFx graficoFx = new GraficoFx("Ajuste - " + nombreRegistro, "Re(Z) [Ω]", "-Im(Z) [Ω]", false);
            graficoFx.agregarSerie("medición", datos[1], partesImaginariasMedidas, GraficoFx.COLORES[0], true);
            graficoFx.agregarSerie("ajuste", partesRealesAjuste, partesImaginariasAjuste, GraficoFx.COLORES[1], false);
            VBox.setVgrow(graficoFx, Priority.ALWAYS);
            contenedorGrafico.getChildren().setAll(graficoFx);
            barraResultado.mostrarOk(String.format("R0 = %.2f Ω    R∞ = %.2f Ω    fc = %.1f Hz    α = %.3f",
                    parametros[0], parametros[1], parametros[2], parametros[3]));
        } catch (Exception error) {
            barraResultado.mostrarError("Error: " + error.getMessage());
        }
    }
}
