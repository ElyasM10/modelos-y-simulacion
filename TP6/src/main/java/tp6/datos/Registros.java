package tp6.datos;

import java.io.IOException;

// Los 4 registros de la base de Dell'Osa et al. que vienen con el programa (ver datos/README.md para saber por qué esos)
public class Registros {

    // nombre de cada archivo CSV (sin el .csv)
    public static final String[] ARCHIVOS = {"humano_A", "humano_B", "agar_A", "agar_B"};

    // cómo se muestra cada uno en pantalla
    public static final String[] DESCRIPCION = {
            "Humano, registro ID 1 (Ayllón)", "Humano, registro ID 401 (Ayllón)",
            "Fantoma de agar, registro ID 1311", "Fantoma de agar, registro ID 1401"
    };

    // Carga el registro número k (0 a 3)
    public static Medicion leer(int k) throws IOException {
        return Medicion.leerIncluido(DESCRIPCION[k], ARCHIVOS[k]);
    }
}
