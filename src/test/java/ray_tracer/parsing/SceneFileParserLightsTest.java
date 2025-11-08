package ray_tracer.parsing;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SceneFileParserLightsTest {

    @Test
    public void lights_total_exceeding_throws() throws Exception {
        Path tmp = Files.createTempFile("scene_lights_exceed", ".scene");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("size 10 10\n");
            sb.append("camera 0 0 5 0 0 0 0 1 0 45\n");
            // add two directional lights whose components sum over 1.0
            sb.append("directional 1 0 0 0.8 0 0\n");
            sb.append("directional -1 0 0 0.5 0 0\n");
            Files.writeString(tmp, sb.toString());

            assertThrows(ParserException.class, () -> SceneFileParser.parse(tmp.toString()));
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

}
