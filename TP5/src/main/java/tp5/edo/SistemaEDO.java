package tp5.edo;

// Un sistema de ecuaciones diferenciales de primer orden: dado el tiempo t y el estado
// actual, devuelve el vector de derivadas (misma dimensión que el estado).
public interface SistemaEDO {
    double[] derivada(double t, double[] estado);
}
