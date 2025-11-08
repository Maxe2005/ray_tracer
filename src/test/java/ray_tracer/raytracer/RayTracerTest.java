package ray_tracer.raytracer;

import org.junit.jupiter.api.Test;
import ray_tracer.parsing.Scene;
import ray_tracer.parsing.Camera;
import ray_tracer.imaging.Color;
import ray_tracer.geometry.Point;
import ray_tracer.geometry.Vector;
import ray_tracer.geometry.Intersection;
import ray_tracer.geometry.shapes.Shape;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RayTracerTest {

    @Test
    public void setPixelsDimensions_and_getters() {
        Scene scene = new Scene();
        scene.addSize(4, 2);
        Camera cam = new Camera(0,0,5, 0,0,0, 0,1,0, 90);
        scene.setCamera(cam);
        RayTracer rt = new RayTracer(scene);
        rt.setPixelsDimensions();

        double expectedPixelHeight = Math.tan(cam.getRadiansFov() / 2.0);
        double expectedPixelWidth = expectedPixelHeight * ((double) scene.getWidth() / (double) scene.getHeight());

        assertEquals(expectedPixelHeight, rt.getPixelHeight(), 1e-12);
        assertEquals(expectedPixelWidth, rt.getPixelWidth(), 1e-12);
    }

    @Test
    public void getPixelColor_returns_ambient() {
        Scene scene = new Scene();
        scene.addSize(2,2);
        Camera cam = new Camera(0,0,5, 0,0,0, 0,1,0, 60);
        scene.setCamera(cam);
        Color ambient = new Color(0.1, 0.2, 0.3);
        scene.setAmbient(ambient);

        RayTracer rt = new RayTracer(scene);

        // build a dummy intersection
        Shape s = new Shape(Color.WHITE, Color.BLACK) {
            @Override
            public Optional<Intersection> intersect(ray_tracer.raytracer.Ray ray) {
                return Optional.empty();
            }
        };
        Intersection inter = new Intersection(new ray_tracer.raytracer.Ray(new Point(0,0,0), new Vector(1,0,0)), 1.0, s);

        assertEquals(ambient, rt.getPixelColor(inter));
    }

}
