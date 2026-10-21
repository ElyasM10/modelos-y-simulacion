package tp6.modelo;

// Un tejido: simplemente un nombre ("Hígado", "Pulmón"...) pegado a su modelo de Cole.
// Sirve para poder graficar varios juntos con su etiqueta en la leyenda.
public class Tejido {

    private final String nombre;
    private final ModeloCole modelo;

    public Tejido(String nombre, ModeloCole modelo) {
        this.nombre = nombre;
        this.modelo = modelo;
    }

    public String getNombre() { return nombre; }

    public ModeloCole getModelo() { return modelo; }
}
