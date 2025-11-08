package ray_tracer.parsing;

import org.junit.jupiter.api.Test;
import ray_tracer.geometry.shapes.Plane;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SceneFileParserPlaneTest {

    @Test
    public void plane_adds_shape() throws Exception {
        Path tmp = Files.createTempFile("scene_plane", ".scene");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("size 16 16\n");
            sb.append("camera 0 0 5 0 0 0 0 1 0 45\n");
            sb.append("diffuse 0.1 0.1 0.1\n");
            sb.append("specular 0.0 0.0 0.0\n");
            sb.append("plane 0 0 0 0 1 0\n");
            Files.writeString(tmp, sb.toString());

            Scene scene = SceneFileParser.parse(tmp.toString());
            assertNotNull(scene);
            assertFalse(scene.getShapes().isEmpty());
            assertTrue(scene.getShapes().get(0) instanceof Plane);
            Plane plane = (Plane) scene.getShapes().get(0);
            assertEquals(0.0, plane.getPoint().getX(), 1e-12);
            assertEquals(1.0, plane.getNormal().getY(), 1e-12);
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

}
