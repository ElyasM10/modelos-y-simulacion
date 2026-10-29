package tp6;

import tp6.consola.ModoConsola;
import tp6.ui.Ventana;

// Punto de entrada del TP6. Comentá la línea del modo que no quieras ejecutar.
public class Main {

    // Solo una de las dos líneas de abajo debe estar activa. Las dos versiones son independientes:
    // la consola no usa la interfaz y la interfaz no usa la consola; solo comparten tp6.modelo.Cole.
    public static void main(String[] args) throws Exception {
        ModoConsola.ejecutar();   // versión consola: imprime resultados y guarda los PNG en graficos/
      //   Ventana.abrir();       // versión con interfaz gráfica (JavaFX)
    }
}
