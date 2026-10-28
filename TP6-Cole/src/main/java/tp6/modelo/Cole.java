package tp6.modelo;

import java.io.*;
import java.util.*;

// el modelo de Cole y el ajuste de curvas (lo usan la consola y la ventana).
public class Cole {

    // Tabla 1 : {R0 [ohm], Rinf [ohm], fc [Hz], alpha}
    public static final Map<String, double[]> TEJIDOS = new LinkedHashMap<>();
    static {
        TEJIDOS.put("Músculo Long.", new double[]{385, 70, 37e3, 0.27});
        TEJIDOS.put("Músculo Trans.", new double[]{730, 75, 27e3, 0.24});
        TEJIDOS.put("Hígado", new double[]{540, 100, 72e3, 0.45});
        TEJIDOS.put("Pulmón", new double[]{440, 70, 100e3, 0.59});
        TEJIDOS.put("Bazo", new double[]{550, 60, 373e3, 0.36});
    }

    // Registros de la base de Dell'Osa et al. (2025) usados en el ejercicio 3
    public static final Map<String, String> REGISTROS = new LinkedHashMap<>();
    static {
        REGISTROS.put("Humano A", "/datos/humano_A.csv");
        REGISTROS.put("Humano B", "/datos/humano_B.csv");
        REGISTROS.put("Agar A", "/datos/agar_A.csv");
        REGISTROS.put("Agar B", "/datos/agar_B.csv");
    }

    /*
        Z(w) = Rinf + (R0 - Rinf) / (1 + (j*w*tau)^alpha).
       Devuelve {parte real, parte imaginaria}.
       Usamos (j*w*tau)^alpha = (w*tau)^alpha * (cos(alpha*pi/2) + j*sin(alpha*pi/2)).
     */
    public static double[] modelo(double frecuencia, double resistenciaCero, double resistenciaInfinito,
                                  double frecuenciaCaracteristica, double alfa) {
        double frecuenciaAngular = 2 * Math.PI * frecuencia;
        double tau = 1 / (2 * Math.PI * frecuenciaCaracteristica);
        double moduloPotencia = Math.pow(frecuenciaAngular * tau, alfa);
        double anguloPotencia = alfa * Math.PI / 2;
        // denominador = 1 + moduloPotencia*(cos + j sin)
        double denominadorReal = 1 + moduloPotencia * Math.cos(anguloPotencia);
        double denominadorImaginario = moduloPotencia * Math.sin(anguloPotencia);
        // (R0-Rinf) / (dr + j di) = (R0-Rinf)*(dr - j di) / (dr^2 + di^2)
        double factor = (resistenciaCero - resistenciaInfinito)
                / (denominadorReal * denominadorReal + denominadorImaginario * denominadorImaginario);
        return new double[]{resistenciaInfinito + factor * denominadorReal, -factor * denominadorImaginario};
    }

    public static double modulo(double[] impedancia) {
        return Math.hypot(impedancia[0], impedancia[1]);
    }

    /**
     * Ajusta Cole a datos medidos (método de Ayllon): en el plano complejo (Re, Im) los datos
     * forman un arco de circunferencia. Se ajusta esa circunferencia por mínimos cuadrados y de
     * su centro y radio salen R0, Rinf y alpha. Devuelve {R0, Rinf, fc, alpha}.
     */
    public static double[] ajustar(double[] frecuencias, double[] partesReales, double[] partesImaginarias) {
        int cantidadPuntos = frecuencias.length;

        // 1) Circunferencia (x-x0)^2 + (y-y0)^2 = r^2. Reordenada:
        //    x^2 + y^2 = 2*x0*x + 2*y0*y + c  -> sistema lineal 3x3 (ecuaciones normales)
        double[][] sistema = new double[3][4];
        for (int punto = 0; punto < cantidadPuntos; punto++) {
            double[] fila = {2 * partesReales[punto], 2 * partesImaginarias[punto], 1};
            double terminoIndependiente = partesReales[punto] * partesReales[punto]
                    + partesImaginarias[punto] * partesImaginarias[punto];
            for (int filaSistema = 0; filaSistema < 3; filaSistema++) {
                for (int columna = 0; columna < 3; columna++)
                    sistema[filaSistema][columna] += fila[filaSistema] * fila[columna];
                sistema[filaSistema][3] += fila[filaSistema] * terminoIndependiente;
            }
        }
        double[] solucion = resolver(sistema);
        double centroX = solucion[0], centroY = solucion[1];
        double radio = Math.sqrt(solucion[2] + centroX * centroX + centroY * centroY);

        // 2) La circunferencia corta el eje real en Rinf y R0
        double medioAncho = Math.sqrt(radio * radio - centroY * centroY);
        double resistenciaInfinito = centroX - medioAncho;
        double resistenciaCero = centroX + medioAncho;

        // 3) alpha sale de qué tan lejos del eje real está el centro
        double alfa = 1 - (2 / Math.PI) * Math.atan(centroY / medioAncho);

        // 4) fc es la frecuencia donde la parte imaginaria es mínima (tope del arco)
        int posicionMinimo = 0;
        for (int punto = 1; punto < cantidadPuntos; punto++)
            if (partesImaginarias[punto] < partesImaginarias[posicionMinimo]) posicionMinimo = punto;
        return new double[]{resistenciaCero, resistenciaInfinito, frecuencias[posicionMinimo], alfa};
    }

    // Resuelve un sistema 3x3 (matriz aumentada) por eliminación de Gauss.
    private static double[] resolver(double[][] sistema) {
        for (int pivote = 0; pivote < 3; pivote++) {
            int filaMayor = pivote;
            for (int fila = pivote + 1; fila < 3; fila++)
                if (Math.abs(sistema[fila][pivote]) > Math.abs(sistema[filaMayor][pivote])) filaMayor = fila;
            double[] filaIntercambio = sistema[pivote]; sistema[pivote] = sistema[filaMayor]; sistema[filaMayor] = filaIntercambio;
            for (int fila = pivote + 1; fila < 3; fila++) {
                double factor = sistema[fila][pivote] / sistema[pivote][pivote];
                for (int columna = pivote; columna < 4; columna++)
                    sistema[fila][columna] -= factor * sistema[pivote][columna];
            }
        }
        double[] incognitas = new double[3];
        for (int fila = 2; fila >= 0; fila--) {
            double suma = sistema[fila][3];
            for (int columna = fila + 1; columna < 3; columna++) suma -= sistema[fila][columna] * incognitas[columna];
            incognitas[fila] = suma / sistema[fila][fila];
        }
        return incognitas;
    }

    // Lee un CSV (desde los recursos del proyecto) con columnas f [Hz], Re(Z), Im(Z). Devuelve {f[], re[], im[]}.
    public static double[][] leerCsv(String ruta) throws IOException {
        List<double[]> filas = new ArrayList<>();
        try (BufferedReader lector = new BufferedReader(new InputStreamReader(Cole.class.getResourceAsStream(ruta)))) {
            lector.readLine(); // encabezado
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.isBlank()) continue;
                String[] columnas = linea.split(",");
                filas.add(new double[]{Double.parseDouble(columnas[0]), Double.parseDouble(columnas[1]), Double.parseDouble(columnas[2])});
            }
        }
        double[][] datos = new double[3][filas.size()];
        for (int fila = 0; fila < filas.size(); fila++)
            for (int columna = 0; columna < 3; columna++) datos[columna][fila] = filas.get(fila)[columna];
        return datos;
    }

    // n frecuencias logarítmicas entre fmin y fmax.
    public static double[] frecuencias(double frecuenciaMinima, double frecuenciaMaxima, int cantidad) {
        double[] frecuencias = new double[cantidad];
        for (int i = 0; i < cantidad; i++)
            frecuencias[i] = frecuenciaMinima * Math.pow(frecuenciaMaxima / frecuenciaMinima, i / (double) (cantidad - 1));
        return frecuencias;
    }
}
