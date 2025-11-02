package ray_tracer.parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ray_tracer.imaging.Color;
import ray_tracer.geometry.shapes.Sphere;
import ray_tracer.geometry.Vector;
import ray_tracer.geometry.Point;

public class SceneFileParser {
    static Color waitingDiffuse = null;
    static Color waitingSpecular = null;
    static boolean isSizeSet = false;
    static boolean isCameraSet = false;

    public static Scene parse(String sceneDescriptionPath) {
        Path path = Paths.get(sceneDescriptionPath);
        try (InputStream stream = Files.newInputStream(path);
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

            Scene scene = new Scene();

            initVariables();

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
                    case "ambient":
                        parseAmbient(rest, scene, num_line);
                        break;
                    case "diffuse":
                        parseDiffuse(rest, scene, num_line);
                        break;
                    case "specular":
                        parseSpecular(rest, scene, num_line);
                        break;
                    case "sphere":
                        parseSphere(rest, scene, num_line);
                        break;
                    case "directional":
                        parseDirectional(rest, scene, num_line);
                        break;
                    case "point":
                        parsePoint(rest, scene, num_line);
                        break;
                    default:
                        System.err.println("[SceneFileParser] Ligne inconnue '" + key + "' -> '" + rest + "'");
                }
            }

            handleFinalsErrors(scene);
            return scene;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void initVariables() {
        waitingDiffuse = null;
        waitingSpecular = null;
        isSizeSet = false;
        isCameraSet = false;
    }

    private static void handleFinalsErrors(Scene scene) {
        if (!isSizeSet) {
            System.err.println("[SceneFileParser] Aucune taille spécifiée dans le fichier de scène.");
        }
        if (!isCameraSet) {
            System.err.println("[SceneFileParser] Aucune caméra spécifiée dans le fichier de scène.");
        }
        if (scene.getLights().isEmpty()) {
            System.err.println("[SceneFileParser] Aucune lumière spécifiée dans le fichier de scène.");
        }
        if (scene.getShapes().isEmpty()) {
            System.err.println("[SceneFileParser] Aucune forme spécifiée dans le fichier de scène.");
        }
        if (!scene.areLightsCorrect()) {
            System.err.println("[SceneFileParser] Les lumières spécifiées dans le fichier de scène sont incorrectes.");
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

    private static void parseAmbient(String[] params, Scene scene, int lineNumber) {
        // Ex: ambient r g b
        if (params.length == 3) {
            try {
                double r = Double.parseDouble(params[0]);
                double g = Double.parseDouble(params[1]);
                double b = Double.parseDouble(params[2]);
                scene.setAmbient(new Color(r, g, b));
            } catch (NumberFormatException e) {
                System.err.println("[SceneFileParser] Couleur ambiante invalide: " + String.join(" ", params));
                System.err.println("  Ligne " + lineNumber + ": " + e.getMessage());
            }
        } else {
            System.err.println("[SceneFileParser] Couleur ambiante invalide: " + String.join(" ", params));
            System.err.println("  Ligne " + lineNumber + ": Il faut exactement trois valeurs (r g b).");
        }
    }

    private static void parseDiffuse(String[] params, Scene scene, int lineNumber) {
        // Ex: diffuse r g b
        if (params.length == 3) {
            try {
                double r = Double.parseDouble(params[0]);
                double g = Double.parseDouble(params[1]);
                double b = Double.parseDouble(params[2]);
                if (r + scene.getAmbient().getR() > 1.0 ||
                    g + scene.getAmbient().getG() > 1.0 ||
                    b + scene.getAmbient().getB() > 1.0) {
                    System.err.println("[SceneFileParser] Couleur diffuse invalide (dépassement de 1.0 avec l'ambiant): " + String.join(" ", params));
                    System.err.println("  Ligne " + lineNumber + ": La somme pour chaque composante diffuse + ambiante doit être inférieure ou égale à 1.0.");
                    return;
                }
                waitingDiffuse = new Color(r, g, b);
            } catch (NumberFormatException e) {
                System.err.println("[SceneFileParser] Couleur diffuse invalide: " + String.join(" ", params));
                System.err.println("  Ligne " + lineNumber + ": " + e.getMessage());
            }
        } else {
            System.err.println("[SceneFileParser] Couleur diffuse invalide: " + String.join(" ", params));
            System.err.println("  Ligne " + lineNumber + ": Il faut exactement trois valeurs (r g b).");
        }
    }

    private static void parseSpecular(String[] params, Scene scene, int lineNumber) {
        // Ex: specular r g b
        if (params.length == 3) {
            try {
                double r = Double.parseDouble(params[0]);
                double g = Double.parseDouble(params[1]);
                double b = Double.parseDouble(params[2]);
                waitingSpecular = new Color(r, g, b);
            } catch (NumberFormatException e) {
                System.err.println("[SceneFileParser] Couleur spéculaire invalide: " + String.join(" ", params));
                System.err.println("  Ligne " + lineNumber + ": " + e.getMessage());
            }
        } else {
            System.err.println("[SceneFileParser] Couleur spéculaire invalide: " + String.join(" ", params));
            System.err.println("  Ligne " + lineNumber + ": Il faut exactement trois valeurs (r g b).");
        }
    }

    private static void parseSphere(String[] params, Scene scene, int lineNumber) {
        // Ex: sphere x y z radius
        if (params.length == 4) {
            try {
                double x = Double.parseDouble(params[0]);
                double y = Double.parseDouble(params[1]);
                double z = Double.parseDouble(params[2]);
                double radius = Double.parseDouble(params[3]);
                if (waitingDiffuse == null || waitingSpecular == null) {
                    System.err.println("[SceneFileParser] Matériau non défini avant la sphère: " + String.join(" ", params));
                    System.err.println("  Ligne " + lineNumber + ": Veuillez définir les couleurs diffuse et spéculaire avant de définir une sphère.");
                    return;
                }
                scene.addShape(new Sphere(x, y, z, radius, waitingDiffuse, waitingSpecular));
            } catch (NumberFormatException e) {
                System.err.println("[SceneFileParser] Paramètres de sphère invalides: " + String.join(" ", params));
                System.err.println("  Ligne " + lineNumber + ": " + e.getMessage());
            }
        } else {
            System.err.println("[SceneFileParser] Paramètres de sphère invalides: " + String.join(" ", params));
            System.err.println("  Ligne " + lineNumber + ": Il faut exactement quatre paramètres (x y z radius).");
        }
    }

    private static void parseDirectional(String[] params, Scene scene, int lineNumber) {
        // Ex: directional x y z r g b
        if (params.length == 6) {
            try {
                double x = Double.parseDouble(params[0]);
                double y = Double.parseDouble(params[1]);
                double z = Double.parseDouble(params[2]);
                double r = Double.parseDouble(params[3]);
                double g = Double.parseDouble(params[4]);
                double b = Double.parseDouble(params[5]);
                scene.addLight(new DirectionalLight(new Vector(x, y, z), new Color(r, g, b)));
            } catch (NumberFormatException e) {
                System.err.println("[SceneFileParser] Paramètres de lumière directionnelle invalides: " + String.join(" ", params));
                System.err.println("  Ligne " + lineNumber + ": " + e.getMessage());
            }
        } else {
            System.err.println("[SceneFileParser] Paramètres de lumière directionnelle invalides: " + String.join(" ", params));
            System.err.println("  Ligne " + lineNumber + ": Il faut exactement six paramètres (x y z r g b).");
        }
    }

    private static void parsePoint(String[] params, Scene scene, int lineNumber) {
        // Ex: point x y z r g b
        if (params.length == 6) {
            try {
                double x = Double.parseDouble(params[0]);
                double y = Double.parseDouble(params[1]);
                double z = Double.parseDouble(params[2]);
                double r = Double.parseDouble(params[3]);
                double g = Double.parseDouble(params[4]);
                double b = Double.parseDouble(params[5]);
                scene.addLight(new PointLight(new Point(x, y, z), new Color(r, g, b)));
            } catch (NumberFormatException e) {
                System.err.println("[SceneFileParser] Paramètres de lumière ponctuelle invalides: " + String.join(" ", params));
                System.err.println("  Ligne " + lineNumber + ": " + e.getMessage());
            }
        } else {
            System.err.println("[SceneFileParser] Paramètres de lumière ponctuelle invalides: " + String.join(" ", params));
            System.err.println("  Ligne " + lineNumber + ": Il faut exactement six paramètres (x y z r g b).");
        }
    }
}