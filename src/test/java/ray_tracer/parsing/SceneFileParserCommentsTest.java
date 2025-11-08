package ray_tracer.parsing;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SceneFileParserCommentsTest {

    @Test
    public void comments_and_empty_lines_are_ignored() throws Exception {
        Path tmp = Files.createTempFile("scene_comments", ".scene");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("# this is a comment\n");
            sb.append("\n");
            sb.append("size 8 8\n");
            sb.append("camera 0 0 5 0 0 0 0 1 0 45\n");
            sb.append("# another comment\n");
            Files.writeString(tmp, sb.toString());

            Scene scene = SceneFileParser.parse(tmp.toString());
            assertNotNull(scene);
            assertEquals(8, scene.getWidth());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

}
