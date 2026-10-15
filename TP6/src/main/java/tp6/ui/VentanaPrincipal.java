package tp6.ui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;

// Ventana principal: menú lateral con los ejercicios (y su descripción) y la pantalla del ejercicio elegido
public class VentanaPrincipal extends JFrame {

    private final CardLayout cartas = new CardLayout();
    private final JPanel contenido = new JPanel(cartas);
    private final JButton[] botones = new JButton[3];

    public VentanaPrincipal() {
        super("TP6 · Modelos y Simulación — Modelo de Cole");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        contenido.add(new PanelEjercicio1(), "1");
        contenido.add(new PanelEjercicio2(), "2");
        contenido.add(new PanelEjercicio3(), "3");

        JPanel menu = new JPanel();
        menu.setLayout(new javax.swing.BoxLayout(menu, javax.swing.BoxLayout.Y_AXIS));
        menu.setBackground(Estilo.LATERAL);
        menu.setBorder(BorderFactory.createEmptyBorder(22, 14, 14, 14));
        menu.setPreferredSize(new Dimension(270, 0));

        JLabel marca = new JLabel("Modelo de Cole");
        marca.setForeground(Color.WHITE);
        marca.setFont(marca.getFont().deriveFont(Font.BOLD, 21f));
        JLabel sub = new JLabel("Bioimpedancia · TP6");
        sub.setForeground(new Color(0xA8A8B3));
        menu.add(marca);
        menu.add(sub);
        menu.add(Box.createVerticalStrut(22));

        botones[0] = opcion("1", "Ejercicio 1", "El modelo con tus propios parámetros",
                "Ingresá R0, R∞, fc y α y mirá la impedancia en función de la frecuencia.");
        botones[1] = opcion("2", "Ejercicio 2", "Cinco tejidos juntos",
                "Tabla 1 editable (músculo, hígado, pulmón, bazo) en un mismo espectro.");
        botones[2] = opcion("3", "Ejercicio 3", "Ajuste a datos reales",
                "Obtené R0, R∞, fc y α de mediciones de Dell'Osa et al. o de tu propio CSV.");
        for (JButton b : botones) {
            menu.add(b);
            menu.add(Box.createVerticalStrut(10));
        }
        menu.add(Box.createVerticalGlue());

        add(menu, BorderLayout.WEST);
        add(contenido, BorderLayout.CENTER);

        seleccionar("1");
        setSize(1280, 800);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
    }

    private JButton opcion(final String id, String titulo, String subtitulo, String descripcion) {
        JButton b = new JButton("<html><div style='width:170px'><b style='font-size:13px'>" + titulo + "</b> · "
                + subtitulo + "<br><span style='color:#A8A8B3'>" + descripcion + "</span></div></html>");
        b.setForeground(Color.WHITE);
        b.setHorizontalAlignment(JButton.LEFT);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(true);
        b.setBackground(Estilo.LATERAL);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        b.putClientProperty("JButton.buttonType", "borderless");
        b.addActionListener(e -> seleccionar(id));
        return b;
    }

    private void seleccionar(String id) {
        cartas.show(contenido, id);
        for (int i = 0; i < botones.length; i++) {
            boolean activo = String.valueOf(i + 1).equals(id);
            botones[i].setBackground(activo ? Estilo.ROJO : Estilo.LATERAL);
        }
    }
}
