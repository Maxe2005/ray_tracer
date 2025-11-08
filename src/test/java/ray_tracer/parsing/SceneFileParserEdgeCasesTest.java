package ray_tracer.parsing;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SceneFileParserEdgeCasesTest {

    @Test
    public void missing_size_throws() throws Exception {
        Path tmp = Files.createTempFile("scene_missing_size", ".scene");
        try {
            StringBuilder sb = new StringBuilder();
            // missing size -> should throw in handleFinalsErrors
            sb.append("camera 0 0 5 0 0 0 0 1 0 45\n");
            Files.writeString(tmp, sb.toString());

            assertThrows(ParserException.class, () -> SceneFileParser.parse(tmp.toString()));
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    public void invalid_size_throws() throws Exception {
        Path tmp = Files.createTempFile("scene_invalid_size", ".scene");
        try {
            Files.writeString(tmp, "size a b\ncamera 0 0 5 0 0 0 0 1 0 45\n");
            assertThrows(ParserException.class, () -> SceneFileParser.parse(tmp.toString()));
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    public void diffuse_exceeding_ambient_throws() throws Exception {
        Path tmp = Files.createTempFile("scene_diffuse_exceed", ".scene");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("size 10 10\n");
            sb.append("camera 0 0 5 0 0 0 0 1 0 45\n");
            sb.append("ambient 0.9 0.9 0.9\n");
            // diffuse red 0.2 would exceed red component
            sb.append("diffuse 0.2 0 0\n");
            Files.writeString(tmp, sb.toString());

            assertThrows(ParserException.class, () -> SceneFileParser.parse(tmp.toString()));
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    public void vertex_before_maxverts_throws() throws Exception {
        Path tmp = Files.createTempFile("scene_vertex_before_maxverts", ".scene");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("size 10 10\n");
            sb.append("camera 0 0 5 0 0 0 0 1 0 45\n");
            // vertex before maxverts should throw
            sb.append("vertex 0 0 0\n");
            Files.writeString(tmp, sb.toString());

            assertThrows(ParserException.class, () -> SceneFileParser.parse(tmp.toString()));
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    public void maxverts_and_extra_vertex_warns_but_parses() throws Exception {
        Path tmp = Files.createTempFile("scene_maxverts_warn", ".scene");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("size 10 10\n");
            sb.append("camera 0 0 5 0 0 0 0 1 0 45\n");
            sb.append("maxverts 2\n");
            sb.append("vertex 0 0 0\n");
            sb.append("vertex 1 0 0\n");
            // third vertex should trigger a warning but parsing continues
            sb.append("vertex 2 0 0\n");
            Files.writeString(tmp, sb.toString());

            Scene scene = SceneFileParser.parse(tmp.toString());
            assertNotNull(scene);
            // scene parsed; width should be set to 10
            assertEquals(10, scene.getWidth());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    public void triangle_invalid_indices_throw() throws Exception {
        Path tmp = Files.createTempFile("scene_tri_invalid", ".scene");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("size 10 10\n");
            sb.append("camera 0 0 5 0 0 0 0 1 0 45\n");
            sb.append("maxverts 2\n");
            sb.append("vertex 0 0 0\n");
            sb.append("vertex 1 0 0\n");
            // tri referencing an out-of-range index 5
            sb.append("tri 0 1 5\n");
            Files.writeString(tmp, sb.toString());

            assertThrows(ParserException.class, () -> SceneFileParser.parse(tmp.toString()));
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    public void sphere_without_material_parses_and_adds_shape() throws Exception {
        Path tmp = Files.createTempFile("scene_sphere_nomaterial", ".scene");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("size 10 10\n");
            sb.append("camera 0 0 5 0 0 0 0 1 0 45\n");
            // sphere without preceding diffuse/specular
            sb.append("sphere 0 0 0 1\n");
            Files.writeString(tmp, sb.toString());

            Scene scene = SceneFileParser.parse(tmp.toString());
            assertNotNull(scene);
            // shape added despite missing material
            assertFalse(scene.getShapes().isEmpty());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

}
