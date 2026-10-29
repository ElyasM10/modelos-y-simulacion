// Descripción del proyecto como módulo de Java: qué necesita y qué deja ver al exterior.
module tp6 {
    requires javafx.controls;  // la librería de JavaFX con botones, gráficos, pestañas, etc.
    requires java.desktop;     // java.awt: lo usa la versión consola para dibujar las imágenes PNG

    exports tp6;
    exports tp6.ui to javafx.graphics; // JavaFX necesita crear la clase Ventana (Application)
}
