package tp6.ejercicios;

import org.apache.commons.math3.complex.Complex;
import org.jfree.chart.JFreeChart;
import tp6.grafico.ConstructorGraficos;
import tp6.grafico.Graficos;
import tp6.modelo.ModeloCole;
import tp6.modelo.Tejido;

import java.util.ArrayList;
import java.util.List;

/*
  EJERCICIO 1: Implementación del Modelo de Cole (clase tp6.modelo.ModeloCole).

  Se comprueba que el modelo se comporta como dice la teoría, usando el músculo longitudinal de la Tabla 1:
    - a frecuencia muy baja, Z tiende a R0
    - a frecuencia muy alta, Z tiende a Rinf
    - en f = fc, la parte real de Z queda justo a mitad de camino entre R0 y Rinf: (385 + 70) / 2 = 227.5 Ω
   (con α chico, como acá, el modelo tarda en llegar a los extremos: hay que ir a frecuencias muy extremas)

  Después se hace el análisis de sensibilidad: se cambia UN parámetro por vez (el resto queda fijo en el valor del
  músculo longitudinal) para ver qué controla cada uno.
 */
public class Ejercicio1 {

    // caso base: los valores del músculo longitudinal de la Tabla 1 (R0, Rinf, fc, α)
    private static final double R0 = 385, R_INF = 70, FC = 37e3, ALFA = 0.27;

    public static void ejecutar() {

        System.out.println("=== Ejercicio 1: Modelo de Cole (comprobación con el músculo longitudinal) ===");

        // armamos el modelo con los valores del caso base
        ModeloCole musculo = new ModeloCole(R0, R_INF, FC, ALFA);

        /*
          Chequeo del modelo con tres frecuencias:
            - muy baja: Z tiene que acercarse a R0 (385)
            - justo en fc: la parte real da el punto medio entre R0 y Rinf, o sea (385+70)/2 = 227.5
            - muy alta: Z tiene que acercarse a Rinf (70)
          Se usan frecuencias extremas (1 µHz y 10^15 Hz) porque con α chico el modelo tarda muchísimo en llegar a los extremos.
         */
        mostrar("f = 1 µHz (muy baja)", musculo.impedancia(1e-6));
        mostrar("f = fc = 37 kHz", musculo.impedancia(FC));
        mostrar("f = 10^15 Hz (muy alta)", musculo.impedancia(1e15));

        System.out.println("  Esperado: Z(baja) ≈ R0 = 385 Ω y Z(alta) ≈ Rinf = 70 Ω");
        System.out.println();

        System.out.println("=== Ejercicio 1: sensibilidad (un parámetro por vez) ===");
        // un parámetro por vez: el resto se queda en el valor base
        variar("R0", "Ω", new double[]{300, 385, 500, 700}, 0);
        variar("Rinf", "Ω", new double[]{30, 70, 120, 200}, 1);
        variar("fc", "Hz", new double[]{5e3, 37e3, 200e3, 1e6}, 2);
        variar("alfa", "", new double[]{0.15, 0.27, 0.6, 1.0}, 3);
        System.out.println();
    }

    // Grafica el modelo cambiando UN parámetro ("cual": 0=R0, 1=Rinf, 2=fc, 3=α) por cada valor de la lista.
    // Así se ve qué hace cada uno. Guarda 3 imágenes: módulo, fase y Nyquist.
    private static void variar(String parametro, String unidad, double[] valores, int cual) {

        List<Tejido> tejidos = new ArrayList<Tejido>();
        for (double v : valores) {
            double[] p = {R0, R_INF, FC, ALFA};   // arranco con los valores base...

            p[cual] = v;                          // ...y piso solo el que estoy variando
            tejidos.add(new Tejido(parametro + " = " + texto(v) + " " + unidad, new ModeloCole(p[0], p[1], p[2], p[3])));
        }

        // todas las curvas con el mismo rango de frecuencias: 10 Hz a 1 GHz
        JFreeChart[] g = ConstructorGraficos.espectro("Ejercicio 1, variando " + parametro, tejidos, 10, 1e9);
        Graficos.guardar(g[0], "Ejercicio1_Var_" + parametro + "_Modulo");
        Graficos.guardar(g[1], "Ejercicio1_Var_" + parametro + "_Fase");
        Graficos.guardar(g[2], "Ejercicio1_Var_" + parametro + "_Nyquist");
    }

    // Para la leyenda: 37000.0 se muestra como "37000" y 0.27 como "0.27"
    private static String texto(double v) {
        return v == Math.rint(v) ? String.valueOf((long) v) : String.valueOf(v);
    }

    // Imprime Z como "parte real - j·parte imaginaria" más su módulo
    private static void mostrar(String etiqueta, Complex z) {
        System.out.printf("  %-24s Z = %8.3f %s j·%.3f Ω   |Z| = %.3f Ω%n",
                etiqueta, z.getReal(), z.getImaginary() < 0 ? "-" : "+", Math.abs(z.getImaginary()), z.abs());
    }
}
