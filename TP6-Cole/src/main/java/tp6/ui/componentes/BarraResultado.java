package tp6.ui.componentes;

import javafx.scene.control.Label;

// Línea de estado al pie de un ejercicio: en verde cuando todo salió bien y en rojo cuando hubo un error.
// Es un Label (un texto). El color lo definen las clases CSS "resultado-ok" y "resultado-error" (en estilo.css).
public class BarraResultado extends Label {

    public BarraResultado() {
        // Empieza con un espacio en blanco para que la barra ya ocupe su lugar aunque todavía no tenga texto
        super(" ");
    }

    // Muestra el texto en verde
    public void mostrarOk(String texto) {
        mostrar(texto, "resultado-ok");
    }

    // Muestra el texto en rojo
    public void mostrarError(String texto) {
        mostrar(texto, "resultado-error");
    }

    private void mostrar(String texto, String estilo) {
        // Se saca el color anterior (para que no queden los dos a la vez), se pone el nuevo y se escribe el texto
        getStyleClass().removeAll("resultado-ok", "resultado-error");
        getStyleClass().add(estilo);
        setText(texto);
    }
}
