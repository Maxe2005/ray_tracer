package ray_tracer.parsing;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SceneFileParserUnknownKeywordTest {

    @Test
    public void unknown_keyword_generates_warning_but_parses() throws Exception {
        Path tmp = Files.createTempFile("scene_unknown_keyword", ".scene");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("size 10 10\n");
            sb.append("camera 0 0 5 0 0 0 0 1 0 45\n");
            sb.append("foobar 1 2 3\n");
            Files.writeString(tmp, sb.toString());

            Scene scene = SceneFileParser.parse(tmp.toString());
            assertNotNull(scene);
            // parsing continues despite unknown keyword
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

}
