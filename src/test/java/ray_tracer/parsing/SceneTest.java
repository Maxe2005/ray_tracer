package ray_tracer.parsing;

import org.junit.jupiter.api.Test;

import ray_tracer.imaging.Color;
import ray_tracer.geometry.Point;
import ray_tracer.geometry.shapes.Sphere;

import static org.junit.jupiter.api.Assertions.*;

public class SceneTest {

    @Test
    public void addSize_and_output_and_camera_and_ambient_and_collections() {
        Scene s = new Scene();
        s.addSize(100, 80);
        assertEquals(100, s.getWidth());
        assertEquals(80, s.getHeight());

        s.setOutputFile("img.png");
        assertEquals("img.png", s.getOutputFile());

        Camera c = new Camera(0,0,0, 0,0,-1, 0,1,0, 30);
        s.setCamera(c);
        assertNotNull(s.getCamera());

        s.setAmbient(new Color(0.1, 0.1, 0.1));
        assertEquals(0.1, s.getAmbient().getR(), 1e-12);

        // add a shape and a light and check lists
        s.addShape(new Sphere(0,0,0, 1.0, Color.BLACK, Color.WHITE));
        assertFalse(s.getShapes().isEmpty());

        s.addLight(new PointLight(new Point(0,0,0), new Color(0.2,0.2,0.2)));
        assertFalse(s.getLights().isEmpty());
    }

    @Test
    public void addSize_invalid_throws() {
        Scene s = new Scene();
        assertThrows(NumberFormatException.class, () -> s.addSize(0, -1));
    }

    @Test
    public void areLightsCorrect_checks_types_and_totals() {
        Scene s = new Scene();
        // With no lights, should be correct
        assertTrue(s.areLightsCorrect());

        // Add a valid point light
        s.addLight(new PointLight(new Point(0,0,0), new Color(0.2, 0.2, 0.2)));
        assertTrue(s.areLightsCorrect());

        // Add an anonymous light that is not allowed -> should return false
        s.addLight(new AbstractLight(new Color(0.0,0.0,0.0)) {
        });
        assertFalse(s.areLightsCorrect());
    }

}
