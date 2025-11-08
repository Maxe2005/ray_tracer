package ray_tracer.parsing;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SceneFileParserTest {

    @Test
    public void parse_minimal_valid_scene_file() throws Exception {
        Path tmp = Files.createTempFile("testscene", ".scene");
        try {
            StringBuilder sb = new StringBuilder();
            // minimal valid scene: size + camera (camera needs 10 params)
            sb.append("size 10 10\n");
            sb.append("camera 0 0 5 0 0 0 0 1 0 45\n");
            Files.writeString(tmp, sb.toString());

            Scene scene = SceneFileParser.parse(tmp.toString());
            assertNotNull(scene);
            assertEquals(10, scene.getWidth());
            assertEquals(10, scene.getHeight());
            assertNotNull(scene.getCamera());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

}
