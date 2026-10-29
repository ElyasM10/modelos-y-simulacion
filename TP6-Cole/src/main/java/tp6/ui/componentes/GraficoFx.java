package tp6.ui.componentes;

import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

/*
 * Gráfico de líneas y/o puntos de la interfaz (JavaFX), con eje X lineal o logarítmico.
 *
 * Hereda de LineChart, el gráfico de líneas que trae JavaFX. Se usa así:
 *   1) new GraficoFx(titulo, etiquetaEjeX, etiquetaEjeY, ejeXLogaritmico)
 *   2) agregarSerie(...) una vez por cada curva o conjunto de puntos
 *   3) se pone el gráfico dentro de un panel y JavaFX lo dibuja solo
 *
 * JavaFX no trae eje logarítmico. Para lograrlo se grafican los log10 de las frecuencias en un eje común
 * y las etiquetas del eje se vuelven a convertir (10^valor) y se muestran como Hz, kHz o MHz.
 */
public class GraficoFx extends LineChart<Number, Number> {

    // Colores de las series, en el orden en que se van agregando: rojo, verde, azul, naranja, magenta
    public static final Color[] COLORES = {Color.RED, Color.web("#008200"), Color.BLUE, Color.ORANGE, Color.MAGENTA};

    private final boolean ejeXLogaritmico;
    private final NumberAxis ejeX;
    // Texto de estilo (CSS) donde se van acumulando los colores de las series
    private final StringBuilder coloresSeries = new StringBuilder();
    // Mínimo y máximo del eje X (en log10 si el eje es logarítmico), para fijar los límites del eje
    private double minimoLog = Double.MAX_VALUE, maximoLog = -Double.MAX_VALUE;

    // Constructor público: crea los dos ejes (X e Y) y llama al constructor privado
    public GraficoFx(String titulo, String etiquetaEjeX, String etiquetaEjeY, boolean ejeXLogaritmico) {
        this(titulo, etiquetaEjeX, etiquetaEjeY, ejeXLogaritmico, new NumberAxis(), new NumberAxis());
    }

    // Constructor privado: existe porque LineChart necesita los ejes en su constructor (super), y también queremos guardar ejeX
    private GraficoFx(String titulo, String etiquetaEjeX, String etiquetaEjeY, boolean ejeXLogaritmico,
                      NumberAxis ejeX, NumberAxis ejeY) {
        super(ejeX, ejeY);
        this.ejeX = ejeX;
        this.ejeXLogaritmico = ejeXLogaritmico;
        // Título, sin animación al aparecer, con círculos en cada punto (después se ocultan en las series de línea)
        setTitle(titulo);
        setAnimated(false);
        setCreateSymbols(true);
        // Tamaño mínimo cero: permite que el gráfico se achique cuando la ventana se achica
        setMinSize(0, 0);
        // Nombres de los ejes y que no obliguen a incluir el cero (así el gráfico se ajusta a los datos)
        ejeX.setLabel(etiquetaEjeX);
        ejeY.setLabel(etiquetaEjeY);
        ejeX.setForceZeroInRange(false);
        ejeY.setForceZeroInRange(false);
        if (ejeXLogaritmico) configurarEjeLogaritmico();
    }

    // Agrega una serie. dibujarPuntos = true: solo círculos (mediciones); false: solo línea (curva del modelo).
    public void agregarSerie(String nombre, double[] valoresX, double[] valoresY, Color color, boolean dibujarPuntos) {
        // Paso 1: color de la serie. JavaFX pinta la serie número N con el "color buscado" CHART_COLOR_N,
        // así que se define ese color en el estilo del gráfico: así la línea, los puntos y la leyenda coinciden.
        coloresSeries.append("CHART_COLOR_").append(getData().size() + 1).append(": ").append(hexadecimal(color)).append("; ");
        setStyle(coloresSeries.toString());

        // Paso 2: se crea la serie y se le cargan los puntos
        XYChart.Series<Number, Number> serie = new XYChart.Series<>();
        serie.setName(nombre);
        for (int i = 0; i < valoresX.length; i++) {
            // Si el eje es logarítmico, el valor de X que se grafica es su log10
            double x = ejeXLogaritmico ? Math.log10(valoresX[i]) : valoresX[i];
            minimoLog = Math.min(minimoLog, x);
            maximoLog = Math.max(maximoLog, x);
            XYChart.Data<Number, Number> punto = new XYChart.Data<>(x, valoresY[i]);
            // JavaFX crea el círculo de cada punto más tarde; este "listener" avisa cuando existe, para poder darle estilo
            punto.nodeProperty().addListener((observable, anterior, nodo) -> estilizarPunto(nodo, dibujarPuntos));
            serie.getData().add(punto);
        }
        // Lo mismo para la línea de la serie: cuando JavaFX la crea, se le da estilo
        serie.nodeProperty().addListener((observable, anterior, nodo) -> estilizarLinea(nodo, dibujarPuntos));
        // Paso 3: en el eje logarítmico los límites se redondean a décadas enteras (por ejemplo, de 10^2 a 10^8)
        if (ejeXLogaritmico) {
            ejeX.setLowerBound(Math.floor(minimoLog));
            ejeX.setUpperBound(Math.ceil(maximoLog));
        }
        // Paso 4: recién acá la serie se agrega al gráfico y JavaFX la dibuja
        getData().add(serie);
    }

    // Serie de puntos: se oculta la línea que une los puntos (se la deja transparente)
    private static void estilizarLinea(Node nodo, boolean dibujarPuntos) {
        if (nodo != null && dibujarPuntos) nodo.setStyle("-fx-stroke: transparent;");
    }

    // Serie de línea: se ocultan los círculos de cada punto. Serie de puntos: círculos chicos.
    private static void estilizarPunto(Node nodo, boolean dibujarPuntos) {
        if (nodo == null) return;
        nodo.setStyle(dibujarPuntos ? "-fx-padding: 2.5px;" : "-fx-background-color: transparent; -fx-padding: 0;");
    }

    // El eje X logarítmico se arma con log10 de los valores, con una marca por década y etiquetas en Hz, kHz o MHz.
    private void configurarEjeLogaritmico() {
        // Los límites los fija agregarSerie (no se calculan solos); una marca por unidad de log10, o sea por década
        ejeX.setAutoRanging(false);
        ejeX.setTickUnit(1);
        // 9 marcas chicas entre dos grandes, como en un papel logarítmico (2, 3, 4... 9)
        ejeX.setMinorTickCount(9);
        // El eje guarda log10(f); para escribir la etiqueta se vuelve a f = 10^valor y se le pone unidad
        ejeX.setTickLabelFormatter(new StringConverter<Number>() {
            @Override public String toString(Number logaritmo) { return formatoFrecuencia(Math.pow(10, logaritmo.doubleValue())); }
            @Override public Number fromString(String texto) { return 0; }
        });
    }

    // Escribe una frecuencia con la unidad que corresponde: 100 Hz, 1 kHz, 10 MHz...
    private static String formatoFrecuencia(double frecuencia) {
        if (frecuencia >= 1e6) return String.format("%.0f MHz", frecuencia / 1e6);
        if (frecuencia >= 1e3) return String.format("%.0f kHz", frecuencia / 1e3);
        return String.format("%.0f Hz", frecuencia);
    }

    // Convierte un color de JavaFX (componentes de 0 a 1) al texto hexadecimal que usa el CSS, por ejemplo "#ff0000"
    private static String hexadecimal(Color color) {
        return String.format("#%02x%02x%02x", (int) Math.round(color.getRed() * 255),
                (int) Math.round(color.getGreen() * 255), (int) Math.round(color.getBlue() * 255));
    }
}
