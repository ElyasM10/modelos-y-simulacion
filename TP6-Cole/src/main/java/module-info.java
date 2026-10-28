module tp6 {
    requires javafx.controls;
    requires java.desktop;

    exports tp6;
    exports tp6.ui to javafx.graphics; // JavaFX necesita crear la clase Ventana (Application)
}
