package tp5.grafico;

import java.awt.Color;
import java.util.function.DoubleUnaryOperator;

public class Curva {

    public final DoubleUnaryOperator funcion;
    public final String etiqueta;
    public final Color color;

    public Curva(DoubleUnaryOperator funcion, String etiqueta, Color color) {
        this.funcion = funcion;
        this.etiqueta = etiqueta;
        this.color = color;
    }
}
