package tp6.ui.componentes;

import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

// Gráfico de líneas y/o puntos de la interfaz (JavaFX), con eje X lineal o logarítmico.
public class GraficoFx extends LineChart<Number, Number> {

    public static final Color[] COLORES = {Color.RED, Color.web("#008200"), Color.BLUE, Color.ORANGE, Color.MAGENTA};

    private final boolean ejeXLogaritmico;
    private final NumberAxis ejeX;
    private final StringBuilder coloresSeries = new StringBuilder();
    private double minimoLog = Double.MAX_VALUE, maximoLog = -Double.MAX_VALUE;

    public GraficoFx(String titulo, String etiquetaEjeX, String etiquetaEjeY, boolean ejeXLogaritmico) {
        this(titulo, etiquetaEjeX, etiquetaEjeY, ejeXLogaritmico, new NumberAxis(), new NumberAxis());
    }

    private GraficoFx(String titulo, String etiquetaEjeX, String etiquetaEjeY, boolean ejeXLogaritmico,
                      NumberAxis ejeX, NumberAxis ejeY) {
        super(ejeX, ejeY);
        this.ejeX = ejeX;
        this.ejeXLogaritmico = ejeXLogaritmico;
        setTitle(titulo);
        setAnimated(false);
        setCreateSymbols(true);
        setMinSize(0, 0);
        ejeX.setLabel(etiquetaEjeX);
        ejeY.setLabel(etiquetaEjeY);
        ejeX.setForceZeroInRange(false);
        ejeY.setForceZeroInRange(false);
        if (ejeXLogaritmico) configurarEjeLogaritmico();
    }

    // Agrega una serie. dibujarPuntos = true: solo círculos (mediciones); false: solo línea (curva del modelo).
    public void agregarSerie(String nombre, double[] valoresX, double[] valoresY, Color color, boolean dibujarPuntos) {
        // El color se define como color "buscado" del gráfico: así la línea, los puntos y la leyenda coinciden.
        coloresSeries.append("CHART_COLOR_").append(getData().size() + 1).append(": ").append(hexadecimal(color)).append("; ");
        setStyle(coloresSeries.toString());

        XYChart.Series<Number, Number> serie = new XYChart.Series<>();
        serie.setName(nombre);
        for (int i = 0; i < valoresX.length; i++) {
            double x = ejeXLogaritmico ? Math.log10(valoresX[i]) : valoresX[i];
            minimoLog = Math.min(minimoLog, x);
            maximoLog = Math.max(maximoLog, x);
            XYChart.Data<Number, Number> punto = new XYChart.Data<>(x, valoresY[i]);
            punto.nodeProperty().addListener((observable, anterior, nodo) -> estilizarPunto(nodo, dibujarPuntos));
            serie.getData().add(punto);
        }
        serie.nodeProperty().addListener((observable, anterior, nodo) -> estilizarLinea(nodo, dibujarPuntos));
        if (ejeXLogaritmico) {
            ejeX.setLowerBound(Math.floor(minimoLog));
            ejeX.setUpperBound(Math.ceil(maximoLog));
        }
        getData().add(serie);
    }

    private static void estilizarLinea(Node nodo, boolean dibujarPuntos) {
        if (nodo != null && dibujarPuntos) nodo.setStyle("-fx-stroke: transparent;");
    }

    private static void estilizarPunto(Node nodo, boolean dibujarPuntos) {
        if (nodo == null) return;
        nodo.setStyle(dibujarPuntos ? "-fx-padding: 2.5px;" : "-fx-background-color: transparent; -fx-padding: 0;");
    }

    // El eje X logarítmico se arma con log10 de los valores, con una marca por década y etiquetas en Hz, kHz o MHz.
    private void configurarEjeLogaritmico() {
        ejeX.setAutoRanging(false);
        ejeX.setTickUnit(1);
        ejeX.setMinorTickCount(9);
        ejeX.setTickLabelFormatter(new StringConverter<Number>() {
            @Override public String toString(Number logaritmo) { return formatoFrecuencia(Math.pow(10, logaritmo.doubleValue())); }
            @Override public Number fromString(String texto) { return 0; }
        });
    }

    private static String formatoFrecuencia(double frecuencia) {
        if (frecuencia >= 1e6) return String.format("%.0f MHz", frecuencia / 1e6);
        if (frecuencia >= 1e3) return String.format("%.0f kHz", frecuencia / 1e3);
        return String.format("%.0f Hz", frecuencia);
    }

    private static String hexadecimal(Color color) {
        return String.format("#%02x%02x%02x", (int) Math.round(color.getRed() * 255),
                (int) Math.round(color.getGreen() * 255), (int) Math.round(color.getBlue() * 255));
    }
}
