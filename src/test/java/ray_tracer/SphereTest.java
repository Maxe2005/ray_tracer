package ray_tracer;

import org.junit.jupiter.api.Test;

import ray_tracer.geometry.DoubleComparisonUtil;
import ray_tracer.geometry.shapes.Sphere;
import ray_tracer.imaging.Color;

import static org.junit.jupiter.api.Assertions.*;

public class SphereTest {

    @Test
    public void getters_and_diffuse_specular() {
        Color diffuse = new Color(0.1, 0.2, 0.3);
        Color specular = new Color(0.4, 0.5, 0.6);
        Sphere s = new Sphere(1.0, 2.0, 3.0, 4.5, diffuse, specular);

        assertTrue(DoubleComparisonUtil.approximatelyEqual(1.0, s.getX()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(2.0, s.getY()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(3.0, s.getZ()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(4.5, s.getRadius()));

        assertEquals(diffuse.getR(), s.getDiffuse().getR(), 1e-12);
        assertEquals(specular.getB(), s.getSpecular().getB(), 1e-12);
    }

    @Test
    public void toString_contains_key_parts() {
        Sphere s = new Sphere(0.0, 0.0, 0.0, 1.0, Color.BLACK, Color.WHITE);
        String sStr = s.toString();
        assertTrue(sStr.contains("Sphere"));
        assertTrue(sStr.contains("radius"));
        assertTrue(sStr.contains("diffuse"));
    }

}
