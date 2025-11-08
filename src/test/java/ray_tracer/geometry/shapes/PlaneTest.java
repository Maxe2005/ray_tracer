package ray_tracer.geometry.shapes;

import org.junit.jupiter.api.Test;

import ray_tracer.geometry.Vector;
import ray_tracer.geometry.Point;
import ray_tracer.geometry.DoubleComparisonUtil;
import ray_tracer.imaging.Color;

import static org.junit.jupiter.api.Assertions.*;

public class PlaneTest {

    @Test
    public void getters_and_toString() {
        Point p = new Point(1.0, 0.0, -1.0);
        Vector n = new Vector(0.0, 1.0, 0.0);
        Plane plane = new Plane(p, n, Color.WHITE, Color.BLACK);

        assertTrue(DoubleComparisonUtil.approximatelyEqual(1.0, plane.getPoint().getX()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(-1.0, plane.getPoint().getZ()));

        assertTrue(DoubleComparisonUtil.approximatelyEqual(0.0, plane.getNormal().getX()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(1.0, plane.getNormal().getY()));

        String s = plane.toString();
        assertTrue(s.contains("Plane"));
    }

}
