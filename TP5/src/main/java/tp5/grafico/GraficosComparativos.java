package tp5.grafico;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleBinaryOperator;

// Gráficos comparativos (análisis de sensibilidad): una curva por cada valor de un parámetro.
public class GraficosComparativos {

    // Colores para distinguir las curvas de cada familia (una por valor del parámetro que se varía)
    private static final Color[] COLORES = {
            new Color(200, 60, 40), new Color(50, 130, 60), new Color(60, 90, 200), new Color(150, 90, 170)
    };

    // Arma y guarda un gráfico con una curva por cada valor de `valores`, variando un único
    // parámetro del modelo. `modelo` recibe (valor del parámetro, t) y devuelve la salida (T, P o N).
    public static void graficarFamilia(
            String titulo,
            double tMax,
            double[] valores,
            String nombreParametro,
            DoubleBinaryOperator modelo,
            String etiquetaY,
            String archivo) {

        List<Curva> curvas = new ArrayList<>();

        for (int i = 0; i < valores.length; i++) {
            double valor = valores[i];
            curvas.add(new Curva(
                    t -> modelo.applyAsDouble(valor, t),
                    nombreParametro + " = " + valor,
                    COLORES[i % COLORES.length]
            ));
        }

        GraficoFuncion grafico = new GraficoFuncion(titulo, 0, tMax, curvas, "t", etiquetaY);

        VentanaGrafico.guardar(grafico, archivo);
    }

}
