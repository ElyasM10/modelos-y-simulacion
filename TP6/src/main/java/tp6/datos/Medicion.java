package tp6.datos;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

// Una medición real de bioimpedancia: tres listas del mismo largo (frecuencia, parte real, parte imaginaria).
// Se lee de un CSV con columnas: f_Hz, Re_ohm, Im_ohm
public class Medicion {

    private final String nombre;
    private final double[] f;
    private final double[] re;
    private final double[] im;

    private Medicion(String nombre, double[] f, double[] re, double[] im) {
        this.nombre = nombre;
        this.f = f;
        this.re = re;
        this.im = im;
    }

    // Lee un archivo CSV cualquiera del disco (el que elige el usuario con "Abrir CSV propio")
    public static Medicion leer(String nombre, File archivo) throws IOException {
        return interpretar(nombre, Files.readAllLines(archivo.toPath(), StandardCharsets.UTF_8));
    }

    // Lee uno de los CSV que vienen adentro del programa (carpeta resources/datos)
    public static Medicion leerIncluido(String nombre, String archivo) throws IOException {
        InputStream in = Medicion.class.getResourceAsStream("/datos/" + archivo + ".csv");
        if (in == null) {
            throw new IOException("No se encontró el recurso datos/" + archivo + ".csv");
        }
        List<String> lineas = new ArrayList<String>();
        try (BufferedReader lector = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                lineas.add(linea);
            }
        }
        return interpretar(nombre, lineas);
    }

    // Recorre las líneas y se queda con las que tienen 3 números (separados por coma, punto y coma, tab o espacios).
    // Lo que no se pueda leer (el encabezado, comentarios) se saltea sin drama.
    private static Medicion interpretar(String nombre, List<String> lineas) throws IOException {

        List<double[]> filas = new ArrayList<double[]>();
        for (String linea : lineas) {
            String[] campos = linea.trim().split("[,;\\t ]+");
            if (campos.length < 3) continue;
            try {
                filas.add(new double[]{Double.parseDouble(campos[0]), Double.parseDouble(campos[1]),
                        Double.parseDouble(campos[2])});
            } catch (NumberFormatException e) {
                // era el encabezado u otra línea que no es un dato: se ignora
            }
        }
        // con muy pocos puntos no se puede ajustar nada razonable
        if (filas.size() < 5) {
            throw new IOException("El archivo necesita al menos 5 filas con: frecuencia [Hz], Re(Z) [Ω], Im(Z) [Ω]");
        }

        int n = filas.size();
        double[] f = new double[n];
        double[] re = new double[n];
        double[] im = new double[n];
        for (int i = 0; i < n; i++) {
            f[i] = filas.get(i)[0];
            re[i] = filas.get(i)[1];
            im[i] = filas.get(i)[2];
            // la frecuencia tiene que ser positiva (se trabaja en escala logarítmica)
            if (f[i] <= 0) throw new IOException("Hay frecuencias menores o iguales a 0 (fila " + (i + 1) + ")");
        }
        return new Medicion(nombre, f, re, im);
    }

    public String getNombre() { return nombre; }

    public double[] getF() { return f; }

    public double[] getRe() { return re; }

    public double[] getIm() { return im; }
}
