package tp4;

// import org.apache.commons.math3.linear.RealVector; // solo hace falta con la versión con librería (ver tp4.leontief.LeontiefApacheCommonsMath)
import tp4.grafico.GraficoBarras;
import tp4.grafico.VentanaGrafico;
import tp4.leontief.Leontief;
import tp4.reporte.Reporte;

import java.awt.Color;

// Con la versión sin librerías, Leontief.produccion()/aplicarInversa() devuelven double[]
// (usado abajo). Con la versión con Apache Commons Math (comentada), devuelven RealVector,
// y habría que reemplazar cada "double[] x" / "double[] deltaX" por "RealVector x" / "RealVector deltaX",
// y "x" / "deltaX" por "x.toArray()" / "deltaX.toArray()" al pasarlos a GraficoBarras.
public class Main {

    public static void main(String[] args) {

        System.out.println();
        System.out.println("       TP4 - MODELOS Y SIMULACIÓN");
        System.out.println("       Modelo insumo-producto de Leontief");
        System.out.println();

        ejercicio1();
        ejercicio2();
        ejercicio3();
        ejercicio4();

        System.out.println();
        System.out.println("========================================");
        System.out.println("Todos los vectores de producción fueron calculados.");
        System.out.println("========================================");
    }


    // EJERCICIO 1: x = (I - A)^-1 · y, 2 sectores (Agricultura, Industria)

    private static void ejercicio1() {

        System.out.println("=== Ejercicio 1: 2 sectores (Agricultura, Industria) ===");

        String[] sectores = {"Agricultura", "Industria"};

        double[][] a = {
                {0.2, 0.3},
                {0.1, 0.4}
        };

        double[] y = {200, 150};

        double[] x = Leontief.produccion(a, y);

        Reporte.imprimirVector("Vector de producción x", sectores, x);
        System.out.println();

        GraficoBarras grafico = new GraficoBarras(
                "Ejercicio 1 - Producción total x",
                sectores,
                x,
                "Producción",
                new Color(50, 110, 200)
        );

        VentanaGrafico.mostrar("Ejercicio 1 - Producción", grafico, "Ejercicio1_Produccion");
    }


    // EJERCICIO 2: efecto de Δy = +50 en Transporte, 3 sectores (Energía, Transporte, Manufactura)

    private static void ejercicio2() {

        System.out.println("=== Ejercicio 2: efecto de +50 en demanda de Transporte ===");
        System.out.println("(sectores: Energía, Transporte, Manufactura)");

        String[] sectores = {"Energía", "Transporte", "Manufactura"};

        double[][] inversa = {
                {1.25, 0.10, 0.05},
                {0.20, 1.40, 0.10},
                {0.15, 0.20, 1.30}
        };

        double[] deltaY = {0, 50, 0};

        double[] deltaX = Leontief.aplicarInversa(inversa, deltaY);

        Reporte.imprimirVector("Efecto Δx sobre cada sector", sectores, deltaX);
        System.out.println();

        GraficoBarras grafico = new GraficoBarras(
                "Ejercicio 2 - Efecto Δx (+50 en Transporte)",
                sectores,
                deltaX,
                "Δx",
                new Color(200, 90, 40)
        );

        VentanaGrafico.mostrar("Ejercicio 2 - Efecto en demanda", grafico, "Ejercicio2_EfectoDemanda");
    }


    // EJERCICIO 3: x = (I - A)^-1 · y, 4 sectores (Agricultura, Industria, Transporte, Servicios)

    private static void ejercicio3() {

        System.out.println("=== Ejercicio 3: 4 sectores (Agricultura, Industria, Transporte, Servicios) ===");

        String[] sectores = {"Agricultura", "Industria", "Transporte", "Servicios"};

        double[][] a = {
                {0.1, 0.2, 0.1, 0.0},
                {0.1, 0.3, 0.2, 0.1},
                {0.0, 0.2, 0.2, 0.1},
                {0.1, 0.1, 0.1, 0.2}
        };

        double[] y = {100, 150, 80, 120};

        double[] x = Leontief.produccion(a, y);

        Reporte.imprimirVector("Vector de producción total x", sectores, x);
        System.out.println();

        GraficoBarras grafico = new GraficoBarras(
                "Ejercicio 3 - Producción total x",
                sectores,
                x,
                "Producción",
                new Color(50, 110, 200)
        );

        VentanaGrafico.mostrar("Ejercicio 3 - Producción", grafico, "Ejercicio3_Produccion");
    }


    // EJERCICIO 4: efecto de Δy = +100 en Energía, 5 sectores (Agricultura, Industria, Energía, Transporte, Servicios)

    private static void ejercicio4() {

        System.out.println("=== Ejercicio 4: efecto de +100 en demanda de Energía ===");
        System.out.println("(sectores: Agricultura, Industria, Energía, Transporte, Servicios)");

        String[] sectores = {"Agricultura", "Industria", "Energía", "Transporte", "Servicios"};

        double[][] inversa = {
                {1.20, 0.10, 0.05, 0.00, 0.02},
                {0.15, 1.30, 0.10, 0.05, 0.00},
                {0.05, 0.10, 1.25, 0.10, 0.05},
                {0.10, 0.15, 0.10, 1.40, 0.10},
                {0.05, 0.05, 0.05, 0.10, 1.20}
        };

        double[] deltaY = {0, 0, 100, 0, 0};

        double[] deltaX = Leontief.aplicarInversa(inversa, deltaY);

        Reporte.imprimirVector("Efecto Δx sobre cada sector", sectores, deltaX);
        System.out.println();

        GraficoBarras grafico = new GraficoBarras(
                "Ejercicio 4 - Efecto Δx (+100 en Energía)",
                sectores,
                deltaX,
                "Δx",
                new Color(200, 90, 40)
        );

        VentanaGrafico.mostrar("Ejercicio 4 - Efecto en demanda", grafico, "Ejercicio4_EfectoDemanda");
    }
}
