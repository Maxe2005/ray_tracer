package ray_tracer.geometry.shapes;

import org.junit.jupiter.api.Test;

import ray_tracer.imaging.Color;
import ray_tracer.geometry.Point;
import ray_tracer.geometry.Vector;
import ray_tracer.geometry.DoubleComparisonUtil;
import ray_tracer.raytracer.Ray;
import ray_tracer.geometry.Intersection;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class SphereTest {

    @Test
    public void miss_returns_empty() {
        Sphere s = new Sphere(0.0, 0.0, 5.0, 1.0, new Color(), new Color(), 10);
        Ray r = new Ray(new Point(0.0, 0.0, 0.0), new Vector(0.0, 1.0, 0.0));

        Optional<Intersection> inter = s.intersect(r);
        assertFalse(inter.isPresent());
    }

    @Test
    public void hit_returns_closest_intersection() {
        Sphere s = new Sphere(0.0, 0.0, 5.0, 1.0, new Color(), new Color(), 10);
        Ray r = new Ray(new Point(0.0, 0.0, 0.0), new Vector(0.0, 0.0, 1.0));

        Optional<Intersection> inter = s.intersect(r);
        assertTrue(inter.isPresent());
        // expected intersection at z=4 (distance along ray)
        assertTrue(DoubleComparisonUtil.approximatelyEqual(4.0, inter.get().getDistance()));
        assertSame(s, inter.get().getShape());
    }

    @Test
    public void origin_inside_sphere_returns_positive_distance() {
        Sphere s = new Sphere(0.0, 0.0, 0.0, 2.0, new Color(), new Color(), 10);
        Ray r = new Ray(new Point(0.0, 0.0, 0.0), new Vector(0.0, 0.0, 1.0));

        Optional<Intersection> inter = s.intersect(r);
        assertTrue(inter.isPresent());
        // when origin inside, should return the positive exit distance (radius)
        assertTrue(DoubleComparisonUtil.approximatelyEqual(2.0, inter.get().getDistance()));
    }

    @Test
    public void getters_and_toString() {
        Sphere s = new Sphere(1.0, 2.0, 3.0, 4.5, new Color(0.1, 0.2, 0.3), new Color(0.4, 0.5, 0.6), 10);

        assertTrue(DoubleComparisonUtil.approximatelyEqual(1.0, s.getX()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(2.0, s.getY()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(3.0, s.getZ()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(4.5, s.getRadius()));

        String sStr = s.toString();
        assertTrue(sStr.contains("Sphere"));
    }

    @Test
    public void getters_and_diffuse_specular() {
        Color diffuse = new Color(0.1, 0.2, 0.3);
        Color specular = new Color(0.4, 0.5, 0.6);
        Sphere s = new Sphere(1.0, 2.0, 3.0, 4.5, diffuse, specular, 10);

        assertTrue(DoubleComparisonUtil.approximatelyEqual(1.0, s.getX()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(2.0, s.getY()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(3.0, s.getZ()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(4.5, s.getRadius()));

        assertEquals(diffuse.getR(), s.getDiffuse().getR(), 1e-12);
        assertEquals(specular.getB(), s.getSpecular().getB(), 1e-12);
    }

}
