package tp6.ejercicios;

import org.jfree.chart.JFreeChart;
import tp6.datos.Medicion;
import tp6.datos.Registros;
import tp6.grafico.ConstructorGraficos;
import tp6.grafico.Graficos;
import tp6.modelo.AjusteCole;
import org.apache.commons.math3.complex.Complex;
import tp6.modelo.ModeloCole;

import java.io.IOException;

/*
  EJERCICIO 3: Ajuste del Modelo de Cole a mediciones reales de la base de datos de Dell'Osa et al. (Zenodo).

  Se eligieron 4 registros (ver datos/README.md): 2 de tejido humano y 2 de un fantoma de agar-agar.
  Para cada uno, el ajuste por mínimos cuadrados no lineales (Levenberg-Marquardt de Commons Math, clase
  tp6.modelo.AjusteCole) devuelve R0, Rinf, fc y α, y se grafica la medición junto con la curva ajustada.
 */
public class Ejercicio3 {

    // Ajusta los 4 registros uno por uno, imprime los parámetros y guarda los gráficos
    public static void ejecutar() {

        /*
          PASO A PASO de este ejercicio (se repite para cada uno de los 4 registros):
            1) Se lee la medición del CSV: frecuencia, parte real y parte imaginaria de Z en cada punto.
            2) Se ajusta el modelo: se buscan R0, Rinf, fc y α que hacen mínima la diferencia entre
               el modelo y la medición (mínimos cuadrados, Levenberg-Marquardt).
            3) Se imprime la fila con los 4 parámetros y el error medio del ajuste [Ω].
            4) Se grafica la medición contra el modelo ajustado.
          Al final se valida el método con datos sintéticos (ver validarConDatosSinteticos).
         */
        System.out.println("=== Ejercicio 3: Ajuste del Modelo de Cole a datos reales ===");
        System.out.printf("  %-36s %8s %8s %11s %6s %9s%n", "registro", "R0[Ω]", "Rinf[Ω]", "fc[Hz]", "α", "error[Ω]");

        for (int k = 0; k < Registros.ARCHIVOS.length; k++) {
            try {
                // Paso 1: leo el CSV
                Medicion med = Registros.leer(k);
                // Paso 2: busco R0, Rinf, fc y α que mejor calzan con la medición
                AjusteCole ajuste = AjusteCole.ajustar(med.getF(), med.getRe(), med.getIm());
                ModeloCole m = ajuste.getModelo();

                // Paso 3
                System.out.printf("  %-36s %8.2f %8.2f %11.1f %6.3f %9.3f%n",
                        med.getNombre(), m.getR0(), m.getRInf(), m.getFc(), m.getAlfa(), ajuste.getRmse());

                // Paso 4: gráficos de medición (puntos) contra ajuste (línea); guardo Nyquist y módulo
                JFreeChart[] g = ConstructorGraficos.ajuste(med.getNombre(), med.getF(), med.getRe(), med.getIm(), m);
                Graficos.guardar(g[0], "Ejercicio3_" + Registros.ARCHIVOS[k] + "_Nyquist");
                Graficos.guardar(g[1], "Ejercicio3_" + Registros.ARCHIVOS[k] + "_Modulo");

            } catch (IOException e) {
                System.out.println("  No se pudo leer " + Registros.ARCHIVOS[k] + ": " + e.getMessage());
            }
        }
        System.out.println();

        validarConDatosSinteticos();
    }

    /*
      Validación del ajuste: se generan mediciones "falsas" con el modelo de Cole usando los parámetros de la
      Tabla 1 (100 frecuencias entre 10 Hz y 1 GHz), se les agrega ruido gaussiano del 1 % del módulo de Z y se
      comprueba que el ajuste recupere los parámetros originales.

      Paso a paso, para cada tejido:
        1) Con los parámetros reales se calcula Z exacta en 100 frecuencias.
        2) A la parte real y a la imaginaria se les suma ruido: 1 % de |Z| por un número al azar de distribución normal.
        3) Se ajusta el modelo a esos datos ruidosos, como si fueran una medición.
        4) Se comparan los parámetros recuperados con los reales: si están cerca, el método de ajuste funciona.
     */
    private static void validarConDatosSinteticos() {

        System.out.println("=== Validación del ajuste con datos sintéticos (ruido 1 %) ===");
        System.out.printf("  %-15s %-28s %-28s%n", "tejido", "real (R0, Rinf, fc, α)", "ajustado (R0, Rinf, fc, α)");

        java.util.Random azar = new java.util.Random(42);   // semilla fija: así el ruido es siempre el mismo y el resultado se repite
        java.util.List<tp6.modelo.Tejido> tejidos = Ejercicio2.tabla1();

        for (tp6.modelo.Tejido t : tejidos) {
            ModeloCole real = t.getModelo();
            // Pasos 1 y 2: 100 frecuencias entre 10 Hz y 1 GHz (equiespaciadas en logaritmo), con la impedancia exacta del modelo + ruido
            int n = 100;
            double[] f = new double[n], re = new double[n], im = new double[n];
            for (int i = 0; i < n; i++) {
                f[i] = 10 * Math.pow(1e8, i / (n - 1.0));      // de 10 Hz (i = 0) a 10 · 1e8 = 1 GHz (i = n-1)
                Complex z = real.impedancia(f[i]);
                double ruido = 0.01 * z.abs();                 // tamaño del ruido: 1 % del módulo de Z
                re[i] = z.getReal() + ruido * azar.nextGaussian();
                im[i] = z.getImaginary() + ruido * azar.nextGaussian();
            }
            // Paso 3: hago de cuenta que no conozco los parámetros y trato de recuperarlos desde los datos ruidosos
            ModeloCole aj = AjusteCole.ajustar(f, re, im).getModelo();
            // Paso 4: imprimo real y ajustado lado a lado
            System.out.printf("  %-15s %-28s %-28s%n", t.getNombre(),
                    String.format("%.0f, %.0f, %.0f, %.2f", real.getR0(), real.getRInf(), real.getFc(), real.getAlfa()),
                    String.format("%.1f, %.1f, %.0f, %.3f", aj.getR0(), aj.getRInf(), aj.getFc(), aj.getAlfa()));
        }
        System.out.println();
    }
}
