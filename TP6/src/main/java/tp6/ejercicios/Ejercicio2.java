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
  EJERCICIO 2: Modelo de Cole para los 5 tejidos de la Tabla 1, todos en el mismo espectro de frecuencias.

  Se grafican tres vistas de la misma información:
    - módulo |Z| en función de la frecuencia
    - fase de Z (en grados) en función de la frecuencia
    - diagrama de Nyquist: -Im(Z) contra Re(Z); el modelo de Cole da un arco de circunferencia
 */
public class Ejercicio2 {

    // Tabla 1 del enunciado: un valor por tejido, en el mismo orden en las 5 listas
    public static final String[] NOMBRES = {"Músculo long.", "Músculo trans.", "Hígado", "Pulmón", "Bazo"};
    public static final double[] R0 = {385, 730, 540, 440, 550};     // [Ω]
    public static final double[] R_INF = {70, 75, 100, 70, 60};      // [Ω]
    public static final double[] FC = {37e3, 27e3, 72e3, 100e3, 373e3};   // [Hz]
    public static final double[] ALFA = {0.27, 0.24, 0.45, 0.59, 0.36};

    // Arma la lista de los 5 tejidos con sus modelos (la usan la consola y la interfaz)
    public static List<Tejido> tabla1() {
        List<Tejido> tejidos = new ArrayList<Tejido>();
        for (int t = 0; t < NOMBRES.length; t++) {
            tejidos.add(new Tejido(NOMBRES[t], new ModeloCole(R0[t], R_INF[t], FC[t], ALFA[t])));
        }
        return tejidos;
    }

    public static void ejecutar() {

        /*
          PASO A PASO de este ejercicio:
            1) Se arman los 5 modelos con los parámetros de la Tabla 1.
            2) Para cada uno se calcula la impedancia justo en su fc. Ahí se cumple que la parte real vale
               (R0 + Rinf)/2 y que la parte imaginaria es la más grande: (R0 - Rinf)/2 · tan(α·π/4).
            3) Se calcula Z de los 5 en las mismas frecuencias (10 Hz a 1 GHz) y se grafican juntos.
         */
        System.out.println("=== Ejercicio 2: Modelo de Cole para 5 tejidos ===");

        // Paso 1
        List<Tejido> tejidos = tabla1();

        // Paso 2: por cada tejido, imprimo sus parámetros y la impedancia justo en su fc
        for (int t = 0; t < tejidos.size(); t++) {
            Complex enFc = tejidos.get(t).getModelo().impedancia(FC[t]);
            System.out.printf("  %-15s R0=%5.0f  Rinf=%4.0f  fc=%6.0f Hz  α=%.2f  |  Z(fc) = %.2f %s j·%.2f Ω%n",
                    NOMBRES[t], R0[t], R_INF[t], FC[t], ALFA[t],
                    enFc.getReal(), enFc.getImaginary() < 0 ? "-" : "+", Math.abs(enFc.getImaginary()));
        }

        // Paso 3: espectro común para los 5: de 10 Hz a 1 GHz (si cada uno usara un rango distinto no se podrían comparar)
        JFreeChart[] g = ConstructorGraficos.espectro("Ejercicio 2", tejidos, 10, 1e9);
        Graficos.guardar(g[0], "Ejercicio2_Modulo");
        Graficos.guardar(g[1], "Ejercicio2_Fase");
        Graficos.guardar(g[2], "Ejercicio2_Nyquist");
        System.out.println();
    }
}
