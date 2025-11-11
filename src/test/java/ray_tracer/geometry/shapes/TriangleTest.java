package ray_tracer.geometry.shapes;

import org.junit.jupiter.api.Test;

import ray_tracer.geometry.Point;
import ray_tracer.geometry.DoubleComparisonUtil;
import ray_tracer.imaging.Color;

import static org.junit.jupiter.api.Assertions.*;

public class TriangleTest {

    @Test
    public void vertex_getters_and_toString() {
        Point v0 = new Point(0.0, 0.0, 0.0);
        Point v1 = new Point(1.0, 0.0, 0.0);
        Point v2 = new Point(0.0, 1.0, 0.0);
        Triangle t = new Triangle(v0, v1, v2, Color.BLACK, Color.WHITE, 10);

        assertTrue(DoubleComparisonUtil.approximatelyEqual(0.0, t.getV0().getX()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(1.0, t.getV1().getX()));

        String s = t.toString();
        assertTrue(s.contains("Triangle"));
    }

}
