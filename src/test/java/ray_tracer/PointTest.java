package ray_tracer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PointTest {

    @Test
    public void subtraction_returnsVector() {
        Point p1 = new Point(1.0, 2.0, 3.0);
        Point p2 = new Point(0.2, 0.5, 1.0);

        AbstractVec3 res = p1.subtraction(p2);
        assertTrue(res instanceof Vector);
        Vector v = (Vector) res;
        assertTrue(DoubleComparisonUtil.approximatelyEqual(0.8, v.getX()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(1.5, v.getY()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(2.0, v.getZ()));
    }

    @Test
    public void scalarMultiplication_returnsPoint() {
        Point p = new Point(1.0, -2.0, 3.5);
        AbstractVec3 scaled = p.scalarMultiplication(2.0);
        assertTrue(scaled instanceof Point);
        Point s = (Point) scaled;
        assertTrue(DoubleComparisonUtil.approximatelyEqual(2.0, s.getX()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(-4.0, s.getY()));
    }

}
