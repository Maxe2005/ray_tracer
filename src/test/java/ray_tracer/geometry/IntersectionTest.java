package ray_tracer.geometry;

import org.junit.jupiter.api.Test;
import ray_tracer.geometry.shapes.Shape;
import ray_tracer.imaging.Color;
import ray_tracer.raytracer.Ray;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class IntersectionTest {

    @Test
    public void constructor_and_getters() {
        Ray r = new Ray(new Point(1,2,3), new Vector(1,0,0));
        Shape s = new Shape(Color.WHITE, Color.BLACK) {
            @Override
            public Optional<Intersection> intersect(ray_tracer.raytracer.Ray ray) {
                return Optional.empty();
            }
        };
        Intersection inter = new Intersection(r, 2.5, s);
        assertEquals(r, inter.getRay());
        assertEquals(2.5, inter.getDistance(), 1e-12);
        assertEquals(s, inter.getShape());
    }

}
