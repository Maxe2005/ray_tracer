package ray_tracer.parsing;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SceneFileParserPointLightTest {

    @Test
    public void point_adds_point_light() throws Exception {
        Path tmp = Files.createTempFile("scene_point", ".scene");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("size 12 12\n");
            sb.append("camera 0 0 5 0 0 0 0 1 0 45\n");
            sb.append("point 1 2 3 0.2 0.3 0.4\n");
            Files.writeString(tmp, sb.toString());

            Scene scene = SceneFileParser.parse(tmp.toString());
            assertNotNull(scene);
            assertFalse(scene.getLights().isEmpty());
            assertTrue(scene.getLights().get(0) instanceof PointLight);
            PointLight pl = (PointLight) scene.getLights().get(0);
            assertEquals(1.0, pl.getPosition().getX(), 1e-12);
            assertEquals(0.3, pl.getColor().getG(), 1e-12);
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

}
