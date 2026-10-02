package tp5.ejercicios;

import tp5.diagrama.DiagramaBloques;
import tp5.grafico.VentanaGrafico;
import tp5.reporte.Reporte;

// EJERCICIO 4: diagrama en bloques de los 6 sistemas lineales (a) a (f)
public class Ejercicio4 {

    public static void ejecutar() {

        Reporte.titulo("Ejercicio 4: Diagramas en bloques");

        // Cada sistema tiene la forma: ẋ = a11·x + a12·y ,  ẏ = a21·x + a22·y
        // Cada fila es: {nombre, a11, a12, a21, a22, texto de la ecuación para el título}
        // Un coeficiente en 0 significa que ese lazo de realimentación no se dibuja.
        Object[][] sistemas = {
                {"a", 1.0, 2.0, -5.0, 2.0, "ẋ = x + 2y,  ẏ = -5x + 2y"},
                {"b", -2.0, 0.0, 0.0, -3.0, "ẋ = -2x,  ẏ = -3y"},
                {"c", -3.0, 2.0, -1.0, 0.0, "ẋ = -3x + 2y,  ẏ = -x"},
                {"d", 10.0, -18.0, 6.0, -11.0, "ẋ = 10x - 18y,  ẏ = 6x - 11y"},
                {"e", 0.0, -1.0, 1.0, -2.0, "ẋ = -y,  ẏ = x - 2y"},
                {"f", 0.0, 1.0, 0.0, -1.0, "ẋ = y,  ẏ = -y"},
        };

        for (Object[] s : sistemas) {

            String nombre = (String) s[0];   // letra del inciso: a, b, c, d, e o f
            double a11 = (double) s[1];      // coeficiente de x en ẋ (ganancia del lazo x -> sumador de x)
            double a12 = (double) s[2];      // coeficiente de y en ẋ (ganancia del lazo y -> sumador de x)
            double a21 = (double) s[3];      // coeficiente de x en ẏ (ganancia del lazo x -> sumador de y)
            double a22 = (double) s[4];      // coeficiente de y en ẏ (ganancia del lazo y -> sumador de y)
            String ecuacion = (String) s[5]; // texto que aparece como título del diagrama

            Reporte.linea("(" + nombre + ")  " + ecuacion);

            // Dibuja el diagrama (integradores 1/s, sumadores y cajas de ganancia) en un panel Swing
            DiagramaBloques diagrama = new DiagramaBloques(nombre, a11, a12, a21, a22, ecuacion);

            // Lo guarda como graficos/Ejercicio4_SistemaA.png ... SistemaF.png
            VentanaGrafico.guardar(diagrama, "Ejercicio4_Sistema" + nombre.toUpperCase());
        }

        System.out.println();
    }

}
