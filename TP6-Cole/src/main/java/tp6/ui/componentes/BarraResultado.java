package tp6.ui.componentes;

import javafx.scene.control.Label;

// Línea de estado al pie de un ejercicio: en verde cuando todo salió bien y en rojo cuando hubo un error.
public class BarraResultado extends Label {

    public BarraResultado() {
        super(" ");
    }

    public void mostrarOk(String texto) {
        mostrar(texto, "resultado-ok");
    }

    public void mostrarError(String texto) {
        mostrar(texto, "resultado-error");
    }

    private void mostrar(String texto, String estilo) {
        getStyleClass().removeAll("resultado-ok", "resultado-error");
        getStyleClass().add(estilo);
        setText(texto);
    }
}
