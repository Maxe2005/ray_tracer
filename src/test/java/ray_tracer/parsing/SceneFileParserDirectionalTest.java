package ray_tracer.parsing;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SceneFileParserDirectionalTest {

    @Test
    public void directional_adds_directional_light() throws Exception {
        Path tmp = Files.createTempFile("scene_directional", ".scene");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("size 10 10\n");
            sb.append("camera 0 0 5 0 0 0 0 1 0 45\n");
            sb.append("directional 1 2 3 0.1 0.2 0.3\n");
            Files.writeString(tmp, sb.toString());

            Scene scene = SceneFileParser.parse(tmp.toString());
            assertNotNull(scene);
            assertFalse(scene.getLights().isEmpty());
            assertTrue(scene.getLights().get(0) instanceof DirectionalLight);
            DirectionalLight dl = (DirectionalLight) scene.getLights().get(0);
            assertEquals(1.0, dl.getDirection().getX(), 1e-12);
            assertEquals(0.1, dl.getColor().getR(), 1e-12);
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

}
