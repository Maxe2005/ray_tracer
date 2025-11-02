package ray_tracer.parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SceneFileParser {

    public static Scene parse(String sceneDescriptionPath) {
        Path path = Paths.get(sceneDescriptionPath);
        try (InputStream stream = Files.newInputStream(path);
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

            Scene scene = new Scene();

            String line;
            int num_line = 0;
            while ((line = reader.readLine()) != null) {
                num_line++;
                // Nettoyage: enlever espaces superflus
                line = line.trim();

                // Ignorer lignes vides et commentaires
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                    continue;
                }

                // Récupérer le mot-clé (premier token)
                String[] tokens = line.split("\\s+", 2);
                String key = tokens[0].toLowerCase();
                // Prendre tout les autres token comme le reste
                String[] rest = tokens.length > 1 ? tokens[1].trim().split("\\s+") : new String[0];

                switch (key) {
                    case "camera":
                        parseCamera(rest, scene, num_line);
                        break;
                    case "sphere":
                        parseSphere(rest, scene, num_line);
                        break;
                    case "light":
                        parseLight(rest, scene, num_line);
                        break;
                    case "material":
                        parseMaterial(rest, scene, num_line);
                        break;
                    case "background":
                        parseBackground(rest, scene, num_line);
                        break;
                    case "size":
                        parseSize(rest, scene, num_line);
                        break;
                    default:
                        System.err.println("[SceneFileParser] Ligne inconnue '" + key + "' -> '" + rest + "'");
                }
            }

            return scene;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Méthodes dédiées pour chaque cas.

    private static void parseCamera(String[] params, Scene scene, int lineNumber) {
        // Ex: camera 0 0 0 0 0 -1 90

        scene.addDirective("camera", params);
    }

    private static void parseSphere(String params, Scene scene, int lineNumber) {
        // Ex: sphere x y z radius materialName
        scene.addDirective("sphere", params);
    }

    private static void parseLight(String params, Scene scene, int lineNumber) {
        // Ex: light x y z r g b intensity
        scene.addDirective("light", params);
    }

    private static void parseMaterial(String params, Scene scene, int lineNumber) {
        // Ex: material name r g b diffuse specular shininess
        scene.addDirective("material", params);
    }

    private static void parseBackground(String params, Scene scene, int lineNumber) {
        // Ex: background r g b
        scene.addDirective("background", params);
    }

    private static void parseSize(String[] params, Scene scene, int lineNumber) {
        // Ex: size width height
        if (params.length == 2) {
            try {
                int width = Integer.parseInt(params[0]);
                int height = Integer.parseInt(params[1]);
                scene.addSize(width, height);
            } catch (NumberFormatException e) {
                System.err.println("[SceneFileParser] Taille invalide: " + String.join(" ", params));
                System.err.println("  Ligne " + lineNumber + ": " + e.getMessage());
            }
        } else {
            System.err.println("[SceneFileParser] Taille invalide: " + String.join(" ", params));
            System.err.println("  Ligne " + lineNumber + ": Il faut exactement deux entiers (width height).");
        }
    }

}