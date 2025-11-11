package ray_tracer.parsing;

import org.junit.jupiter.api.Test;

import ray_tracer.imaging.Color;
import ray_tracer.geometry.Point;
import ray_tracer.geometry.Vector;
import ray_tracer.geometry.shapes.Sphere;
import ray_tracer.raytracer.Ray;
import ray_tracer.geometry.Intersection;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class SceneIntersectTest {

    @Test
    public void scene_returns_closest_shape_intersection() {
        Scene scene = new Scene();
        Sphere s = new Sphere(0.0, 0.0, 5.0, 1.0, new Color(), new Color(), 10);
        scene.addShape(s);

        Ray r = new Ray(new Point(0.0, 0.0, 0.0), new Vector(0.0, 0.0, 1.0));

        Optional<Intersection> opt = scene.intersect(r);
        assertTrue(opt.isPresent());
        assertSame(s, opt.get().getShape());
    }

}
