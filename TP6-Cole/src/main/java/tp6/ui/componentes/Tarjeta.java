package tp6.ui.componentes;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

// Contenedor blanco con sombra. Puede llevar un título y una descripción arriba del contenido.
public class Tarjeta extends VBox {

    public Tarjeta(Node... contenido) {
        super(8, contenido);
        getStyleClass().add("tarjeta");
    }

    public static Tarjeta conTitulo(String titulo, String descripcion, Node... contenido) {
        Label etiquetaTitulo = new Label(titulo);
        etiquetaTitulo.getStyleClass().add("titulo-pagina");
        Label etiquetaDescripcion = new Label(descripcion);
        etiquetaDescripcion.getStyleClass().add("subtitulo");
        Tarjeta tarjeta = new Tarjeta(etiquetaTitulo, etiquetaDescripcion);
        tarjeta.getChildren().addAll(contenido);
        return tarjeta;
    }
}
