package tp2.grafico;

import tp2.funciones.Funcion;

import javax.swing.*;
import java.awt.*;

public class Grafico extends JPanel {

    private Funcion funcion;

    private double tiempoInicial;
    private double tiempoFinal;

    public Grafico(
            Funcion funcion,
            double tiempoInicial,
            double tiempoFinal) {

        this.funcion = funcion;
        this.tiempoInicial = tiempoInicial;
        this.tiempoFinal = tiempoFinal;

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
        // CALCULAR LOS VALORES
        // ==========================================

        int cantidadPuntos = anchoGrafico;

        double[] tiempos =
                new double[cantidadPuntos];

        double[] valores =
                new double[cantidadPuntos];

        double maximo = 0;

        double minimo = 0;

        for (int i = 0; i < cantidadPuntos; i++) {

            double t =
                    tiempoInicial
                            + (tiempoFinal - tiempoInicial)
                            * i
                            / (cantidadPuntos - 1);

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
        // DIBUJAR LA FUNCIÓN
        // ==========================================

        grafico.setColor(Color.BLUE);

        grafico.setStroke(
                new BasicStroke(
                        2.5f
                )
        );

        for (int i = 1;
             i < cantidadPuntos;
             i++) {

            int x1 =
                    izquierda
                            + i - 1;

            int x2 =
                    izquierda
                            + i;

            int y1 =
                    convertirY(
                            valores[i - 1],
                            arriba,
                            abajo,
                            minimo,
                            maximo
                    );

            int y2 =
                    convertirY(
                            valores[i],
                            arriba,
                            abajo,
                            minimo,
                            maximo
                    );

            grafico.drawLine(
                    x1,
                    y1,
                    x2,
                    y2
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