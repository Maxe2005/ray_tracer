package ray_tracer.parsing;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

import ray_tracer.geometry.AABB;
import ray_tracer.geometry.Point;
import ray_tracer.geometry.shapes.Shape;
import ray_tracer.raytracer.Camera;
import ray_tracer.raytracer.Scene;

/**
 * Coordinateur de parsing : choisit le parser adapté selon l'extension
 * (.scene -> SceneTextParser, .obj -> ObjImporter).
 */
public class SceneFileParser {

    public static Scene parse(String inputPath) throws ParserException {
        Path p = Paths.get(inputPath);
        String name = p.getFileName().toString().toLowerCase();
        if (name.endsWith(".scene")) {
            return SceneTextParser.parse(inputPath);
        } else if (name.endsWith(".obj")) {
            Scene scene = new Scene();
            // sensible defaults
            scene.addSize(800, 600);
            try {
                ObjImporter.importObj(p, scene);
            } catch (IOException e) {
                throw new ParserException("Erreur E/S lors de l'import OBJ: " + e.getMessage(), 0);
            }

            // compute bounding box to position camera if missing
            AABB total = null;
            for (Shape s : scene.getShapes()) {
                AABB b = s.getBounds();
                if (b == null) continue;
                total = (total == null) ? b : AABB.surroundingBox(total, b);
            }
            if (scene.getCamera() == null && total != null) {
                Point center = total.centroid();
                double dx = total.getMax().getX() - total.getMin().getX();
                double dy = total.getMax().getY() - total.getMin().getY();
                double dz = total.getMax().getZ() - total.getMin().getZ();
                double extent = Math.max(dx, Math.max(dy, dz));
                Point lookFrom = new Point(center.getX(), center.getY(), center.getZ() + extent * 2.5 + 1.0);
                scene.setCamera(new Camera(lookFrom.getX(), lookFrom.getY(), lookFrom.getZ(), center.getX(), center.getY(), center.getZ(), 0, 1, 0, 45.0));
            }

            // default output name
            String out = p.getFileName().toString();
            out = out.substring(0, out.lastIndexOf('.')) + ".png";
            scene.setOutputFile(out);

            return scene;
        } else {
            throw new ParserException("Type de fichier non supporté: " + inputPath, 0);
        }
    }
}
