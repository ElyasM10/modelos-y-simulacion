package tp6.ui.ejercicio1;

import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import tp6.modelo.Cole;

/*
 * Formulario del ejercicio 1: tejido de partida, parámetros del modelo y frecuencia puntual.
 *
 * Es un GridPane: una grilla de filas y columnas. Acá usa dos filas:
 *   fila 0: los nombres de los campos (R0, R∞, fc...)
 *   fila 1: los campos para escribir (y el botón Graficar al final)
 *
 * El formulario no sabe nada de gráficos: solo guarda lo que el usuario escribe y avisa cuando hay que graficar.
 */
public class FormularioParametros extends GridPane {

    // TextField: un cuadro de texto. El texto entre comillas es el valor inicial (el hígado de la Tabla 1)
    private final TextField campoResistenciaCero = new TextField("540");
    private final TextField campoResistenciaInfinito = new TextField("100");
    private final TextField campoFrecuenciaCaracteristica = new TextField("72000");
    private final TextField campoAlfa = new TextField("0.45");
    private final TextField campoFrecuenciaPuntual = new TextField("10000");

    // alGraficar es una función que nos pasa el panel. Se ejecuta al apretar el botón, al dar Enter en un campo o al elegir un tejido.
    // Runnable es el tipo Java para "un bloque de código que se puede ejecutar más tarde, sin parámetros".
    public FormularioParametros(Runnable alGraficar) {
        // Separación entre columnas (horizontal) y entre filas (vertical), en píxeles
        setHgap(12);
        setVgap(6);

        // Paso 1: lista desplegable con los tejidos de la Tabla 1
        ComboBox<String> selectorTejido = new ComboBox<>();
        selectorTejido.getItems().addAll(Cole.TEJIDOS.keySet());
        selectorTejido.setPromptText("Elegir tejido de la Tabla 1");
        // setOnAction: "cuando el usuario elige algo, ejecutá esto". Se copian los parámetros del tejido a los campos y se grafica.
        selectorTejido.setOnAction(evento -> {
            double[] parametros = Cole.TEJIDOS.get(selectorTejido.getValue());
            campoResistenciaCero.setText(String.valueOf(parametros[0]));
            campoResistenciaInfinito.setText(String.valueOf(parametros[1]));
            campoFrecuenciaCaracteristica.setText(String.valueOf(parametros[2]));
            campoAlfa.setText(String.valueOf(parametros[3]));
            alGraficar.run();
        });

        // Paso 2: botón. La clase CSS "boton-primario" (en estilo.css) lo pinta de azul. Al hacer clic ejecuta alGraficar.
        Button botonGraficar = new Button("Graficar");
        botonGraficar.getStyleClass().add("boton-primario");
        botonGraficar.setOnAction(evento -> alGraficar.run());

        // Paso 3: se acomoda todo en la grilla. agregarCampo(columna, nombre, campo) pone el nombre arriba y el campo abajo
        agregarCampo(0, "Tejido", selectorTejido);
        agregarCampo(1, "R0 [Ω]", campoResistenciaCero);
        agregarCampo(2, "R∞ [Ω]", campoResistenciaInfinito);
        agregarCampo(3, "fc [Hz]", campoFrecuenciaCaracteristica);
        agregarCampo(4, "α", campoAlfa);
        agregarCampo(5, "Z en f [Hz]", campoFrecuenciaPuntual);
        // add(elemento, columna, fila): el botón va en la columna 6, fila 1 (junto a los campos)
        add(botonGraficar, 6, 1);
        setHalignment(botonGraficar, HPos.LEFT);

        // Paso 4: a los 5 campos de texto se les da un ancho y se hace que al apretar Enter también se grafique
        for (TextField campo : new TextField[]{campoResistenciaCero, campoResistenciaInfinito,
                campoFrecuenciaCaracteristica, campoAlfa, campoFrecuenciaPuntual}) {
            campo.setPrefColumnCount(7);
            campo.setOnAction(evento -> alGraficar.run());
        }
    }

    // Pone un nombre (fila 0) y su campo (fila 1) en la misma columna de la grilla
    private void agregarCampo(int columna, String nombre, Control campo) {
        Label etiqueta = new Label(nombre);
        etiqueta.getStyleClass().add("etiqueta-campo");
        add(etiqueta, columna, 0);
        add(campo, columna, 1);
    }

    // Devuelve {R0, R∞, fc, α} validados. Lanza IllegalArgumentException si algún dato no es válido.
    public double[] leerParametros() {
        // Paso 1: se convierte el texto de cada campo en un número (si no es un número, numero() lanza una excepción)
        double resistenciaCero = numero(campoResistenciaCero);
        double resistenciaInfinito = numero(campoResistenciaInfinito);
        double frecuenciaCaracteristica = numero(campoFrecuenciaCaracteristica);
        double alfa = numero(campoAlfa);
        // Paso 2: se validan las condiciones del modelo; si alguna falla se lanza un error con la explicación
        if (!(resistenciaCero > resistenciaInfinito && resistenciaInfinito >= 0
                && frecuenciaCaracteristica > 0 && alfa > 0 && alfa <= 1))
            throw new IllegalArgumentException("Se requiere R0 > R∞ ≥ 0, fc > 0 y 0 < α ≤ 1");
        return new double[]{resistenciaCero, resistenciaInfinito, frecuenciaCaracteristica, alfa};
    }

    public double leerFrecuenciaPuntual() {
        return numero(campoFrecuenciaPuntual);
    }

    // Convierte el texto de un campo en número. Acepta coma o punto como separador decimal ("0,45" o "0.45").
    private static double numero(TextField campo) {
        return Double.parseDouble(campo.getText().trim().replace(',', '.'));
    }
}
