package tp6.ui;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;

// Colores, fuentes y pequeños ayudantes comunes a todas las pantallas
public class Estilo {

    public static final Color ROJO = new Color(0x7B241C);          // color principal (el mismo del informe)
    public static final Color LATERAL = new Color(0x26262E);       // fondo del menú lateral
    public static final Color LATERAL_SELECCION = new Color(0x3A3A46);
    public static final Color TEXTO_SUAVE = new Color(0x6B6B76);
    public static final Color ERROR = new Color(0xC0392B);
    public static final Color OK = new Color(0x2E7D32);

    public static JLabel titulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 22f));
        return l;
    }

    public static JLabel seccion(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(l.getFont().deriveFont(Font.BOLD, 13f));
        l.setForeground(ROJO);
        return l;
    }

    // Panel con un título arriba y el contenido debajo, dentro de un borde suave (una "tarjeta")
    public static JPanel tarjeta(String titulo, JComponent contenido) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD8D8DE), 1, true),
                BorderFactory.createEmptyBorder(10, 12, 12, 12)));
        p.add(seccion(titulo), BorderLayout.NORTH);
        p.add(contenido, BorderLayout.CENTER);
        return p;
    }

    public static JTextField campo(double valor) {
        JTextField c = new JTextField(texto(valor), 9);
        c.setHorizontalAlignment(JTextField.RIGHT);
        return c;
    }

    // Número sin ceros ni notación científica de más (385, 0.27, 37000)
    public static String texto(double v) {
        return new BigDecimal(v).round(new java.math.MathContext(10)).stripTrailingZeros().toPlainString();
    }

    // Lee un número de un campo (acepta coma o punto decimal y notación 1e3); si no es válido, explica cuál
    public static double leer(JTextField campo, String nombre) {
        try {
            return Double.parseDouble(campo.getText().trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("«" + nombre + "» no es un número válido");
        }
    }

    public static void mostrarError(JLabel estado, String mensaje) {
        estado.setForeground(ERROR);
        estado.setText("⚠ " + mensaje);
    }

    public static void mostrarOk(JLabel estado, String mensaje) {
        estado.setForeground(OK);
        estado.setText("✔ " + mensaje);
    }
}
