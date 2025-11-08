package ray_tracer;

import org.junit.jupiter.api.Test;

import ray_tracer.geometry.shapes.Shape;
import ray_tracer.imaging.Color;

import static org.junit.jupiter.api.Assertions.*;

public class ShapeTest {

    @Test
    public void null_diffuse_and_specular_become_black() {
        Shape s = new Shape(null, null) {};
        assertEquals(Color.BLACK.getR(), s.getDiffuse().getR(), 1e-12);
        assertEquals(Color.BLACK.getG(), s.getSpecular().getG(), 1e-12);
    }

    @Test
    public void toString_contains_shape() {
        Shape s = new Shape(Color.WHITE, Color.BLACK) {};
        String t = s.toString();
        assertTrue(t.contains("Shape"));
        assertTrue(t.contains("diffuse"));
    }

}
