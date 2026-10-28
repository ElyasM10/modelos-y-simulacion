package tp6.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import tp6.ui.ejercicio1.PanelEjercicio1;
import tp6.ui.ejercicio2.PanelEjercicio2;
import tp6.ui.ejercicio3.PanelEjercicio3;

// TP6 - versión con interfaz gráfica (JavaFX). Una pestaña por ejercicio.
public class Ventana extends Application {

    public static void abrir() {
        Application.launch(Ventana.class);
    }

    @Override
    public void start(Stage escenario) {
        TabPane pestanias = new TabPane(
                pestania("Ej 1 · Modelo", new PanelEjercicio1()),
                pestania("Ej 2 · 5 tejidos", new PanelEjercicio2()),
                pestania("Ej 3 · Ajuste", new PanelEjercicio3()));
        Scene escena = new Scene(pestanias, 1100, 700);
        escena.getStylesheets().add(getClass().getResource("/estilos/estilo.css").toExternalForm());
        escenario.setTitle("TP6 - Modelo de Cole");
        escenario.setScene(escena);
        escenario.show();
    }

    private static Tab pestania(String titulo, javafx.scene.Node contenido) {
        Tab pestania = new Tab(titulo, contenido);
        pestania.setClosable(false);
        return pestania;
    }
}
