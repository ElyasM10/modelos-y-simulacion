package tp4.leontief;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class Leontief {

    // Resuelve x = (I - A)^-1 · y planteando y resolviendo (I - A)·x = y con
    // la descomposición LU de Apache Commons Math (sin invertir la matriz a mano).
    public static RealVector produccion(double[][] a, double[] y) {

        RealMatrix matrizA = new Array2DRowRealMatrix(a);
        RealMatrix iMenosA = MatrixUtils.createRealIdentityMatrix(a.length).subtract(matrizA);

        return new LUDecomposition(iMenosA).getSolver().solve(new ArrayRealVector(y));
    }

    // Aplica una inversa (I - A)^-1 ya conocida a un vector: producción total,
    // o el efecto Δx = (I - A)^-1 · Δy de una variación de demanda final.
    public static RealVector aplicarInversa(double[][] inversa, double[] v) {

        return new Array2DRowRealMatrix(inversa).operate(new ArrayRealVector(v));
    }
}
