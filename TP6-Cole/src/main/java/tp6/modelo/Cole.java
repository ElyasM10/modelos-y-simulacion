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
        // Paso 1: frecuencia angular w = 2*pi*f (en rad/s)
        double frecuenciaAngular = 2 * Math.PI * frecuencia;
        // Paso 2: constante de tiempo tau = 1 / (2*pi*fc)
        double tau = 1 / (2 * Math.PI * frecuenciaCaracteristica);
        // Paso 3: (j*w*tau)^alfa en forma polar. Como j = e^(j*pi/2), queda j^alfa = cos(alfa*pi/2) + j*sin(alfa*pi/2).
        // moduloPotencia es el módulo (w*tau)^alfa y anguloPotencia es el ángulo alfa*pi/2.
        double moduloPotencia = Math.pow(frecuenciaAngular * tau, alfa);
        double anguloPotencia = alfa * Math.PI / 2;
        // Paso 4: denominador = 1 + moduloPotencia*(cos + j sin), separado en parte real e imaginaria
        double denominadorReal = 1 + moduloPotencia * Math.cos(anguloPotencia);
        double denominadorImaginario = moduloPotencia * Math.sin(anguloPotencia);
        // Paso 5: división compleja. Se multiplica arriba y abajo por el conjugado del denominador:
        // (R0-Rinf) / (dr + j di) = (R0-Rinf)*(dr - j di) / (dr^2 + di^2)
        double factor = (resistenciaCero - resistenciaInfinito)
                / (denominadorReal * denominadorReal + denominadorImaginario * denominadorImaginario);
        // Paso 6: se suma Rinf a la parte real. La parte imaginaria queda con signo negativo (la bioimpedancia es capacitiva).
        return new double[]{resistenciaInfinito + factor * denominadorReal, -factor * denominadorImaginario};
    }

    // Módulo de la impedancia: raíz de (parte real al cuadrado + parte imaginaria al cuadrado).
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
        // sistema es la matriz aumentada del sistema: 3 filas (una por incógnita) y 4 columnas (3 coeficientes + el término independiente)
        double[][] sistema = new double[3][4];
        for (int punto = 0; punto < cantidadPuntos; punto++) {
            // Cada punto medido aporta una ecuación: [2x, 2y, 1] * [x0, y0, c] = x^2 + y^2
            double[] fila = {2 * partesReales[punto], 2 * partesImaginarias[punto], 1};
            double terminoIndependiente = partesReales[punto] * partesReales[punto]
                    + partesImaginarias[punto] * partesImaginarias[punto];
            // Ecuaciones normales de mínimos cuadrados: se suma (fila^T * fila) y (fila^T * termino) de todos los puntos
            for (int filaSistema = 0; filaSistema < 3; filaSistema++) {
                for (int columna = 0; columna < 3; columna++)
                    sistema[filaSistema][columna] += fila[filaSistema] * fila[columna];
                sistema[filaSistema][3] += fila[filaSistema] * terminoIndependiente;
            }
        }
        // Se resuelve el sistema: solucion = {x0, y0, c}
        double[] solucion = resolver(sistema);
        double centroX = solucion[0], centroY = solucion[1];
        // De c = r^2 - x0^2 - y0^2 se despeja el radio
        double radio = Math.sqrt(solucion[2] + centroX * centroX + centroY * centroY);

        // 2) La circunferencia corta el eje real en Rinf y R0
        // Con y = 0 en la ecuación de la circunferencia, los cortes están a una distancia medioAncho = raíz(r^2 - y0^2) del centro
        double medioAncho = Math.sqrt(radio * radio - centroY * centroY);
        double resistenciaInfinito = centroX - medioAncho;
        double resistenciaCero = centroX + medioAncho;

        // 3) alpha sale de qué tan lejos del eje real está el centro
        double alfa = 1 - (2 / Math.PI) * Math.atan(centroY / medioAncho);

        // 4) fc es la frecuencia donde la parte imaginaria es mínima (tope del arco)
        // Se recorren los puntos y se guarda la posición del que tiene la parte imaginaria más negativa
        int posicionMinimo = 0;
        for (int punto = 1; punto < cantidadPuntos; punto++)
            if (partesImaginarias[punto] < partesImaginarias[posicionMinimo]) posicionMinimo = punto;
        // Se devuelve en el orden {R0, Rinf, fc, alpha}
        return new double[]{resistenciaCero, resistenciaInfinito, frecuencias[posicionMinimo], alfa};
    }

    // Resuelve un sistema 3x3 (matriz aumentada) por eliminación de Gauss.
    private static double[] resolver(double[][] sistema) {
        // Etapa 1: eliminación hacia adelante. Se deja la matriz triangular (ceros debajo de la diagonal)
        for (int pivote = 0; pivote < 3; pivote++) {
            // Pivoteo parcial: se elige la fila con el coeficiente más grande en esta columna, para evitar dividir por números casi cero
            int filaMayor = pivote;
            for (int fila = pivote + 1; fila < 3; fila++)
                if (Math.abs(sistema[fila][pivote]) > Math.abs(sistema[filaMayor][pivote])) filaMayor = fila;
            double[] filaIntercambio = sistema[pivote]; sistema[pivote] = sistema[filaMayor]; sistema[filaMayor] = filaIntercambio;
            // A cada fila de abajo se le resta un múltiplo de la fila del pivote para anular su coeficiente en esta columna
            for (int fila = pivote + 1; fila < 3; fila++) {
                double factor = sistema[fila][pivote] / sistema[pivote][pivote];
                for (int columna = pivote; columna < 4; columna++)
                    sistema[fila][columna] -= factor * sistema[pivote][columna];
            }
        }
        // Etapa 2: sustitución hacia atrás. Se despeja desde la última fila (una sola incógnita) hacia la primera
        double[] incognitas = new double[3];
        for (int fila = 2; fila >= 0; fila--) {
            // Al término independiente se le restan las incógnitas que ya se conocen
            double suma = sistema[fila][3];
            for (int columna = fila + 1; columna < 3; columna++) suma -= sistema[fila][columna] * incognitas[columna];
            incognitas[fila] = suma / sistema[fila][fila];
        }
        return incognitas;
    }

    // Lee un CSV (desde los recursos del proyecto) con columnas f [Hz], Re(Z), Im(Z). Devuelve {f[], re[], im[]}.
    public static double[][] leerCsv(String ruta) throws IOException {
        // Paso 1: se lee el archivo línea por línea y cada fila se guarda como {f, Re, Im}
        List<double[]> filas = new ArrayList<>();
        // getResourceAsStream busca el archivo dentro de los recursos del proyecto (src/main/resources)
        try (BufferedReader lector = new BufferedReader(new InputStreamReader(Cole.class.getResourceAsStream(ruta)))) {
            lector.readLine(); // encabezado
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.isBlank()) continue;
                // Cada línea tiene tres números separados por coma
                String[] columnas = linea.split(",");
                filas.add(new double[]{Double.parseDouble(columnas[0]), Double.parseDouble(columnas[1]), Double.parseDouble(columnas[2])});
            }
        }
        // Paso 2: se pasa de "una fila por medición" a "un arreglo por magnitud": datos[0] = f, datos[1] = Re, datos[2] = Im
        double[][] datos = new double[3][filas.size()];
        for (int fila = 0; fila < filas.size(); fila++)
            for (int columna = 0; columna < 3; columna++) datos[columna][fila] = filas.get(fila)[columna];
        return datos;
    }

    // n frecuencias logarítmicas entre fmin y fmax.
    public static double[] frecuencias(double frecuenciaMinima, double frecuenciaMaxima, int cantidad) {
        double[] frecuencias = new double[cantidad];
        // Cada frecuencia es la anterior multiplicada por la misma razón: f_i = fmin * (fmax/fmin)^(i/(n-1)).
        // Así quedan parejas en escala logarítmica, que es como se grafica (la dispersión abarca varias décadas).
        for (int i = 0; i < cantidad; i++)
            frecuencias[i] = frecuenciaMinima * Math.pow(frecuenciaMaxima / frecuenciaMinima, i / (double) (cantidad - 1));
        return frecuencias;
    }
}
