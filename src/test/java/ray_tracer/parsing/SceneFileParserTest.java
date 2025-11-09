package ray_tracer.parsing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SceneFileParserTest {

    @BeforeEach
    public void beforeEach() {
        SceneFileParser.clearState();
    }

    @AfterEach
    public void afterEach() {
        SceneFileParser.clearState();
    }

    private Path writeTemp(String content) throws Exception {
        Path tmp = Files.createTempFile("scene-test", ".txt");
        Files.writeString(tmp, content);
        return tmp;
    }

    @Test
    public void unknownKeywordProducesWarning() throws Exception {
        String content = String.join("\n",
                "size 10 10",
                "camera 0 0 0 0 0 -1 0 1 0 90",
                "foobar"
        );

        Path p = writeTemp(content);
        try {
            SceneFileParser.parse(p.toString());
            List<String> warnings = SceneFileParser.getWarnings();
            assertTrue(warnings.stream().anyMatch(s -> s.contains("Mot-clé 'foobar'")), "Expected warning for unknown keyword 'foobar'");
        } finally {
            Files.deleteIfExists(p);
        }
    }

    @Test
    public void sphereWithoutMaterialsAddsWarnings() throws Exception {
        String content = String.join("\n",
                "size 5 5",
                "camera 0 0 0 0 0 -1 0 1 0 90",
                "sphere 0 0 0 1"
        );
        Path p = writeTemp(content);
        try {
            SceneFileParser.parse(p.toString());
            List<String> warnings = SceneFileParser.getWarnings();
            assertTrue(warnings.stream().anyMatch(s -> s.contains("Matériau non défini") && s.contains("sphère")), "Expected material warning for sphere");
        } finally {
            Files.deleteIfExists(p);
        }
    }

    @Test
    public void maxVertsAndVertexLimit_behavior() throws Exception {
        String content = String.join("\n",
                "size 5 5",
                "camera 0 0 0 0 0 -1 0 1 0 90",
                "maxverts 1",
                "vertex 0 0 0",
                "vertex 1 1 1"
        );
        Path p = writeTemp(content);
        try {
            SceneFileParser.parse(p.toString());
            assertEquals(1, SceneFileParser.getMaxVerts());
            assertEquals(1, SceneFileParser.getVertexList().size(), "Only one vertex should be kept when maxverts is 1");
            List<String> warnings = SceneFileParser.getWarnings();
            assertTrue(warnings.stream().anyMatch(s -> s.toLowerCase().contains("maxverts") || s.contains("maximum de sommets")), "Expected a warning about maxverts being reached");
        } finally {
            Files.deleteIfExists(p);
        }
    }

    @Test
    public void triWithInvalidIndicesThrows() throws Exception {
        String content = String.join("\n",
                "size 5 5",
                "camera 0 0 0 0 0 -1 0 1 0 90",
                "maxverts 1",
                "vertex 0 0 0",
                "tri 0 1 2"
        );
        Path p = writeTemp(content);
        try {
            ParserException ex = assertThrows(ParserException.class, () -> SceneFileParser.parse(p.toString()));
            assertTrue(ex.getMessage().contains("Indices de triangle invalides") || ex.getMessage().contains("Indices"));
        } finally {
            Files.deleteIfExists(p);
        }
    }

    @Test
    public void invalidSizeLineThrowsParserException() throws Exception {
        String content = "size 100"; // only one parameter
        Path p = writeTemp(content);
        try {
            ParserException ex = assertThrows(ParserException.class, () -> SceneFileParser.parse(p.toString()));
            assertTrue(ex.getMessage().contains("Taille invalide"));
        } finally {
            Files.deleteIfExists(p);
        }
    }

    @Test
    public void diffuseOverflowAgainstAmbientThrows() throws Exception {
        String content = String.join("\n",
                "size 5 5",
                "camera 0 0 0 0 0 -1 0 1 0 90",
                "ambient 0.9 0 0",
                "diffuse 0.2 0 0"
        );
        Path p = writeTemp(content);
        try {
            ParserException ex = assertThrows(ParserException.class, () -> SceneFileParser.parse(p.toString()));
            assertTrue(ex.getMessage().contains("Couleur diffuse invalide (dépassement") || ex.getMessage().contains("Couleur diffuse invalide"));
        } finally {
            Files.deleteIfExists(p);
        }
    }

    @Test
    public void parseOutputSetsOutputFile() throws Exception {
        String content = String.join("\n",
                "size 4 4",
                "camera 0 0 0 0 0 -1 0 1 0 90",
                "output result.png"
        );
        Path p = writeTemp(content);
        try {
            Scene scene = SceneFileParser.parse(p.toString());
            assertNotNull(scene);
            assertEquals("result.png", scene.getOutputFile());
        } finally {
            Files.deleteIfExists(p);
        }
    }

    @Test
    public void directionalAndPointLightsAreParsed() throws Exception {
        String content = String.join("\n",
                "size 4 4",
                "camera 0 0 0 0 0 -1 0 1 0 90",
                "directional 1 0 0 0.1 0.1 0.1",
                "point 0 1 0 0.2 0.2 0.2"
        );
        Path p = writeTemp(content);
        try {
            Scene scene = SceneFileParser.parse(p.toString());
            assertNotNull(scene);
            assertEquals(2, scene.getLights().size());
            boolean hasDir = scene.getLights().stream().anyMatch(l -> l instanceof DirectionalLight);
            boolean hasPoint = scene.getLights().stream().anyMatch(l -> l instanceof PointLight);
            assertTrue(hasDir && hasPoint, "Expected both directional and point lights");
        } finally {
            Files.deleteIfExists(p);
        }
    }

    @Test
    public void planeRequiresMaterialsButIsAddedWhenPresent() throws Exception {
        String content = String.join("\n",
                "size 4 4",
                "camera 0 0 0 0 0 -1 0 1 0 90",
                "diffuse 0 0 0",
                "specular 0 0 0",
                "plane 0 0 0 0 1 0"
        );
        Path p = writeTemp(content);
        try {
            Scene scene = SceneFileParser.parse(p.toString());
            assertNotNull(scene);
            assertEquals(1, scene.getShapes().size());
            assertTrue(scene.getShapes().get(0) instanceof ray_tracer.geometry.shapes.Plane);
        } finally {
            Files.deleteIfExists(p);
        }
    }

    @Test
    public void vertexWithoutMaxvertsThrows() throws Exception {
        String content = String.join("\n",
                "size 4 4",
                "camera 0 0 0 0 0 -1 0 1 0 90",
                "vertex 0 0 0"
        );
        Path p = writeTemp(content);
        try {
            ParserException ex = assertThrows(ParserException.class, () -> SceneFileParser.parse(p.toString()));
            assertTrue(ex.getMessage().toLowerCase().contains("maxverts") || ex.getMessage().contains("maxverts"));
        } finally {
            Files.deleteIfExists(p);
        }
    }

    @Test
    public void specularInvalidThrowsParserException() throws Exception {
        String content = String.join("\n",
                "size 4 4",
                "camera 0 0 0 0 0 -1 0 1 0 90",
                "specular a b c"
        );
        Path p = writeTemp(content);
        try {
            ParserException ex = assertThrows(ParserException.class, () -> SceneFileParser.parse(p.toString()));
            assertTrue(ex.getMessage().contains("Couleur spéculaire invalide") || ex.getMessage().toLowerCase().contains("specular"));
        } finally {
            Files.deleteIfExists(p);
        }
    }
}
