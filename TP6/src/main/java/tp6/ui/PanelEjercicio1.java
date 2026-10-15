package tp6.ui;

import org.apache.commons.math3.complex.Complex;
import tp6.ejercicios.Ejercicio2;
import tp6.grafico.ConstructorGraficos;
import tp6.modelo.ModeloCole;
import tp6.modelo.Tejido;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.util.Collections;

// Ejercicio 1: el modelo de Cole con parámetros ingresados por el usuario
public class PanelEjercicio1 extends JPanel {

    private static final String PERSONALIZADO = "Personalizado";

    private final JComboBox<String> preset = new JComboBox<String>();
    private final JTextField r0 = Estilo.campo(385);
    private final JTextField rInf = Estilo.campo(70);
    private final JTextField fc = Estilo.campo(37000);
    private final JTextField alfa = Estilo.campo(0.27);
    private final JTextField fMin = Estilo.campo(10);
    private final JTextField fMax = Estilo.campo(1e9);
    private final JTextField fConsulta = Estilo.campo(37000);
    private final JLabel resultado = new JLabel(" ");
    private final JLabel estado = new JLabel(" ");
    private final PanelGraficos graficos = new PanelGraficos("Módulo", "Fase", "Nyquist");

    private boolean cargando = false;

    public PanelEjercicio1() {
        super(new BorderLayout(16, 0));
        setBorder(BorderFactory.createEmptyBorder(18, 20, 12, 20));

        PanelEncabezado encabezado = new PanelEncabezado("Ejercicio 1 · Modelo de Cole",
                "Z(ω) = R∞ + (R0 − R∞) / (1 + (jωτ)<sup>α</sup>), con τ = 1/(2π·fc). Ingresá los parámetros "
                        + "(o elegí un tejido de la Tabla 1 como punto de partida) y mirá cómo cambia la impedancia "
                        + "con la frecuencia. También podés calcular Z en una frecuencia puntual.");

        // ---- formulario de parámetros
        preset.addItem(PERSONALIZADO);
        for (String nombre : Ejercicio2.NOMBRES) preset.addItem(nombre);
        preset.setSelectedIndex(1);
        preset.addActionListener(e -> cargarPreset());

        Formulario parametros = new Formulario();
        parametros.agregar("Tejido de partida", preset);
        parametros.agregar("R0 [Ω]", r0);
        parametros.agregar("R∞ [Ω]", rInf);
        parametros.agregar("fc [Hz]", fc);
        parametros.agregar("α (0 – 1)", alfa);
        parametros.agregar("f mínima [Hz]", fMin);
        parametros.agregar("f máxima [Hz]", fMax);
        JButton graficar = new JButton("Graficar");
        graficar.addActionListener(e -> graficar());
        parametros.agregarAnchoCompleto(graficar);

        Formulario consulta = new Formulario();
        consulta.agregar("f [Hz]", fConsulta);
        JButton calcular = new JButton("Calcular Z");
        calcular.addActionListener(e -> calcularPunto());
        consulta.agregarAnchoCompleto(calcular);
        consulta.agregarAnchoCompleto(resultado);

        JPanel lateral = new JPanel();
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.add(Estilo.tarjeta("Parámetros del modelo", parametros));
        lateral.add(javax.swing.Box.createVerticalStrut(12));
        lateral.add(Estilo.tarjeta("Impedancia en una frecuencia", consulta));

        JPanel izquierda = new JPanel(new BorderLayout());
        izquierda.add(lateral, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.add(graficos, BorderLayout.CENTER);
        centro.add(estado, BorderLayout.SOUTH);

        add(encabezado, BorderLayout.NORTH);
        add(izquierda, BorderLayout.WEST);
        add(centro, BorderLayout.CENTER);

        // Enter en cualquier campo vuelve a graficar
        for (JTextField c : new JTextField[]{r0, rInf, fc, alfa, fMin, fMax}) {
            c.addActionListener(e -> graficar());
            c.addCaretListener(e -> { if (!cargando) preset.setSelectedItem(PERSONALIZADO); });
        }
        fConsulta.addActionListener(e -> calcularPunto());

        graficar();
        calcularPunto();
    }

    private void cargarPreset() {
        int t = preset.getSelectedIndex() - 1;   // el primero es "Personalizado"
        if (t < 0) return;
        cargando = true;
        r0.setText(Estilo.texto(Ejercicio2.R0[t]));
        rInf.setText(Estilo.texto(Ejercicio2.R_INF[t]));
        fc.setText(Estilo.texto(Ejercicio2.FC[t]));
        alfa.setText(Estilo.texto(Ejercicio2.ALFA[t]));
        fConsulta.setText(Estilo.texto(Ejercicio2.FC[t]));
        cargando = false;
        graficar();
        calcularPunto();
    }

    private ModeloCole modelo() {
        double a = Estilo.leer(r0, "R0"), b = Estilo.leer(rInf, "R∞"), c = Estilo.leer(fc, "fc"),
                d = Estilo.leer(alfa, "α");
        ModeloCole.validar(a, b, c, d);
        return new ModeloCole(a, b, c, d);
    }

    private void graficar() {
        try {
            ModeloCole m = modelo();
            double desde = Estilo.leer(fMin, "f mínima");
            double hasta = Estilo.leer(fMax, "f máxima");
            if (desde <= 0 || hasta <= desde) {
                throw new IllegalArgumentException("El rango de frecuencias debe cumplir 0 < f mínima < f máxima");
            }
            String nombre = preset.getSelectedIndex() > 0 ? (String) preset.getSelectedItem() : "Modelo de Cole";
            graficos.mostrar(ConstructorGraficos.espectro("Ejercicio 1",
                    Collections.singletonList(new Tejido(nombre, m)), desde, hasta));
            Estilo.mostrarOk(estado, "Gráficos actualizados. Rueda del mouse: zoom · clic derecho: guardar como PNG");
        } catch (IllegalArgumentException e) {
            Estilo.mostrarError(estado, e.getMessage());
        }
    }

    private void calcularPunto() {
        try {
            ModeloCole m = modelo();
            double f = Estilo.leer(fConsulta, "f");
            if (f <= 0) throw new IllegalArgumentException("La frecuencia debe ser mayor que 0");
            Complex z = m.impedancia(f);
            resultado.setText(String.format("<html>Z = %.3f %s j·%.3f Ω<br>|Z| = %.3f Ω · fase = %.3f°</html>",
                    z.getReal(), z.getImaginary() < 0 ? "−" : "+", Math.abs(z.getImaginary()), z.abs(),
                    Math.toDegrees(z.getArgument())));
        } catch (IllegalArgumentException e) {
            resultado.setText("<html><font color='#C0392B'>" + e.getMessage() + "</font></html>");
        }
    }
}
