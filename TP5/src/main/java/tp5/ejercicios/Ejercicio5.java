package tp5.ejercicios;

import tp5.edo.LotkaVolterra;
import tp5.edo.RungeKutta4;
import tp5.grafico.Curva;
import tp5.grafico.GraficoFase;
import tp5.grafico.GraficoFuncion;
import tp5.grafico.VentanaGrafico;
import tp5.reporte.Reporte;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

// EJERCICIO 5: Modelo Depredador-Presa (Lotka-Volterra) — sin solución cerrada:
// se clasifican los puntos de equilibrio y se integra numéricamente (Runge-Kutta 4).
//   dx/dt = α·x - β·x·y   (x = presas)
//   dy/dt = δ·x·y - γ·y   (y = depredadores)
public class Ejercicio5 {

    public static void ejecutar() {

        Reporte.titulo("Ejercicio 5: Depredador-Presa (Lotka-Volterra)");

        // Parámetros del modelo (valores clásicos de ejemplo)
        double alfa = 1.0;    // α: tasa de crecimiento natural de las presas (sin depredadores)
        double beta = 0.1;    // β: tasa de depredación (cuánto reducen las presas los encuentros con depredadores)
        double delta = 0.075; // δ: eficiencia con que los depredadores convierten presas comidas en nuevos depredadores
        double gama = 1.5;    // γ: tasa de mortalidad de los depredadores (sin presas se extinguen)

        LotkaVolterra sistema = new LotkaVolterra(alfa, beta, delta, gama);

        Reporte.linea("dx/dt = α·x - β·x·y,  dy/dt = δ·x·y - γ·y   (α=" + alfa + ", β=" + beta + ", δ=" + delta + ", γ=" + gama + ")");
        Reporte.linea("Puntos de equilibrio: (0,0) y (x*,y*) = (γ/δ, α/β)");
        // Equilibrio de coexistencia: población de presas y depredadores a la que ambas quedarían constantes
        Reporte.valor("x* = γ/δ", sistema.equilibrioX());
        Reporte.valor("y* = α/β", sistema.equilibrioY());
        // Clasificación según los autovalores de la matriz jacobiana en cada equilibrio (ver informe)
        Reporte.linea("Clasificación: (0,0) es punto silla (autovalores α=" + alfa + " y -γ=" + (-gama) + ", signos opuestos).");
        Reporte.linea("(x*,y*) es un centro (autovalores imaginarios puros ±i·sqrt(α·γ) = ±i·" + String.format("%.4f", Math.sqrt(alfa * gama)) + ").");

        // Configuración de la simulación numérica (Runge-Kutta de orden 4)
        double t0 = 0;                          // tiempo inicial
        double[] estadoInicial = {10, 5};       // condición inicial: x0 = 10 presas, y0 = 5 depredadores
        double h = 0.01;                        // paso de integración (cuanto menor, más preciso)
        int pasos = 6000;                       // cantidad de pasos: tiempo final = pasos * h = 60

        // Resultado: una fila por paso, con columnas [t, x(t), y(t)]
        double[][] trayectoria = RungeKutta4.resolver(sistema, t0, estadoInicial, h, pasos);

        // Estado final de la simulación (t = 60): última fila de la trayectoria
        Reporte.valor("x(60) [presas]", trayectoria[pasos][1]);
        Reporte.valor("y(60) [depredadores]", trayectoria[pasos][2]);
        System.out.println();

        // Gráfico 1 — Poblaciones vs. tiempo: dos curvas, x(t) (columna 1) e y(t) (columna 2)
        List<Curva> curvasTiempo = Arrays.asList(
                new Curva(t -> interpolar(trayectoria, t, 1), "x(t) - presas", new Color(50, 130, 60)),
                new Curva(t -> interpolar(trayectoria, t, 2), "y(t) - depredadores", new Color(200, 60, 40))
        );

        // Eje t de 0 a 60 (pasos * h)
        GraficoFuncion graficoTiempo = new GraficoFuncion(
                "Ejercicio 5 - Poblaciones vs. tiempo", 0, pasos * h, curvasTiempo, "t", "población"
        );

        VentanaGrafico.guardar(graficoTiempo, "Ejercicio5_Poblaciones");

        // Gráfico 2 — Retrato de fase: la órbita (x(t), y(t)) es una curva cerrada, no una función de x,
        // así que se grafica como trayectoria paramétrica (ver GraficoFase).
        double[] xs = new double[trayectoria.length];   // todas las poblaciones de presas, en orden temporal
        double[] ys = new double[trayectoria.length];   // todas las poblaciones de depredadores, en orden temporal
        for (int i = 0; i < trayectoria.length; i++) {
            xs[i] = trayectoria[i][1];
            ys[i] = trayectoria[i][2];
        }

        // El último argumento es el punto de equilibrio (x*, y*), que se marca en rojo en el gráfico
        GraficoFase graficoFase = new GraficoFase(
                "Ejercicio 5 - Retrato de fase",
                xs, ys,
                "x (presas)", "y (depredadores)",
                new double[]{sistema.equilibrioX(), sistema.equilibrioY()}
        );

        VentanaGrafico.guardar(graficoFase, "Ejercicio5_RetratoFase");
    }

    // La simulación solo calcula puntos discretos (uno cada h); para graficar una curva continua
    // se interpola linealmente entre dos puntos vecinos de la trayectoria.
    // Parámetros: trayectoria = matriz [t, x, y] de la simulación, t = instante a evaluar,
    // col = qué variable devolver (1 = x presas, 2 = y depredadores).
    private static double interpolar(double[][] trayectoria, double t, int col) {

        int n = trayectoria.length;
        double t0 = trayectoria[0][0];
        double h = trayectoria[1][0] - t0;   // paso de la simulación (separación entre filas)

        // Índice del punto de la simulación inmediatamente anterior a t (acotado a los extremos)
        int i = (int) ((t - t0) / h);
        if (i < 0) i = 0;
        if (i >= n - 1) i = n - 2;

        double tA = trayectoria[i][0];
        double frac = (t - tA) / h;          // qué fracción del tramo [i, i+1] ya recorrió t (0 a 1)

        // Valor = punto anterior + fracción * (diferencia con el punto siguiente)
        return trayectoria[i][col] + frac * (trayectoria[i + 1][col] - trayectoria[i][col]);
    }

}
