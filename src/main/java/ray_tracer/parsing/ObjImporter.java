package ray_tracer.parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ray_tracer.geometry.Point;
import ray_tracer.geometry.shapes.Triangle;
import ray_tracer.raytracer.Scene;

public class ObjImporter {

    public static void importObj(Path objPath, Scene scene) throws IOException, ParserException {
        if (!Files.exists(objPath)) {
            throw new IOException("Fichier OBJ introuvable: " + objPath.toString());
        }

        List<Point> verts = new ArrayList<>();
        Map<String, MtlImporter.Material> materials = new HashMap<>();
        String currentMaterial = null;

        try (InputStream in = Files.newInputStream(objPath);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            Path baseDir = (objPath.getParent() == null) ? objPath.getFileSystem().getPath("") : objPath.getParent();
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] tokens = line.split("\\s+");
                String key = tokens[0].toLowerCase();
                switch (key) {
                    case "v":
                        if (tokens.length >= 4) {
                            try {
                                double x = Double.parseDouble(tokens[1]);
                                double y = Double.parseDouble(tokens[2]);
                                double z = Double.parseDouble(tokens[3]);
                                verts.add(new Point(x, y, z));
                            } catch (NumberFormatException e) {
                                // ignore malformed vertex
                            }
                        }
                        break;
                    case "mtllib":
                        if (tokens.length >= 2) {
                            Path mtlPath = baseDir.resolve(tokens[1]).normalize();
                            try {
                                materials.putAll(MtlImporter.parseMtl(mtlPath));
                            } catch (IOException e) {
                                // ignore missing mtl
                            }
                        }
                        break;
                    case "usemtl":
                        if (tokens.length >= 2) {
                            currentMaterial = tokens[1];
                        }
                        break;
                    case "f":
                        if (tokens.length >= 4) {
                            // collect vertex indices (support v, v/vt, v//vn)
                            int n = tokens.length - 1;
                            int[] idx = new int[n];
                            boolean valid = true;
                            for (int i = 0; i < n; i++) {
                                String part = tokens[i + 1];
                                String[] comps = part.split("/");
                                try {
                                    int vi = Integer.parseInt(comps[0]);
                                    if (vi < 0) vi = verts.size() + vi + 1; // negative indexing
                                    idx[i] = vi - 1; // convert to 0-based
                                    if (idx[i] < 0 || idx[i] >= verts.size()) valid = false;
                                } catch (NumberFormatException e) {
                                    valid = false;
                                }
                            }
                            if (!valid) break;
                            // triangulate fan if needed
                            for (int i = 1; i < n - 1; i++) {
                                Point p0 = verts.get(idx[0]);
                                Point p1 = verts.get(idx[i]);
                                Point p2 = verts.get(idx[i + 1]);
                                MtlImporter.Material mat = (currentMaterial != null) ? materials.get(currentMaterial) : null;
                                ray_tracer.imaging.Color diff = (mat != null) ? mat.diffuse : ray_tracer.imaging.Color.BLACK;
                                ray_tracer.imaging.Color spec = (mat != null) ? mat.specular : ray_tracer.imaging.Color.BLACK;
                                int shin = (mat != null) ? mat.shininess : 0;
                                scene.addShape(new Triangle(p0, p1, p2, diff, spec, shin));
                            }
                        }
                        break;
                    default:
                        // ignore other lines
                }
            }
        }
    }
}
