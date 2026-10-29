package tp6.ui.componentes;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

// Contenedor blanco con sombra. Puede llevar un título y una descripción arriba del contenido.
// Es un VBox (apila lo que tiene adentro); el aspecto de tarjeta (fondo blanco, sombra, bordes redondeados) lo da la clase CSS "tarjeta".
public class Tarjeta extends VBox {

    // Tarjeta sin título: solo contiene lo que se le pasa (8 = separación en píxeles entre los elementos)
    public Tarjeta(Node... contenido) {
        super(8, contenido);
        getStyleClass().add("tarjeta");
    }

    // Tarjeta con título y descripción arriba y, debajo, el contenido (por ejemplo un formulario o un gráfico)
    public static Tarjeta conTitulo(String titulo, String descripcion, Node... contenido) {
        // Label = texto. Las clases CSS "titulo-pagina" y "subtitulo" (en estilo.css) les dan tamaño y color
        Label etiquetaTitulo = new Label(titulo);
        etiquetaTitulo.getStyleClass().add("titulo-pagina");
        Label etiquetaDescripcion = new Label(descripcion);
        etiquetaDescripcion.getStyleClass().add("subtitulo");
        // Primero se crea la tarjeta con el título y la descripción; después se le agrega el contenido debajo
        Tarjeta tarjeta = new Tarjeta(etiquetaTitulo, etiquetaDescripcion);
        tarjeta.getChildren().addAll(contenido);
        return tarjeta;
    }
}
