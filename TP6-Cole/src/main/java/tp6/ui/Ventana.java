package tp6.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import tp6.ui.ejercicio1.PanelEjercicio1;
import tp6.ui.ejercicio2.PanelEjercicio2;
import tp6.ui.ejercicio3.PanelEjercicio3;

/*
 * TP6 - versión con interfaz gráfica (JavaFX). Una pestaña por ejercicio.
 *
 * Cómo está armada una aplicación JavaFX (de afuera hacia adentro):
 *   Stage (la ventana del sistema operativo)
 *     -> Scene (el contenido de la ventana; acá se le engancha el archivo de estilos CSS)
 *          -> TabPane (las pestañas)
 *               -> un panel por pestaña (PanelEjercicio1, PanelEjercicio2, PanelEjercicio3)
 *
 * Esta clase hereda de Application: JavaFX se encarga de arrancar todo y llama al método start().
 */
public class Ventana extends Application {

    // Lo llama Main. Application.launch arranca JavaFX, crea una Ventana y ejecuta start().
    // Este método queda esperando hasta que se cierra la ventana.
    public static void abrir() {
        Application.launch(Ventana.class);
    }

    // JavaFX llama a este método cuando la aplicación está lista. escenario es la ventana principal.
    @Override
    public void start(Stage escenario) {
        // Paso 1: se crean las 3 pestañas; cada una contiene el panel de su ejercicio
        TabPane pestanias = new TabPane(
                pestania("Ej 1 · Modelo", new PanelEjercicio1()),
                pestania("Ej 2 · 5 tejidos", new PanelEjercicio2()),
                pestania("Ej 3 · Ajuste", new PanelEjercicio3()));
        // Paso 2: la escena contiene las pestañas y define el tamaño inicial (ancho 1100, alto 700)
        Scene escena = new Scene(pestanias, 1100, 700);
        // Paso 3: se carga el CSS (colores, tarjetas, botón azul...) desde los recursos del proyecto
        escena.getStylesheets().add(getClass().getResource("/estilos/estilo.css").toExternalForm());
        // Paso 4: se pone título a la ventana, se le asigna la escena y se la muestra
        escenario.setTitle("TP6 - Modelo de Cole");
        escenario.setScene(escena);
        escenario.show();
    }

    // Crea una pestaña con su título y su contenido. setClosable(false) quita la "x" para que no se puedan cerrar.
    private static Tab pestania(String titulo, javafx.scene.Node contenido) {
        Tab pestania = new Tab(titulo, contenido);
        pestania.setClosable(false);
        return pestania;
    }
}
