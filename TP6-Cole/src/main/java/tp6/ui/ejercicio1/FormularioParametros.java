package tp6.ui.ejercicio1;

import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import tp6.modelo.Cole;

// Formulario del ejercicio 1: tejido de partida, parámetros del modelo y frecuencia puntual.
public class FormularioParametros extends GridPane {

    private final TextField campoResistenciaCero = new TextField("540");
    private final TextField campoResistenciaInfinito = new TextField("100");
    private final TextField campoFrecuenciaCaracteristica = new TextField("72000");
    private final TextField campoAlfa = new TextField("0.45");
    private final TextField campoFrecuenciaPuntual = new TextField("10000");

    // alGraficar se ejecuta al apretar el botón, al dar Enter en un campo o al elegir un tejido.
    public FormularioParametros(Runnable alGraficar) {
        setHgap(12);
        setVgap(6);

        ComboBox<String> selectorTejido = new ComboBox<>();
        selectorTejido.getItems().addAll(Cole.TEJIDOS.keySet());
        selectorTejido.setPromptText("Elegir tejido de la Tabla 1");
        selectorTejido.setOnAction(evento -> {
            double[] parametros = Cole.TEJIDOS.get(selectorTejido.getValue());
            campoResistenciaCero.setText(String.valueOf(parametros[0]));
            campoResistenciaInfinito.setText(String.valueOf(parametros[1]));
            campoFrecuenciaCaracteristica.setText(String.valueOf(parametros[2]));
            campoAlfa.setText(String.valueOf(parametros[3]));
            alGraficar.run();
        });

        Button botonGraficar = new Button("Graficar");
        botonGraficar.getStyleClass().add("boton-primario");
        botonGraficar.setOnAction(evento -> alGraficar.run());

        agregarCampo(0, "Tejido", selectorTejido);
        agregarCampo(1, "R0 [Ω]", campoResistenciaCero);
        agregarCampo(2, "R∞ [Ω]", campoResistenciaInfinito);
        agregarCampo(3, "fc [Hz]", campoFrecuenciaCaracteristica);
        agregarCampo(4, "α", campoAlfa);
        agregarCampo(5, "Z en f [Hz]", campoFrecuenciaPuntual);
        add(botonGraficar, 6, 1);
        setHalignment(botonGraficar, HPos.LEFT);

        for (TextField campo : new TextField[]{campoResistenciaCero, campoResistenciaInfinito,
                campoFrecuenciaCaracteristica, campoAlfa, campoFrecuenciaPuntual}) {
            campo.setPrefColumnCount(7);
            campo.setOnAction(evento -> alGraficar.run());
        }
    }

    private void agregarCampo(int columna, String nombre, Control campo) {
        Label etiqueta = new Label(nombre);
        etiqueta.getStyleClass().add("etiqueta-campo");
        add(etiqueta, columna, 0);
        add(campo, columna, 1);
    }

    // Devuelve {R0, R∞, fc, α} validados. Lanza IllegalArgumentException si algún dato no es válido.
    public double[] leerParametros() {
        double resistenciaCero = numero(campoResistenciaCero);
        double resistenciaInfinito = numero(campoResistenciaInfinito);
        double frecuenciaCaracteristica = numero(campoFrecuenciaCaracteristica);
        double alfa = numero(campoAlfa);
        if (!(resistenciaCero > resistenciaInfinito && resistenciaInfinito >= 0
                && frecuenciaCaracteristica > 0 && alfa > 0 && alfa <= 1))
            throw new IllegalArgumentException("Se requiere R0 > R∞ ≥ 0, fc > 0 y 0 < α ≤ 1");
        return new double[]{resistenciaCero, resistenciaInfinito, frecuenciaCaracteristica, alfa};
    }

    public double leerFrecuenciaPuntual() {
        return numero(campoFrecuenciaPuntual);
    }

    private static double numero(TextField campo) {
        return Double.parseDouble(campo.getText().trim().replace(',', '.'));
    }
}
