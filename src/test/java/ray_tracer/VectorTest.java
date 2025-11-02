package ray_tracer;

import org.junit.jupiter.api.Test;

import ray_tracer.geometry.DoubleComparisonUtil;
import ray_tracer.geometry.Vector;

import static org.junit.jupiter.api.Assertions.*;

public class VectorTest {

    @Test
    public void addition_subtraction_scalar_and_dot_cross_norm_normalize() {
        Vector a = new Vector(1.0, 0.0, 0.0);
        Vector b = new Vector(0.0, 1.0, 0.0);

        Vector sum = (Vector) a.addition(b);
        assertTrue(DoubleComparisonUtil.approximatelyEqual(1.0, sum.getX()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(1.0, sum.getY()));

        Vector diff = (Vector) a.subtraction(b);
        assertTrue(DoubleComparisonUtil.approximatelyEqual(1.0, diff.getX()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(-1.0, diff.getY()));

        Vector scaled = (Vector) a.scalarMultiplication(3.0);
        assertTrue(DoubleComparisonUtil.approximatelyEqual(3.0, scaled.getX()));

        double dot = a.scalarProduct(b);
        assertTrue(DoubleComparisonUtil.approximatelyEqual(0.0, dot));

        Vector cross = (Vector) a.vectorialProduct(b);
        // cross of (1,0,0) and (0,1,0) is (0,0,1)
        assertTrue(DoubleComparisonUtil.approximatelyEqual(0.0, cross.getX()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(0.0, cross.getY()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(1.0, cross.getZ()));

        Vector v = new Vector(3.0, 4.0, 0.0);
        assertTrue(DoubleComparisonUtil.approximatelyEqual(5.0, v.norm()));

        Vector unit = (Vector) v.normalize();
        assertTrue(DoubleComparisonUtil.approximatelyEqual(1.0, unit.norm()));
    }

    @Test
    public void normalize_zero_throws() {
        Vector zero = new Vector(0.0, 0.0, 0.0);
        assertThrows(ArithmeticException.class, () -> zero.normalize());
    }

}
