package tp2;

import tp2.funciones.*;
import tp2.grafico.VentanaGrafico;

public class Main {

    public static void main(String[] args) {

        System.out.println();
        System.out.println("       TP2 - MODELOS Y SIMULACIÓN");

        System.out.println();
        System.out.println("Iniciando programa...");

        // ESCALÓN
        System.out.println("Graficando Escalon");
        Escalon escalon =
                new Escalon(
                        5,
                        2
                );

        VentanaGrafico.mostrar(escalon, 0.2);

        // SENO
        System.out.println("Graficando Seno");
        Seno seno =
                new Seno(
                        5,
                        1,
                        0
                );

        VentanaGrafico.mostrar(seno, 0.05);

        // EXPONENCIAL+
        System.out.println("Graficando Exponencial");
        Exponencial exponencial =
                new Exponencial(
                        1,
                        -0.5,
                        0
                );

        VentanaGrafico.mostrar(exponencial, 0.2);


        // SEÑAL AMORTIGUADA
        System.out.println("Graficando Amortiguada");
        SenalAmortiguada amortiguada =
                new SenalAmortiguada(
                        5,
                        1,
                        0,
                        -0.3
                );

        VentanaGrafico.mostrar(amortiguada, 0.05);



        // PULSO
        System.out.println("Graficando Pulso");
        Pulso pulso =
                new Pulso(
                        5,
                        2,
                        3
                );

        VentanaGrafico.mostrar(pulso, 0.1);



        // FIN
        System.out.println();
        System.out.println("========================================");
        System.out.println("Todas las funciones fueron generadas.");
        System.out.println("Se abrió un gráfico para cada función.");
        System.out.println("========================================");
    }
}