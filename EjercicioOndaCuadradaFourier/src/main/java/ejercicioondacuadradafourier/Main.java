package ejercicioondacuadradafourier;

import ejercicioondacuadradafourier.ejercicios.EjercicioOndaCuadradaFourier;

public class Main {
    public static void main(String[] args) {
        System.out.println("Ejemplo onda cuadrada fourier");
        int terminos = args.length > 0 ? Integer.parseInt(args[0]) : 15;
        EjercicioOndaCuadradaFourier.ejecutar(terminos);
        System.out.println("Ejecucion terminada");
    }
}
