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

            boolean isSizeSet = false;
            boolean isCameraSet = false;

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
                    case "size":
                        parseSize(rest, scene, num_line);
                        isSizeSet = true;
                        break;
                    case "output":
                        parseOutput(rest, scene, num_line);
                        break;
                    case "camera":
                        parseCamera(rest, scene, num_line);
                        isCameraSet = true;
                        break;
                    case "sphere":
                        parseSphere(rest, scene, num_line);
                        break;
                    default:
                        System.err.println("[SceneFileParser] Ligne inconnue '" + key + "' -> '" + rest + "'");
                }
            }

            if (!isSizeSet) {
                System.err.println("[SceneFileParser] Aucune taille spécifiée dans le fichier de scène.");
            }
            if (!isCameraSet) {
                System.err.println("[SceneFileParser] Aucune caméra spécifiée dans le fichier de scène.");
            }

            return scene;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Méthodes dédiées pour chaque cas.

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

    private static void parseOutput(String[] params, Scene scene, int lineNumber) {
        // Ex: output filename
        if (params.length == 1) {
            String filename = params[0];
            scene.setOutputFile(filename);
        } else {
            System.err.println("[SceneFileParser] Sortie invalide: " + String.join(" ", params));
            System.err.println("  Ligne " + lineNumber + ": Il faut exactement un nom de fichier.");
        }
    }

    private static void parseCamera(String[] params, Scene scene, int lineNumber) {
        // Ex: camera lookFromX lookFromY lookFromZ lookAtX lookAtY lookAtZ upDirX upDirY upDirZ fov
        if (params.length == 10) {
            try {
                double lookFromX = Double.parseDouble(params[1]);
                double lookFromY = Double.parseDouble(params[2]);
                double lookFromZ = Double.parseDouble(params[3]);
                double lookAtX = Double.parseDouble(params[4]);
                double lookAtY = Double.parseDouble(params[5]);
                double lookAtZ = Double.parseDouble(params[6]);
                double upDirX = Double.parseDouble(params[7]);
                double upDirY = Double.parseDouble(params[8]);
                double upDirZ = Double.parseDouble(params[9]);
                double fov = Double.parseDouble(params[10]);
                scene.setCamera(new Camera(lookFromX, lookFromY, lookFromZ, lookAtX, lookAtY, lookAtZ, upDirX, upDirY, upDirZ, fov));
            } catch (NumberFormatException e) {
                System.err.println("[SceneFileParser] Paramètres de caméra invalides: " + String.join(" ", params));
                System.err.println("  Ligne " + lineNumber + ": " + e.getMessage());
            }
        } else {
            System.err.println("[SceneFileParser] Paramètres de caméra invalides: " + String.join(" ", params));
            System.err.println("  Ligne " + lineNumber + ": Il faut exactement 10 paramètres.");
        }
    }

}