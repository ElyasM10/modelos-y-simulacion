package tp6.consola;

import java.awt.Graphics2D;

// Algo que se puede dibujar dentro de un rectángulo de una imagen: un gráfico o una tabla.
// LaminaPng acomoda varios paneles en una misma imagen.
public interface PanelPng {

    // Dibuja el panel dentro del rectángulo que empieza en (x, y) y mide ancho por alto píxeles
    void dibujar(Graphics2D lienzo, int x, int y, int ancho, int alto);
}
