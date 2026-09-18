package tp4.leontief;

// Versión CON librería (Apache Commons Math), comentada a propósito.
//
// Esta es la alternativa a Leontief.java: en vez de resolver el sistema a
// mano con SistemaLineal, delega en LUDecomposition (descomposición LU con
// pivoteo parcial) y en RealMatrix.operate para el producto matriz-vector.
// El resultado numérico es exactamente el mismo que la versión sin librería.
//
// Para probarla:
//   1. Descomentar toda la clase de acá abajo.
//   2. Comentar  tp4/leontief/Leontief.java, ya que ambas clases
//      se llaman igual (Leontief) y no pueden coexistir sin conflicto.
//   3. La dependencia de Apache Commons Math ya está declarada en pom.xml.
//
/*
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class Leontief {

    // Resuelve x = (I - A)^-1 · y planteando y resolviendo (I - A)·x = y con
    // la descomposición LU de Apache Commons Math (sin invertir la matriz a mano).
    public static double[] produccion(double[][] a, double[] y) {

        RealMatrix matrizA = new Array2DRowRealMatrix(a);
        RealMatrix iMenosA = MatrixUtils.createRealIdentityMatrix(a.length).subtract(matrizA);

        RealVector x = new LUDecomposition(iMenosA).getSolver().solve(new ArrayRealVector(y));

        return x.toArray();
    }

    // Aplica una inversa (I - A)^-1 ya conocida a un vector: producción total,
    // o el efecto Δx = (I - A)^-1 · Δy de una variación de demanda final.
    public static double[] aplicarInversa(double[][] inversa, double[] v) {

        RealVector resultado = new Array2DRowRealMatrix(inversa).operate(new ArrayRealVector(v));

        return resultado.toArray();
    }
}
*/
