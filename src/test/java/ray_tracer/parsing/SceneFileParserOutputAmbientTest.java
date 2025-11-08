package ray_tracer.parsing;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SceneFileParserOutputAmbientTest {

    @Test
    public void output_and_ambient_parsing() throws Exception {
        Path tmp = Files.createTempFile("scene_output_ambient", ".scene");
        try {
            String s = "size 20 15\ncamera 0 0 5 0 0 0 0 1 0 45\nambient 0.1 0.1 0.1\noutput result.png\n";
            Files.writeString(tmp, s);

            Scene scene = SceneFileParser.parse(tmp.toString());
            assertNotNull(scene);
            assertEquals(20, scene.getWidth());
            assertEquals(15, scene.getHeight());
            assertEquals("result.png", scene.getOutputFile());
            assertNotNull(scene.getAmbient());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

}
