package tp5.ejercicios;

import tp5.grafico.Curva;
import tp5.grafico.GraficoFuncion;
import tp5.grafico.GraficosComparativos;
import tp5.grafico.VentanaGrafico;
import tp5.modelos.ModelosExponenciales;
import tp5.reporte.Reporte;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

// EJERCICIO 1: Ley del Enfriamiento de Newton — dT/dt = -k·(T - Tamb)
// Solución: T(t) = Tamb + (T0 - Tamb)·e^(-k·t)
public class Ejercicio1 {

    public static void ejecutar() {

        Reporte.titulo("Ejercicio 1: Ley del Enfriamiento de Newton");

        // Parámetros del modelo (valores de ejemplo, el enunciado no los fija)
        double k = 0.05;    // constante de enfriamiento: cuanto mayor, más rápido se enfría el objeto
        double tAmb = 20;   // temperatura ambiente (°C): la temperatura a la que tiende el objeto
        double t0 = 90;     // temperatura inicial del objeto en t=0 (°C), ej. un café recién servido

        Reporte.linea("T(t) = Tamb + (T0 - Tamb)*e^(-k*t)   con k=" + k + ", Tamb=" + tAmb + ", T0=" + t0);
        // Vida media = ln(2)/k: tiempo que tarda la diferencia (T - Tamb) en reducirse a la mitad
        Reporte.valor("Vida media (ln2/k)", ModelosExponenciales.vidaMedia(k));
        // Temperatura del objeto a los 10 y a los 40 (mismas unidades de tiempo que k)
        Reporte.valor("T(10)", ModelosExponenciales.enfriamientoNewton(k, tAmb, t0, 10));
        Reporte.valor("T(40)", ModelosExponenciales.enfriamientoNewton(k, tAmb, t0, 40));
        System.out.println();

        // La curva a graficar: T en función de t (la función "t -> T(t)" se evalúa punto por punto)
        List<Curva> curvas = Arrays.asList(
                new Curva(t -> ModelosExponenciales.enfriamientoNewton(k, tAmb, t0, t), "T(t)", new Color(200, 60, 40))
        );

        // Argumentos: título, t mínimo (0), t máximo (80), curvas, etiqueta eje X, etiqueta eje Y
        GraficoFuncion grafico = new GraficoFuncion(
                "Ejercicio 1 - Enfriamiento de Newton", 0, 80, curvas, "t", "T"
        );

        // Guarda el PNG en graficos/Ejercicio1_Enfriamiento.png
        VentanaGrafico.guardar(grafico, "Ejercicio1_Enfriamiento");

        //se varía de a un parámetro y el resto queda fijo.

        // (a) k fijo (0.05), se varía la temperatura ambiente: la curva siempre decae con la misma
        //     "rapidez", pero tiende a un valor final distinto (justo Tamb).
        double[] tAmbientes = {0, 20, 40, 60};
        GraficosComparativos.graficarFamilia("Ejercicio 1 - k fijo, variando Tamb", 80, tAmbientes, "Tamb",
                (ta, t) -> ModelosExponenciales.enfriamientoNewton(k, ta, t0, t), "T",
                "Ejercicio1_VariandoTamb");

        // (b) Tamb fija (20), se varía k: todas terminan en la misma temperatura final (Tamb),
        //     pero con k grande llegan mucho antes (menor vida media).
        double[] constantesK = {0.02, 0.05, 0.1, 0.2};
        GraficosComparativos.graficarFamilia("Ejercicio 1 - Tamb fija, variando k", 80, constantesK, "k",
                (kk, t) -> ModelosExponenciales.enfriamientoNewton(kk, tAmb, t0, t), "T",
                "Ejercicio1_VariandoK");
        for (double kk : constantesK) {
            Reporte.valor("Vida media k=" + kk, ModelosExponenciales.vidaMedia(kk));
        }
        System.out.println();
    }

}
