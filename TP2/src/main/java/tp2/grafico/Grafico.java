package tp2.grafico;

import tp2.funciones.Funcion;

import javax.swing.*;
import java.awt.*;

public class Grafico extends JPanel {

    private Funcion funcion;

    private double tiempoInicial;
    private double tiempoFinal;

    // Período de muestreo (Ts) de la implementación digital de la señal.
    private double periodoMuestreo;

    public Grafico(
            Funcion funcion,
            double tiempoInicial,
            double tiempoFinal,
            double periodoMuestreo) {

        this.funcion = funcion;
        this.tiempoInicial = tiempoInicial;
        this.tiempoFinal = tiempoFinal;
        this.periodoMuestreo = periodoMuestreo;

        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D grafico = (Graphics2D) g;

        // Mejor calidad de dibujo
        grafico.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        int ancho = getWidth();
        int alto = getHeight();

        // Márgenes
        int margenIzquierdo = 80;
        int margenDerecho = 40;
        int margenSuperior = 100;
        int margenInferior = 60;

        int anchoGrafico =
                ancho - margenIzquierdo - margenDerecho;

        int altoGrafico =
                alto - margenSuperior - margenInferior;

        // ==========================================
        // MUESTREO DIGITAL: x[n] = x(n·Ts)
        // ==========================================
        // La cantidad de muestras depende del período de muestreo (Ts),
        // no del ancho en píxeles de la ventana: así la señal queda
        // implementada en forma digital de verdad (independiente de
        // cómo se la dibuje o del tamaño de la ventana).

        int cantidadPuntos =
                (int) Math.floor(
                        (tiempoFinal - tiempoInicial) / periodoMuestreo
                ) + 1;

        double[] tiempos =
                new double[cantidadPuntos];

        double[] valores =
                new double[cantidadPuntos];

        double maximo = 0;

        double minimo = 0;

        for (int i = 0; i < cantidadPuntos; i++) {

            double t =
                    tiempoInicial
                            + i * periodoMuestreo;

            tiempos[i] = t;

            valores[i] =
                    funcion.calcular(t);

            if (valores[i] > maximo) {
                maximo = valores[i];
            }

            if (valores[i] < minimo) {
                minimo = valores[i];
            }
        }

        // Evitamos que los límites queden pegados
        double margenVertical =
                (maximo - minimo) * 0.15;

        if (margenVertical == 0) {
            margenVertical = 1;
        }

        maximo += margenVertical;
        minimo -= margenVertical;

        // ==========================================
        // TÍTULO
        // ==========================================

        grafico.setColor(Color.BLACK);

        grafico.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22
                )
        );

        String titulo =
                funcion.getNombre();

        grafico.drawString(
                titulo,
                margenIzquierdo,
                35
        );

        // ==========================================
        // FÓRMULA
        // ==========================================

        grafico.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        16
                )
        );

        String formula =
                obtenerFormula();

        grafico.drawString(
                formula,
                margenIzquierdo,
                65
        );

        // ==========================================
        // POSICIÓN DEL GRÁFICO
        // ==========================================

        int izquierda =
                margenIzquierdo;

        int derecha =
                ancho - margenDerecho;

        int arriba =
                margenSuperior;

        int abajo =
                alto - margenInferior;

        // ==========================================
        // CUADRÍCULA
        // ==========================================

        grafico.setColor(
                new Color(220, 220, 220)
        );

        grafico.setStroke(
                new BasicStroke(1)
        );

        // Líneas verticales
        int cantidadLineasVerticales = 10;

        for (int i = 0;
             i <= cantidadLineasVerticales;
             i++) {

            int x =
                    izquierda
                            + (derecha - izquierda)
                            * i
                            / cantidadLineasVerticales;

            grafico.drawLine(
                    x,
                    arriba,
                    x,
                    abajo
            );
        }

        // Líneas horizontales
        int cantidadLineasHorizontales = 8;

        for (int i = 0;
             i <= cantidadLineasHorizontales;
             i++) {

            int y =
                    arriba
                            + (abajo - arriba)
                            * i
                            / cantidadLineasHorizontales;

            grafico.drawLine(
                    izquierda,
                    y,
                    derecha,
                    y
            );
        }

        // ==========================================
        // EJE X = 0
        // ==========================================

        int posicionCeroY =
                convertirY(
                        0,
                        arriba,
                        abajo,
                        minimo,
                        maximo
                );

        grafico.setColor(Color.BLACK);

        grafico.setStroke(
                new BasicStroke(2)
        );

        if (posicionCeroY >= arriba &&
                posicionCeroY <= abajo) {

            grafico.drawLine(
                    izquierda,
                    posicionCeroY,
                    derecha,
                    posicionCeroY
            );
        }

        // ==========================================
        // EJE Y
        // ==========================================

        grafico.drawLine(
                izquierda,
                arriba,
                izquierda,
                abajo
        );

        // ==========================================
        // VALORES DEL EJE X
        // ==========================================

        grafico.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        12
                )
        );

        for (int i = 0;
             i <= cantidadLineasVerticales;
             i++) {

            double t =
                    tiempoInicial
                            + (tiempoFinal - tiempoInicial)
                            * i
                            / cantidadLineasVerticales;

            int x =
                    izquierda
                            + (derecha - izquierda)
                            * i
                            / cantidadLineasVerticales;

            grafico.drawString(
                    String.format("%.1f", t),
                    x - 10,
                    abajo + 20
            );
        }

        // ==========================================
        // VALORES DEL EJE Y
        // ==========================================

        for (int i = 0;
             i <= cantidadLineasHorizontales;
             i++) {

            double valor =
                    maximo
                            - (maximo - minimo)
                            * i
                            / cantidadLineasHorizontales;

            int y =
                    arriba
                            + (abajo - arriba)
                            * i
                            / cantidadLineasHorizontales;

            grafico.drawString(
                    String.format("%.2f", valor),
                    10,
                    y + 5
            );
        }

        // ==========================================
        // NOMBRE DE LOS EJES
        // ==========================================

        grafico.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        14
                )
        );

        grafico.drawString(
                "Tiempo (t)",
                derecha - 80,
                abajo + 45
        );

        grafico.drawString(
                "x(t)",
                25,
                arriba - 10
        );

        // ==========================================
        // DIBUJAR LA SEÑAL DIGITAL (muestras x[n])
        // ==========================================
        // Cada muestra se dibuja como un "stem": una línea vertical
        // desde el cero hasta el valor, con un punto en la punta.
        // Así se ve que la señal está formada por muestras discretas
        // y no por una curva continua.

        int radioMarcador = 3;

        for (int i = 0; i < cantidadPuntos; i++) {

            int x =
                    convertirX(
                            tiempos[i],
                            izquierda,
                            derecha
                    );

            int y =
                    convertirY(
                            valores[i],
                            arriba,
                            abajo,
                            minimo,
                            maximo
                    );

            grafico.setColor(new Color(30, 100, 220, 120));

            grafico.setStroke(
                    new BasicStroke(1.5f)
            );

            grafico.drawLine(
                    x,
                    posicionCeroY,
                    x,
                    y
            );

            grafico.setColor(Color.BLUE);

            grafico.fillOval(
                    x - radioMarcador,
                    y - radioMarcador,
                    radioMarcador * 2,
                    radioMarcador * 2
            );
        }

        // ==========================================
        // INFORMACIÓN
        // ==========================================

        grafico.setColor(Color.DARK_GRAY);

        grafico.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        13
                )
        );

        grafico.drawString(
                "Tiempo: "
                        + tiempoInicial
                        + " a "
                        + tiempoFinal,
                derecha - 180,
                35
        );

        grafico.drawString(
                "Valor máximo: "
                        + String.format("%.2f", maximo),
                derecha - 180,
                55
        );

        grafico.drawString(
                "Ts = "
                        + periodoMuestreo
                        + " ("
                        + cantidadPuntos
                        + " muestras)",
                derecha - 180,
                75
        );
    }

    // =================================================
    // CONVERTIR TIEMPO A POSICIÓN EN PANTALLA
    // =================================================

    private int convertirX(
            double t,
            int izquierda,
            int derecha) {

        double proporcion =
                (t - tiempoInicial)
                        / (tiempoFinal - tiempoInicial);

        return (int) (
                izquierda
                        + proporcion
                        * (derecha - izquierda)
        );
    }

    // =================================================
    // CONVERTIR VALOR A POSICIÓN EN PANTALLA
    // =================================================

    private int convertirY(
            double valor,
            int arriba,
            int abajo,
            double minimo,
            double maximo) {

        double proporcion =
                (valor - minimo)
                        / (maximo - minimo);

        return (int) (
                abajo
                        - proporcion
                        * (abajo - arriba)
        );
    }

    // =================================================
    // OBTENER FÓRMULA
    // =================================================

    private String obtenerFormula() {

        String nombre =
                funcion.getNombre();

        switch (nombre) {

            case "Escalón":
                return "x(t) = A · μ(t - t₀)";

            case "Seno":
                return "x(t) = A · sen(2πf₀t + θ)";

            case "Exponencial":
                return "x(t) = A · e^(α(t - t₀))";

            case "Señal Amortiguada":
                return "x(t) = A · sen(2πf₀t + θ) · e^(αt)";

            case "Pulso":
                return "p(t) = A · [μ(t - t₀) - μ(t - t₀ - D)]";

            default:
                return "";
        }
    }
}