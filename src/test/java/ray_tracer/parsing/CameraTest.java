package ray_tracer.parsing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CameraTest {

    @Test
    public void getters_and_toString() {
        Camera c = new Camera(1.0, 2.0, 3.0,
                0.0, 0.0, -1.0,
                0.0, 1.0, 0.0,
                60.0);

        assertEquals(1.0, c.getLookFrom().getX(), 1e-12);
        assertEquals(3.0, c.getLookFrom().getZ(), 1e-12);
        assertEquals(-1.0, c.getLookAt().getZ(), 1e-12);
        assertEquals(60.0, c.getFov(), 1e-12);

        String s = c.toString();
        assertTrue(s.contains("Camera"));
        assertTrue(s.contains("fov"));
    }

}
