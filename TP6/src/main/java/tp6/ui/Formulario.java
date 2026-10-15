package tp6.ui;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

// Panel de "etiqueta + campo" una fila debajo de la otra
public class Formulario extends JPanel {

    private int fila = 0;

    public Formulario() {
        super(new GridBagLayout());
        setOpaque(false);
    }

    public void agregar(String etiqueta, JComponent campo) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = fila++;
        c.insets = new Insets(3, 0, 3, 8);
        c.anchor = GridBagConstraints.WEST;

        c.gridx = 0;
        add(new JLabel(etiqueta), c);

        c.gridx = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(3, 0, 3, 0);
        add(campo, c);
    }

    // componente que ocupa todo el ancho (por ejemplo un botón)
    public void agregarAnchoCompleto(JComponent componente) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = fila++;
        c.gridx = 0;
        c.gridwidth = 2;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(8, 0, 3, 0);
        add(componente, c);
    }
}
