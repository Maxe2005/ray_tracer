package ray_tracer.parsing;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class SceneFileParserMoreTest {

    @Test
    public void output_invalid_params_throws() throws Exception {
        Path tmp = Files.createTempFile("scene_output_invalid", ".scene");
        try {
            Files.writeString(tmp, "size 10 10\noutput a b\ncamera 0 0 5 0 0 0 0 1 0 45\n");
            assertThrows(ParserException.class, () -> SceneFileParser.parse(tmp.toString()));
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    public void camera_invalid_count_throws() throws Exception {
        Path tmp = Files.createTempFile("scene_camera_invalid", ".scene");
        try {
            Files.writeString(tmp, "size 10 10\ncamera 1 2 3\n");
            assertThrows(ParserException.class, () -> SceneFileParser.parse(tmp.toString()));
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    public void directional_invalid_count_throws() throws Exception {
        Path tmp = Files.createTempFile("scene_dir_invalid", ".scene");
        try {
            String s = "size 5 5\ncamera 0 0 5 0 0 0 0 1 0 45\ndirectional 1 2 3 0.1 0.2\n";
            Files.writeString(tmp, s);
            assertThrows(ParserException.class, () -> SceneFileParser.parse(tmp.toString()));
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    public void point_invalid_count_throws() throws Exception {
        Path tmp = Files.createTempFile("scene_point_invalid", ".scene");
        try {
            String s = "size 5 5\ncamera 0 0 5 0 0 0 0 1 0 45\npoint 1 2 3 0.1 0.2\n";
            Files.writeString(tmp, s);
            assertThrows(ParserException.class, () -> SceneFileParser.parse(tmp.toString()));
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    public void plane_invalid_count_throws() throws Exception {
        Path tmp = Files.createTempFile("scene_plane_invalid", ".scene");
        try {
            String s = "size 5 5\ncamera 0 0 5 0 0 0 0 1 0 45\nplane 1 2 3 0 1\n";
            Files.writeString(tmp, s);
            assertThrows(ParserException.class, () -> SceneFileParser.parse(tmp.toString()));
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    public void sphere_invalid_params_throws() throws Exception {
        Path tmp = Files.createTempFile("scene_sphere_invalid", ".scene");
        try {
            String s = "size 5 5\ncamera 0 0 5 0 0 0 0 1 0 45\nsphere a b c d\n";
            Files.writeString(tmp, s);
            assertThrows(ParserException.class, () -> SceneFileParser.parse(tmp.toString()));
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    public void maxverts_invalid_throws() throws Exception {
        Path tmp = Files.createTempFile("scene_maxverts_invalid", ".scene");
        try {
            String s = "size 5 5\ncamera 0 0 5 0 0 0 0 1 0 45\nmaxverts 0\n";
            Files.writeString(tmp, s);
            assertThrows(ParserException.class, () -> SceneFileParser.parse(tmp.toString()));
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

    @Test
    public void triangle_successful_with_materials_adds_shape() throws Exception {
        Path tmp = Files.createTempFile("scene_tri_success", ".scene");
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("size 10 10\n");
            sb.append("camera 0 0 5 0 0 0 0 1 0 45\n");
            sb.append("maxverts 3\n");
            sb.append("vertex 0 0 0\n");
            sb.append("vertex 1 0 0\n");
            sb.append("vertex 0 1 0\n");
            sb.append("diffuse 0.1 0.1 0.1\n");
            sb.append("specular 0.2 0.2 0.2\n");
            sb.append("tri 0 1 2\n");
            Files.writeString(tmp, sb.toString());

            Scene scene = SceneFileParser.parse(tmp.toString());
            assertNotNull(scene);
            assertFalse(scene.getShapes().isEmpty());
        } finally {
            Files.deleteIfExists(tmp);
        }
    }

}
