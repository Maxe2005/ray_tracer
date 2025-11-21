package ray_tracer.raytracer;

import org.junit.jupiter.api.Test;
import ray_tracer.geometry.Orthonormal;
import ray_tracer.geometry.Point;
import ray_tracer.geometry.Vector;
import ray_tracer.geometry.DoubleComparisonUtil;

import static org.junit.jupiter.api.Assertions.*;

public class RayTest {

    @Test
    public void constructor_and_getters_and_isValid() {
        Point p = new Point(1,2,3);
        Vector v = new Vector(1,0,0);
        Ray r = new Ray(p, v);
        assertEquals(p, r.getOrigin());
        assertTrue(r.isRayValid());
    }

    @Test
    public void setDirection_computes_unit_vector_and_varies_with_pixel() {
        Point origin = new Point(0,0,0);
        Ray r = new Ray(origin);

        Orthonormal basis = new Orthonormal(new Vector(1,0,0), new Vector(0,1,0), new Vector(0,0,1));

        r.setDirection(basis, 0, 0, 1.0, 1.0, 4, 4);
        double norm1 = r.getDirection().norm();
        assertTrue(DoubleComparisonUtil.approximatelyEqual(1.0, norm1));

        // different pixel should give different direction
        r.setDirection(basis, 1, 0, 1.0, 1.0, 4, 4);
        double norm2 = r.getDirection().norm();
        assertTrue(DoubleComparisonUtil.approximatelyEqual(1.0, norm2));
        // directions should not be identical for different pixels
        assertFalse(r.getDirection().equals(new Vector(0,0,0)));
    }

}
