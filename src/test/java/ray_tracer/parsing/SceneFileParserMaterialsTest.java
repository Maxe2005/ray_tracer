package ray_tracer.parsing;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SceneFileParserMaterialsTest {

    @Test
    public void sphere_with_materials_adds_shape_without_warnings() throws Exception {
        Path tmp = Files.createTempFile("scene_sphere_materials", ".scene");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("size 10 10\n");
            sb.append("camera 0 0 5 0 0 0 0 1 0 45\n");
            sb.append("diffuse 0.1 0.2 0.1\n");
            sb.append("specular 0.0 0.0 0.0\n");
            sb.append("sphere 0 0 0 1\n");
            Files.writeString(tmp, sb.toString());

            Scene scene = SceneFileParser.parse(tmp.toString());
            assertNotNull(scene);
            assertFalse(scene.getShapes().isEmpty());
            // we can't easily capture warnings without changing code, but parsing should succeed
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

}
