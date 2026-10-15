package tp6.ui;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.JTabbedPane;

// Pestañas con varios gráficos interactivos (zoom con el mouse; clic derecho: guardar como PNG, copiar, imprimir)
public class PanelGraficos extends JTabbedPane {

    private final ChartPanel[] paneles;

    public PanelGraficos(String... nombres) {
        paneles = new ChartPanel[nombres.length];
        for (int i = 0; i < nombres.length; i++) {
            paneles[i] = new ChartPanel(null);
            paneles[i].setMouseWheelEnabled(true);
            addTab(nombres[i], paneles[i]);
        }
    }

    public void mostrar(JFreeChart... graficos) {
        for (int i = 0; i < paneles.length; i++) {
            paneles[i].setChart(graficos[i]);
        }
    }
}
