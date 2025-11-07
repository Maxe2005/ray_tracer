package ray_tracer.parsing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

import ray_tracer.imaging.Color;
import ray_tracer.geometry.shapes.Sphere;
import ray_tracer.geometry.Vector;
import ray_tracer.geometry.Point;

public class SceneFileParser {
    static List<String> warnings = new ArrayList<>();
    static Color waitingDiffuse = null;
    static Color waitingSpecular = null;
    static boolean isSizeSet = false;
    static boolean isCameraSet = false;

    public static Scene parse(String sceneDescriptionPath) throws ParserException {
        Path path = Paths.get(sceneDescriptionPath);
        try (InputStream stream = Files.newInputStream(path);
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

            Scene scene = new Scene();
            initVariables();
            String line;
            int num_line = 0;
            while ((line = reader.readLine()) != null) {
                num_line++;
                parseLine(line, scene, num_line);
            }

            handleFinalsErrors(scene);
            return scene;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            for (String warning : warnings) {
                System.err.println(warning);
            }
        }
    }

    private static void parseLine(String line, Scene scene, int lineNumber) throws ParserException {
        String[] tokens = line.trim().split("\\s+");
        if (tokens.length == 0 || tokens[0].isEmpty() || tokens[0].equals("#")) {
            return; // Ignorer les lignes vides ou les commentaires
        }

        String keyword = tokens[0].toLowerCase();
        String[] params = java.util.Arrays.copyOfRange(tokens, 1, tokens.length);

        switch (keyword) {
            case "size":
                parseSize(params, scene, lineNumber);
                isSizeSet = true;
                break;
            case "output":
                parseOutput(params, scene, lineNumber);
                break;
            case "camera":
                parseCamera(params, scene, lineNumber);
                isCameraSet = true;
                break;
            case "ambient":
                parseAmbient(params, scene, lineNumber);
                break;
            case "diffuse":
                parseDiffuse(params, scene, lineNumber);
                break;
            case "specular":
                parseSpecular(params, scene, lineNumber);
                break;
            case "sphere":
                parseSphere(params, scene, lineNumber);
                break;
            case "directional":
                parseDirectional(params, scene, lineNumber);
                break;
            case "point":
                parsePoint(params, scene, lineNumber);
                break;
            default:
                addWarning("Mot-clé '" + keyword + "' inconnu", lineNumber, null);
        }
    }

    private static void initVariables() {
        warnings.clear();
        waitingDiffuse = null;
        waitingSpecular = null;
        isSizeSet = false;
        isCameraSet = false;
    }

    private static void handleFinalsErrors(Scene scene) throws ParserException {
        if (!isSizeSet) {
            throw new ParserException("Aucune taille spécifiée dans le fichier de scène.");
        }
        if (!isCameraSet) {
            throw new ParserException("Aucune caméra spécifiée dans le fichier de scène.");
        }
        if (!scene.areLightsCorrect()) {
            throw new ParserException("Les lumières spécifiées dans le fichier de scène sont incorrectes.\n\tChaque composante RGB cumulée des lumières doit être inférieure ou égale à 1.0 et seules les lumières ponctuelles et directionnelles sont autorisées.");
        }
        if (scene.getLights().isEmpty()) {
            addWarning("Aucune lumière spécifiée dans le fichier de scène.", 0, null);
        }
        if (scene.getShapes().isEmpty()) {
            addWarning("Aucune forme spécifiée dans le fichier de scène.", 0, null);
        }
    }

    private static void addWarning(String message, int lineNumber, String explanation) {
        StringBuilder sb = new StringBuilder();
        sb.append("[SceneFileParser] WARNING");;
        if (lineNumber > 0) {
            sb.append(" at line ").append(lineNumber).append(": ");
        } else {
            sb.append(": ");
        }
        sb.append(message);
        if (explanation != null) {
            sb.append("\n\t").append(explanation);
        }
        warnings.add(sb.toString());
    }

    // Méthodes dédiées pour chaque cas.

    private static void parseSize(String[] params, Scene scene, int lineNumber) throws ParserException {
        // Ex: size width height
        if (params.length == 2) {
            try {
                int width = Integer.parseInt(params[0]);
                int height = Integer.parseInt(params[1]);
                scene.addSize(width, height);
            } catch (NumberFormatException e) {
                throw new ParserException("Taille invalide: " + e.getMessage(), lineNumber);
            }
        } else {
            throw new ParserException("Taille invalide:  Il faut exactement deux entiers (width height).", lineNumber);
        }
    }

    private static void parseOutput(String[] params, Scene scene, int lineNumber) throws ParserException {
        // Ex: output filename
        if (params.length == 1) {
            String filename = params[0];
            scene.setOutputFile(filename);
        } else {
            throw new ParserException("Sortie invalide: Il faut exactement un nom de fichier.", lineNumber);
        }
    }

    private static void parseCamera(String[] params, Scene scene, int lineNumber) throws ParserException {
        // Ex: camera lookFromX lookFromY lookFromZ lookAtX lookAtY lookAtZ upDirX upDirY upDirZ fov
        if (params.length == 10) {
            try {
                double lookFromX = Double.parseDouble(params[0]);
                double lookFromY = Double.parseDouble(params[1]);
                double lookFromZ = Double.parseDouble(params[2]);
                double lookAtX = Double.parseDouble(params[3]);
                double lookAtY = Double.parseDouble(params[4]);
                double lookAtZ = Double.parseDouble(params[5]);
                double upDirX = Double.parseDouble(params[6]);
                double upDirY = Double.parseDouble(params[7]);
                double upDirZ = Double.parseDouble(params[8]);
                double fov = Double.parseDouble(params[9]);
                scene.setCamera(new Camera(lookFromX, lookFromY, lookFromZ, lookAtX, lookAtY, lookAtZ, upDirX, upDirY, upDirZ, fov));
            } catch (NumberFormatException e) {
                throw new ParserException("Paramètres de caméra invalides: " + e.getMessage(), lineNumber);
            }
        } else {
            throw new ParserException("Paramètres de caméra invalides:  Il faut exactement 10 paramètres.", lineNumber);
        }
    }

    private static void parseAmbient(String[] params, Scene scene, int lineNumber) throws ParserException {
        // Ex: ambient r g b
        if (params.length == 3) {
            try {
                double r = Double.parseDouble(params[0]);
                double g = Double.parseDouble(params[1]);
                double b = Double.parseDouble(params[2]);
                scene.setAmbient(new Color(r, g, b));
            } catch (NumberFormatException e) {
                throw new ParserException("Couleur ambiante invalide: " + e.getMessage(), lineNumber);
            }
        } else {
            throw new ParserException("Couleur ambiante invalide:  Il faut exactement trois valeurs (r g b).", lineNumber);
        }
    }

    private static void parseDiffuse(String[] params, Scene scene, int lineNumber) throws ParserException {
        // Ex: diffuse r g b
        if (params.length == 3) {
            try {
                double r = Double.parseDouble(params[0]);
                double g = Double.parseDouble(params[1]);
                double b = Double.parseDouble(params[2]);
                if (r + scene.getAmbient().getR() > 1.0 ||
                    g + scene.getAmbient().getG() > 1.0 ||
                    b + scene.getAmbient().getB() > 1.0) {
                    throw new ParserException("Couleur diffuse invalide (dépassement de 1.0 avec l'ambiant): La somme pour chaque composante diffuse + ambiante doit être inférieure ou égale à 1.0.", lineNumber);
                }
                waitingDiffuse = new Color(r, g, b);
            } catch (NumberFormatException e) {
                throw new ParserException("Couleur diffuse invalide: " + e.getMessage(), lineNumber);
            }
        } else {
            throw new ParserException("Couleur diffuse invalide:  Il faut exactement trois valeurs (r g b).", lineNumber);
        }
    }

    private static void parseSpecular(String[] params, Scene scene, int lineNumber) throws ParserException {
        // Ex: specular r g b
        if (params.length == 3) {
            try {
                double r = Double.parseDouble(params[0]);
                double g = Double.parseDouble(params[1]);
                double b = Double.parseDouble(params[2]);
                waitingSpecular = new Color(r, g, b);
            } catch (NumberFormatException e) {
                throw new ParserException("Couleur spéculaire invalide: " + e.getMessage(), lineNumber);
            }
        } else {
            throw new ParserException("Couleur spéculaire invalide:  Il faut exactement trois valeurs (r g b).", lineNumber);
        }
    }

    private static void parseSphere(String[] params, Scene scene, int lineNumber) throws ParserException {
        // Ex: sphere x y z radius
        if (params.length == 4) {
            try {
                double x = Double.parseDouble(params[0]);
                double y = Double.parseDouble(params[1]);
                double z = Double.parseDouble(params[2]);
                double radius = Double.parseDouble(params[3]);
                if (waitingDiffuse == null) {
                    addWarning("Matériau non défini avant la sphère", lineNumber, " Vous n'avez pas défini de couleurs diffuse pour la sphère. Utilisation de la couleur par défaut (noir).");
                }
                if (waitingSpecular == null) {
                    addWarning("Matériau non défini avant la sphère", lineNumber, " Vous n'avez pas défini de couleurs spéculaire pour la sphère. Utilisation de la couleur par défaut (noir).");
                }
                scene.addShape(new Sphere(x, y, z, radius, waitingDiffuse, waitingSpecular));
            } catch (NumberFormatException e) {
                throw new ParserException("Paramètres de sphère invalides: " + e.getMessage(), lineNumber);
            }
        } else {
            throw new ParserException("Paramètres de sphère invalides: Il faut exactement quatre paramètres (x y z radius).", lineNumber);
        }
    }

    private static void parseDirectional(String[] params, Scene scene, int lineNumber) throws ParserException {
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
                throw new ParserException("Paramètres de lumière directionnelle invalides: " + e.getMessage(), lineNumber);
            }
        } else {
            throw new ParserException("Paramètres de lumière directionnelle invalides: Il faut exactement six paramètres (x y z r g b).", lineNumber);
        }
    }

    private static void parsePoint(String[] params, Scene scene, int lineNumber) throws ParserException {
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
                throw new ParserException("Paramètres de lumière ponctuelle invalides: " + e.getMessage(), lineNumber);
            }
        } else {
            throw new ParserException("Paramètres de lumière ponctuelle invalides: Il faut exactement six paramètres (x y z r g b).", lineNumber);
        }
    }
}