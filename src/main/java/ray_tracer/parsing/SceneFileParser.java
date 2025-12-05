package ray_tracer.parsing;
// Classes pour lire un fichier ligne par ligne
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
// Pour manipuler les chemins de fichiers
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
// Pour stocker des listes
import java.util.List;
import java.util.ArrayList;

import ray_tracer.imaging.Color;
import ray_tracer.geometry.shapes.Sphere;
import ray_tracer.geometry.shapes.Triangle;
import ray_tracer.geometry.shapes.Plane;
import ray_tracer.geometry.Vector;
import ray_tracer.geometry.Point;

public class SceneFileParser {
     // Liste des avertissements rencontrés pendant le parsing
    static List<String> warnings = new ArrayList<>();
    static Color waitingDiffuse = null;
    static Color waitingSpecular = null;
    static int waitingShininess = 0;
    static boolean isSizeSet = false;
    static boolean isCameraSet = false;
    static int maxVerts = 0;
    static List<Point> vertexList = new ArrayList<>();

    public static Scene parse(String sceneDescriptionPath) throws ParserException {
      // Convertion du chemin en objet Path lisible par Java
        Path path = Paths.get(sceneDescriptionPath);
        // Ouvre automatiquement le fichier et garantit qu'il sera refermé
        try (InputStream stream = Files.newInputStream(path);
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {

            Scene scene = new Scene();
            initVariables();
            String line;
            int num_line = 0;
            while ((line = reader.readLine()) != null) {
                num_line++;
                parseLine(line, scene, num_line); // Analyse la ligne
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
        // Le premier mot est le mot-clé
        String keyword = tokens[0].toLowerCase();
        // Le  reste : des paramètres
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
            case "maxdepth":
                parseMaxdepth(params, scene, lineNumber);
                break;
            default:
                addWarning("Mot-clé '" + keyword + "' inconnu", lineNumber, null);
        }
    }

    //   Remet à zéro toutes les variables internes du parser (appelée AU DÉBUT du parsing d’un fichier.scene)
    /**
     * Réinitialise l'état interne du parseur avant l'analyse d'un nouveau fichier.
     */
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
    /**   Cette méthode est appelée APRÈS que toutes les lignes ont été lues.
        Son rôle :
            vérifier que les éléments obligatoires sont présents
            lancer une erreur si c’est grave
            ajouter des warnings pour les oublis mineurs  */
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

    /**
     * Ajoute un warning interne collecté pendant le parsing.
     * @param message message court du warning
     * @param lineNumber numéro de ligne associé (0 si global)
     * @param explanation explication détaillée facultative
     */
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
                if (scene.getCamera() != null) {
                    addWarning("La caméra a déjà été définie précédemment.", lineNumber, "Vous redéfinissez la caméra de la scène. La dernière valeur sera utilisée.");
                }
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
                if (scene.getAmbient() != null) {
                    addWarning("La couleur ambiante a déjà été définie précédemment.", lineNumber, "Vous redéfinissez la couleur ambiante de la scène. La dernière valeur sera utilisée.");
                }
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

    private static void parseShininess(String[] params, Scene scene, int lineNumber) throws ParserException {
        // Ex: shininess value
        if (params.length == 1) {
            try {
                int shininess = Integer.parseInt(params[0]);
                if (shininess < 0) {
                    throw new ParserException("Valeur de brillance invalide: La brillance doit être un entier non négatif.", lineNumber);
                }
                waitingShininess = shininess;
            } catch (NumberFormatException e) {
                throw new ParserException("Valeur de brillance invalide: " + e.getMessage(), lineNumber);
            }
        } else {
            throw new ParserException("Valeur de brillance invalide: Il faut exactement une valeur entière.", lineNumber);
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
                scene.addShape(new Sphere(x, y, z, radius, waitingDiffuse, waitingSpecular, waitingShininess));
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

    private static void parseMaxVerts(String[] params, Scene scene, int lineNumber) throws ParserException {
        // Ex: maxverts n
        if (params.length == 1) {
            try {
                maxVerts = Integer.parseInt(params[0]);
                if (maxVerts <= 0) {
                    throw new ParserException("Le nombre maximum de sommets doit être un entier positif.", lineNumber);
                }
                vertexList.clear();
            } catch (NumberFormatException e) {
                throw new ParserException("Paramètre maxverts invalide: " + e.getMessage(), lineNumber);
            }
        } else {
            throw new ParserException("Paramètre maxverts invalide: Il faut exactement un entier.", lineNumber);
        }
    }

    private static void parseVertex(String[] params, Scene scene, int lineNumber) throws ParserException {
        // Ex: vertex x y z
        if (params.length == 3) {
            try {
                if (maxVerts == 0) {
                    throw new ParserException("Le nombre maximum de sommets (maxverts) doit être défini avant d'ajouter des sommets.", lineNumber);
                }
                if (vertexList.size() >= maxVerts) {
                    addWarning("Le nombre maximum de sommets prévus dans le dernier maxverts déclaré est atteint.", lineNumber, "Ce point sera ignoré. Pour augmenter cette limite, modifiez la valeur de maxverts.");
                    return;
                }
                double x = Double.parseDouble(params[0]);
                double y = Double.parseDouble(params[1]);
                double z = Double.parseDouble(params[2]);
                vertexList.add(new Point(x, y, z));
            } catch (NumberFormatException e) {
                throw new ParserException("Paramètres de sommet invalides: " + e.getMessage(), lineNumber);
            }
        } else {
            throw new ParserException("Paramètres de sommet invalides: Il faut exactement trois paramètres (x y z).", lineNumber);
        }
    }

    private static void parseTriangle(String[] params, Scene scene, int lineNumber) throws ParserException {
        // Ex: tri v1 v2 v3
        if (params.length == 3) {
            try {
                int v1 = Integer.parseInt(params[0]);
                int v2 = Integer.parseInt(params[1]);
                int v3 = Integer.parseInt(params[2]);
                if (v1 < 0 || v1 >= vertexList.size() ||
                    v2 < 0 || v2 >= vertexList.size() ||
                    v3 < 0 || v3 >= vertexList.size()) {
                    throw new ParserException("Indices de triangle invalides: Les indices doivent être compris entre 0 et " + (vertexList.size() - 1) + ".", lineNumber);
                }
                if (waitingDiffuse == null) {
                    addWarning("Matériau non défini avant le triangle", lineNumber, " Vous n'avez pas défini de couleurs diffuse pour le triangle. Utilisation de la couleur par défaut (noir).");
                }
                if (waitingSpecular == null) {
                    addWarning("Matériau non défini avant le triangle", lineNumber, " Vous n'avez pas défini de couleurs spéculaire pour le triangle. Utilisation de la couleur par défaut (noir).");
                }
                Point p1 = vertexList.get(v1);
                Point p2 = vertexList.get(v2);
                Point p3 = vertexList.get(v3);
                scene.addShape(new Triangle(p1, p2, p3, waitingDiffuse, waitingSpecular, waitingShininess));
            } catch (NumberFormatException e) {
                throw new ParserException("Paramètres de triangle invalides: " + e.getMessage(), lineNumber);
            }
        } else {
            throw new ParserException("Paramètres de triangle invalides: Il faut exactement trois indices de sommet.", lineNumber);
        }
    }

    private static void parsePlane(String[] params, Scene scene, int lineNumber) throws ParserException {
        // Ex: plane pointX pointY pointZ normalX normalY normalZ
        if (params.length == 6) {
            try {
                double pointX = Double.parseDouble(params[0]);
                double pointY = Double.parseDouble(params[1]);
                double pointZ = Double.parseDouble(params[2]);
                double normalX = Double.parseDouble(params[3]);
                double normalY = Double.parseDouble(params[4]);
                double normalZ = Double.parseDouble(params[5]);
                if (waitingDiffuse == null) {
                    addWarning("Matériau non défini avant le plan", lineNumber, " Vous n'avez pas défini de couleurs diffuse pour le plan. Utilisation de la couleur par défaut (noir).");
                }
                if (waitingSpecular == null) {
                    addWarning("Matériau non défini avant le plan", lineNumber, " Vous n'avez pas défini de couleurs spéculaire pour le plan. Utilisation de la couleur par défaut (noir).");
                }
                scene.addShape(new Plane(new Point(pointX, pointY, pointZ), new Vector(normalX, normalY, normalZ), waitingDiffuse, waitingSpecular, waitingShininess));
            } catch (NumberFormatException e) {
                throw new ParserException("Paramètres de plan invalides: " + e.getMessage(), lineNumber);
            }
        } else {
            throw new ParserException("Paramètres de plan invalides: Il faut exactement six paramètres (pointX pointY pointZ normalX normalY normalZ).", lineNumber);
        }
    }

    private static void parseMaxdepth(String[] params, Scene scene, int lineNumber) throws ParserException {
        // Ex: maxdepth n
        if (params.length == 1) {
            try {
                int maxDepth = Integer.parseInt(params[0]);
                if (maxDepth <= 0) {
                    throw new ParserException("Le nombre maximum de reflets doit être un entier positif.", lineNumber);
                }
                scene.addMaxRecursionDepth(maxDepth);
            } catch (NumberFormatException e) {
                throw new ParserException("Paramètre maxdepth invalide: " + e.getMessage(), lineNumber);
            }
        } else {
            throw new ParserException("Paramètre maxdepth invalide: Il faut exactement un entier.", lineNumber);
        }
    }

    /* Small helpers added to improve testability ------------------------------------------------- */
    // Return a copy of warnings collected during parsing
    static List<String> getWarnings() {
        return new ArrayList<>(warnings);
    }

    // Clear internal state (useful between tests)
    static void clearState() {
        initVariables();
    }

    // Expose a few internals for assertions in tests
    static int getMaxVerts() {
        return maxVerts;
    }

    static List<Point> getVertexList() {
        return new ArrayList<>(vertexList);
    }

    static Color getWaitingDiffuse() {
        return waitingDiffuse;
    }

    static Color getWaitingSpecular() {
        return waitingSpecular;
    }
}
