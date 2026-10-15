package tp6.datos;

import java.io.IOException;

// Los 4 registros de Dell'Osa et al. incluidos en el programa (ver datos/README.md)
public class Registros {

    public static final String[] ARCHIVOS = {"humano_A", "humano_B", "agar_A", "agar_B"};

    public static final String[] DESCRIPCION = {
            "Humano, registro ID 1 (Ayllón)", "Humano, registro ID 401 (Ayllón)",
            "Fantoma de agar, registro ID 1311", "Fantoma de agar, registro ID 1401"
    };

    public static Medicion leer(int k) throws IOException {
        return Medicion.leerIncluido(DESCRIPCION[k], ARCHIVOS[k]);
    }
}
