package tp6;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import tp6.ui.VentanaPrincipal;

import javax.swing.SwingUtilities;
import java.util.Collections;

// Punto de entrada: abre la aplicación gráfica (para generar solo los PNG por consola: tp6.MainConsola)
public class Main {

    public static void main(String[] args) {

        FlatLaf.setGlobalExtraDefaults(Collections.singletonMap("@accentColor", "#7B241C"));
        FlatLightLaf.setup();

        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
