package ray_tracer.geometry.shapes;

import ray_tracer.imaging.Color;

import org.junit.jupiter.api.Test;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;


public class ShapeTest {

    @Test
    public void null_diffuse_and_specular_become_black() {
        Shape s = new Shape(null, null) {};
        assertEquals(Color.BLACK.getR(), s.getDiffuse().getR(), 1e-12);
        assertEquals(Color.BLACK.getG(), s.getDiffuse().getG(), 1e-12);
        assertEquals(Color.BLACK.getB(), s.getDiffuse().getB(), 1e-12);
        assertEquals(Color.BLACK.getR(), s.getSpecular().getR(), 1e-12);
        assertEquals(Color.BLACK.getG(), s.getSpecular().getG(), 1e-12);
        assertEquals(Color.BLACK.getB(), s.getSpecular().getB(), 1e-12);
    }

    @Test
    public void toString_contains_shape() {
        Shape s = new Shape(Color.WHITE, Color.BLACK) {};
        String t = s.toString();
        assertTrue(t.contains("Shape"));
    }

    @Test
    public void intersect_returns_empty_optional() {
        Shape s = new Shape(Color.WHITE, Color.WHITE) {};
        Optional<?> res = s.intersect(null);
        assertFalse(res.isPresent());
        assertEquals(Optional.empty(), res);
    }

    @Test
    public void preserves_non_null_colors() {
        Shape s = new Shape(Color.WHITE, Color.BLACK) {};
        assertEquals(Color.WHITE.getR(), s.getDiffuse().getR(), 1e-12);
        assertEquals(Color.WHITE.getG(), s.getDiffuse().getG(), 1e-12);
        assertEquals(Color.WHITE.getB(), s.getDiffuse().getB(), 1e-12);

        assertEquals(Color.BLACK.getR(), s.getSpecular().getR(), 1e-12);
        assertEquals(Color.BLACK.getG(), s.getSpecular().getG(), 1e-12);
        assertEquals(Color.BLACK.getB(), s.getSpecular().getB(), 1e-12);
    }
}
