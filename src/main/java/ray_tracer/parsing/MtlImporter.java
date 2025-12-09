package ray_tracer.parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import ray_tracer.imaging.Color;

public class MtlImporter {

    public static class Material {
        public Color diffuse = Color.BLACK;
        public Color specular = Color.BLACK;
        public int shininess = 0;
        public String mapKd = null; // texture file (ignored for now)
    }

    public static Map<String, Material> parseMtl(Path mtlPath) throws IOException {
        Map<String, Material> map = new HashMap<>();
        if (!Files.exists(mtlPath)) return map;
        try (InputStream in = Files.newInputStream(mtlPath);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            String line;
            Material current = null;
            String currentName = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] tokens = line.split("\\s+");
                String key = tokens[0].toLowerCase();
                switch (key) {
                    case "newmtl":
                        if (tokens.length >= 2) {
                            currentName = tokens[1];
                            current = new Material();
                            map.put(currentName, current);
                        }
                        break;
                    case "kd":
                        if (current != null && tokens.length >= 4) {
                            try {
                                double r = Double.parseDouble(tokens[1]);
                                double g = Double.parseDouble(tokens[2]);
                                double b = Double.parseDouble(tokens[3]);
                                current.diffuse = new Color(r, g, b);
                            } catch (NumberFormatException e) {
                                // ignore
                            }
                        }
                        break;
                    case "ks":
                        if (current != null && tokens.length >= 4) {
                            try {
                                double r = Double.parseDouble(tokens[1]);
                                double g = Double.parseDouble(tokens[2]);
                                double b = Double.parseDouble(tokens[3]);
                                current.specular = new Color(r, g, b);
                            } catch (NumberFormatException e) {
                                // ignore
                            }
                        }
                        break;
                    case "ns":
                        if (current != null && tokens.length >= 2) {
                            try {
                                current.shininess = (int) Double.parseDouble(tokens[1]);
                            } catch (NumberFormatException e) {
                                // ignore
                            }
                        }
                        break;
                    case "map_kd":
                        if (current != null && tokens.length >= 2) {
                            current.mapKd = tokens[1];
                        }
                        break;
                    default:
                        // ignore other keys
                }
            }
        }
        return map;
    }
}
