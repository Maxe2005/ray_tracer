package ray_tracer.geometry.shapes;

import org.junit.jupiter.api.Test;

import ray_tracer.imaging.Color;
import ray_tracer.geometry.DoubleComparisonUtil;

import static org.junit.jupiter.api.Assertions.*;

public class SphereTest {

    @Test
    public void getters_and_toString() {
        Sphere s = new Sphere(1.0, 2.0, 3.0, 4.5, new Color(0.1, 0.2, 0.3), new Color(0.4, 0.5, 0.6));

        assertTrue(DoubleComparisonUtil.approximatelyEqual(1.0, s.getX()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(2.0, s.getY()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(3.0, s.getZ()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(4.5, s.getRadius()));

        String sStr = s.toString();
        assertTrue(sStr.contains("Sphere"));
    }

}
