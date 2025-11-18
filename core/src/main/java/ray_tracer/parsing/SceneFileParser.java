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
import ray_tracer.geometry.shapes.Triangle;
import ray_tracer.geometry.shapes.Plane;
import ray_tracer.geometry.Vector;
import ray_tracer.geometry.Point;

public class SceneFileParser {
    static List<String> warnings = new ArrayList<>();
    static Color waitingDiffuse = null;
    static Color waitingSpecular = null;
    static int waitingShininess = 0;
    static boolean isSizeSet = false;
    static boolean isCameraSet = false;
    static int maxVerts = 0;
    static List<Point> vertexList = new ArrayList<>();

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
            case "shininess":
                parseShininess(params, scene, lineNumber);
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
            case "maxverts":
                parseMaxVerts(params, scene, lineNumber);
                break;
            case "vertex":
                parseVertex(params, scene, lineNumber);
                break;
            case "tri":
                parseTriangle(params, scene, lineNumber);
                break;
            case "plane":
                parsePlane(params, scene, lineNumber);
                break;
            default:
                addWarning("Mot-clé '" + keyword + "' inconnu", lineNumber, null);
        }
    }

    private static void initVariables() {
        warnings.clear();
        waitingDiffuse = null;
        waitingSpecular = null;
        waitingShininess = 0;
        isSizeSet = false;
        isCameraSet = false;
        maxVerts = 0;
        vertexList.clear();
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
        if (maxVerts > 0 && vertexList.size() < maxVerts) {
            addWarning("Le nombre de sommets définis est inférieur au maximum spécifié par maxverts.", 0, "Vous avez défini " + vertexList.size() + " sommets, mais le maxverts est de " + maxVerts + ".");
        }
        if (scene.getAmbient().equals(Color.BLACK)) {
            addWarning("Aucune couleur ambiante spécifiée dans le fichier de scène.", 0, "La couleur ambiante par défaut (noir) sera utilisée.");
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
                if (scene.getHeight() > 0 && scene.getWidth() > 0 && height > 0 && width > 0) {
                    addWarning("La taille a déjà été définie précédemment.", lineNumber, "Vous redéfinissez la taille de la scène. La dernière valeur sera utilisée.");
                }
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
            if (!scene.getOutputFile().equals(Scene.DEFAULT_OUTPUT)) {
                addWarning("Le fichier de sortie a déjà été défini précédemment.", lineNumber, "Vous redéfinissez le fichier de sortie. La dernière valeur sera utilisée.");
            }
            scene.setOutputFile(filename);
        } else {
            throw new ParserException("Sortie invalide: Il faut exactement un nom de fichier.", lineNumber);
        }
    }
... (file continues)
