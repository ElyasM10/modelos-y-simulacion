package tp6.ui;

import tp6.ejercicios.Ejercicio2;
import tp6.grafico.ConstructorGraficos;
import tp6.modelo.ModeloCole;
import tp6.modelo.Tejido;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

// Ejercicio 2: varios tejidos (Tabla 1, editable) en un mismo espectro
public class PanelEjercicio2 extends JPanel {

    private static final String[] COLUMNAS = {"Mostrar", "Tejido", "R0 [Ω]", "R∞ [Ω]", "fc [Hz]", "α"};

    private final DefaultTableModel modeloTabla = new DefaultTableModel(COLUMNAS, 0) {
        @Override
        public Class<?> getColumnClass(int columna) {
            if (columna == 0) return Boolean.class;
            return columna == 1 ? String.class : Double.class;
        }
    };
    private final JTable tabla = new JTable(modeloTabla);
    private final JTextField fMin = Estilo.campo(10);
    private final JTextField fMax = Estilo.campo(1e9);
    private final JLabel estado = new JLabel(" ");
    private final PanelGraficos graficos = new PanelGraficos("Módulo", "Fase", "Nyquist");

    public PanelEjercicio2() {
        super(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(18, 20, 12, 20));

        PanelEncabezado encabezado = new PanelEncabezado("Ejercicio 2 · Varios tejidos en un mismo espectro",
                "Modelo de Cole para los 5 tejidos de la Tabla 1, graficados juntos. La tabla se puede editar "
                        + "(doble clic en un valor): cambiá parámetros, destildá tejidos, agregá nuevos y volvé a graficar.");

        tabla.setRowHeight(26);
        tabla.getColumnModel().getColumn(0).setMaxWidth(70);
        restaurarTabla1();

        JButton graficar = new JButton("Graficar");
        graficar.addActionListener(e -> graficar());
        JButton agregar = new JButton("Agregar tejido");
        agregar.addActionListener(e -> {
            modeloTabla.addRow(new Object[]{true, "Nuevo tejido", 400.0, 80.0, 50000.0, 0.5});
            tabla.setRowSelectionInterval(modeloTabla.getRowCount() - 1, modeloTabla.getRowCount() - 1);
        });
        JButton quitar = new JButton("Quitar seleccionado");
        quitar.addActionListener(e -> {
            int fila = tabla.getSelectedRow();
            if (fila >= 0) modeloTabla.removeRow(fila);
        });
        JButton restaurar = new JButton("Restaurar Tabla 1");
        restaurar.addActionListener(e -> { restaurarTabla1(); graficar(); });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botones.setOpaque(false);
        botones.add(agregar);
        botones.add(quitar);
        botones.add(restaurar);
        botones.add(new JLabel("   f mín [Hz]"));
        botones.add(fMin);
        botones.add(new JLabel("f máx [Hz]"));
        botones.add(fMax);
        botones.add(graficar);

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new java.awt.Dimension(100, 170));

        JPanel superior = new JPanel(new BorderLayout(0, 8));
        superior.add(encabezado, BorderLayout.NORTH);
        superior.add(Estilo.tarjeta("Parámetros de cada tejido", wrap(scroll, botones)), BorderLayout.CENTER);

        JPanel centro = new JPanel(new BorderLayout(0, 6));
        centro.add(graficos, BorderLayout.CENTER);
        centro.add(estado, BorderLayout.SOUTH);

        add(superior, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);

        graficar();
    }

    private static JPanel wrap(JScrollPane tabla, JPanel botones) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setOpaque(false);
        p.add(tabla, BorderLayout.CENTER);
        p.add(botones, BorderLayout.SOUTH);
        return p;
    }

    private void restaurarTabla1() {
        modeloTabla.setRowCount(0);
        for (int t = 0; t < Ejercicio2.NOMBRES.length; t++) {
            modeloTabla.addRow(new Object[]{true, Ejercicio2.NOMBRES[t], Ejercicio2.R0[t], Ejercicio2.R_INF[t],
                    Ejercicio2.FC[t], Ejercicio2.ALFA[t]});
        }
    }

    private void graficar() {
        if (tabla.isEditing()) tabla.getCellEditor().stopCellEditing();
        try {
            double desde = Estilo.leer(fMin, "f mín");
            double hasta = Estilo.leer(fMax, "f máx");
            if (desde <= 0 || hasta <= desde) {
                throw new IllegalArgumentException("El rango de frecuencias debe cumplir 0 < f mín < f máx");
            }

            List<Tejido> tejidos = new ArrayList<Tejido>();
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                if (!(Boolean) modeloTabla.getValueAt(i, 0)) continue;
                String nombre = String.valueOf(modeloTabla.getValueAt(i, 1));
                try {
                    double r0 = (Double) modeloTabla.getValueAt(i, 2);
                    double rInf = (Double) modeloTabla.getValueAt(i, 3);
                    double fc = (Double) modeloTabla.getValueAt(i, 4);
                    double alfa = (Double) modeloTabla.getValueAt(i, 5);
                    ModeloCole.validar(r0, rInf, fc, alfa);
                    tejidos.add(new Tejido(nombre, new ModeloCole(r0, rInf, fc, alfa)));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(nombre + ": " + e.getMessage());
                }
            }
            if (tejidos.isEmpty()) throw new IllegalArgumentException("Tildá al menos un tejido para graficar");

            graficos.mostrar(ConstructorGraficos.espectro("Ejercicio 2", tejidos, desde, hasta));
            Estilo.mostrarOk(estado, tejidos.size() + " tejido(s) graficados. Rueda del mouse: zoom · clic derecho: guardar como PNG");
        } catch (IllegalArgumentException e) {
            Estilo.mostrarError(estado, e.getMessage());
        }
    }
}
