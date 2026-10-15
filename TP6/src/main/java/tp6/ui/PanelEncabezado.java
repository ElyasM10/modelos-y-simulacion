package tp6.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;

// Título del ejercicio con su descripción
public class PanelEncabezado extends JPanel {

    public PanelEncabezado(String titulo, String descripcionHtml) {
        super(new BorderLayout(0, 6));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        setOpaque(false);

        add(Estilo.titulo(titulo), BorderLayout.NORTH);

        JLabel descripcion = new JLabel("<html><body style='width:760px'>" + descripcionHtml + "</body></html>");
        descripcion.setForeground(Estilo.TEXTO_SUAVE);
        add(descripcion, BorderLayout.CENTER);
    }
}
