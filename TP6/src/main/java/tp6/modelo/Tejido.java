package tp6.modelo;

// Un tejido (o muestra) con nombre y los parámetros de su modelo de Cole
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
