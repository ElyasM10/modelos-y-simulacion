package tp6.ui;

import tp6.datos.Medicion;
import tp6.datos.Registros;
import tp6.grafico.ConstructorGraficos;
import tp6.modelo.AjusteCole;
import tp6.modelo.ModeloCole;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Font;
import java.io.File;

// Ejercicio 3: ajuste del modelo de Cole a una medición real (de la base de datos o un CSV propio)
public class PanelEjercicio3 extends JPanel {

    private final JComboBox<String> registros = new JComboBox<String>(Registros.DESCRIPCION);
    private final JLabel info = new JLabel(" ");
    private final JLabel vR0 = valor(), vRInf = valor(), vFc = valor(), vAlfa = valor(), vError = valor();
    private final JLabel estado = new JLabel(" ");
    private final PanelGraficos graficos = new PanelGraficos("Nyquist", "Módulo", "Fase");

    private Medicion actual;

    public PanelEjercicio3() {
        super(new BorderLayout(16, 0));
        setBorder(BorderFactory.createEmptyBorder(18, 20, 12, 20));

        PanelEncabezado encabezado = new PanelEncabezado("Ejercicio 3 · Ajuste a datos reales",
                "Elegí una medición de la base de datos de Dell'Osa et al. (Zenodo) o cargá tu propio CSV "
                        + "(columnas: frecuencia [Hz], Re(Z) [Ω], Im(Z) [Ω]). El programa busca R0, R∞, fc y α "
                        + "con mínimos cuadrados no lineales (Levenberg-Marquardt) y superpone el modelo ajustado.");

        registros.addActionListener(e -> cargarIncluido());
        JButton abrir = new JButton("Abrir CSV propio…");
        abrir.addActionListener(e -> abrirArchivo());

        Formulario origen = new Formulario();
        origen.agregarAnchoCompleto(registros);
        origen.agregarAnchoCompleto(abrir);
        origen.agregarAnchoCompleto(info);

        Formulario resultados = new Formulario();
        resultados.agregar("R0 [Ω]", vR0);
        resultados.agregar("R∞ [Ω]", vRInf);
        resultados.agregar("fc [Hz]", vFc);
        resultados.agregar("α", vAlfa);
        resultados.agregar("Error medio [Ω]", vError);

        JPanel lateral = new JPanel();
        lateral.setLayout(new BoxLayout(lateral, BoxLayout.Y_AXIS));
        lateral.add(Estilo.tarjeta("Medición", origen));
        lateral.add(Box.createVerticalStrut(12));
        lateral.add(Estilo.tarjeta("Parámetros ajustados", resultados));

        JPanel izquierda = new JPanel(new BorderLayout());
        izquierda.add(lateral, BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 6));
        centro.add(graficos, BorderLayout.CENTER);
        centro.add(estado, BorderLayout.SOUTH);

        add(encabezado, BorderLayout.NORTH);
        add(izquierda, BorderLayout.WEST);
        add(centro, BorderLayout.CENTER);

        cargarIncluido();
    }

    private static JLabel valor() {
        JLabel l = new JLabel("—");
        l.setFont(l.getFont().deriveFont(Font.BOLD, 15f));
        return l;
    }

    private void cargarIncluido() {
        try {
            ajustar(Registros.leer(registros.getSelectedIndex()));
        } catch (java.io.IOException e) {
            Estilo.mostrarError(estado, e.getMessage());
        }
    }

    private void abrirArchivo() {
        JFileChooser selector = new JFileChooser(new File("."));
        selector.setFileFilter(new FileNameExtensionFilter("Archivos de datos (CSV, TXT)", "csv", "txt", "dat"));
        if (selector.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File archivo = selector.getSelectedFile();
        try {
            ajustar(Medicion.leer(archivo.getName(), archivo));
        } catch (java.io.IOException e) {
            Estilo.mostrarError(estado, e.getMessage());
        }
    }

    // El ajuste corre en segundo plano para no congelar la ventana
    private void ajustar(final Medicion medicion) {
        actual = medicion;
        estado.setForeground(Estilo.TEXTO_SUAVE);
        estado.setText("Ajustando…");

        new SwingWorker<AjusteCole, Void>() {
            @Override
            protected AjusteCole doInBackground() {
                return AjusteCole.ajustar(medicion.getF(), medicion.getRe(), medicion.getIm());
            }

            @Override
            protected void done() {
                if (medicion != actual) return;   // el usuario ya eligió otra medición
                try {
                    AjusteCole ajuste = get();
                    ModeloCole m = ajuste.getModelo();
                    double[] f = medicion.getF();

                    vR0.setText(String.format("%.2f", m.getR0()));
                    vRInf.setText(String.format("%.2f", m.getRInf()));
                    vFc.setText(String.format("%.1f", m.getFc()));
                    vAlfa.setText(String.format("%.3f", m.getAlfa()));
                    vError.setText(String.format("%.3f", ajuste.getRmse()));
                    info.setText(String.format("<html>%d puntos · de %.0f Hz a %.0f Hz</html>",
                            f.length, f[0], f[f.length - 1]));

                    graficos.mostrar(ConstructorGraficos.ajuste(medicion.getNombre(), f, medicion.getRe(),
                            medicion.getIm(), m));
                    Estilo.mostrarOk(estado, "Ajuste terminado. Rueda del mouse: zoom · clic derecho: guardar como PNG");
                } catch (Exception e) {
                    Throwable causa = e.getCause() != null ? e.getCause() : e;
                    Estilo.mostrarError(estado, "No se pudo ajustar: " + causa.getMessage());
                }
            }
        }.execute();
    }
}
