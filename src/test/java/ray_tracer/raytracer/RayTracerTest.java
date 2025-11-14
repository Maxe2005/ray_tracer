package ray_tracer.raytracer;

import org.junit.jupiter.api.Test;
import ray_tracer.parsing.Scene;
import ray_tracer.parsing.Camera;

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

}
