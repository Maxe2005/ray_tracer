package ray_tracer.parsing;

import org.junit.jupiter.api.Test;

import ray_tracer.geometry.Point;
import ray_tracer.geometry.Vector;
import ray_tracer.imaging.Color;

import static org.junit.jupiter.api.Assertions.*;

public class LightTest {

    @Test
    public void point_and_directional_getters_and_toString() {
        Point p = new Point(1.0, 2.0, 3.0);
        PointLight pl = new PointLight(p, new Color(0.1, 0.2, 0.3));
        assertEquals(1.0, pl.getPosition().getX(), 1e-12);
        assertTrue(pl.toString().contains("PointLight"));

        Vector d = new Vector(0.0, -1.0, 0.0);
        DirectionalLight dl = new DirectionalLight(d, new Color(0.5, 0.4, 0.3));
        assertEquals(-1.0, dl.getDirection().getY(), 1e-12);
        assertTrue(dl.toString().contains("DirectionalLight"));
    }

}
