package ray_tracer.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ray_tracer.imaging.Color;
import ray_tracer.geometry.shapes.Shape;
import ray_tracer.geometry.Intersection;
import ray_tracer.geometry.Vector;
import ray_tracer.raytracer.Ray;

public class Scene {
    public static final String DEFAULT_OUTPUT = "output.png";
    public static final int DEFAULT_MAX_RECURSION_DEPTH = 1;
    private int width;
    private int height;
    private Camera camera;
    private String output = DEFAULT_OUTPUT;
    private Color ambient = new Color();
    private int maxRecursionDepth = DEFAULT_MAX_RECURSION_DEPTH;
    private List<AbstractLight> lights = new ArrayList<>();
    private List<Shape> shapes = new ArrayList<>();

    /**
     * Vérifie la validité et la somme des composantes des lumières de la scène.
     * @return true si toutes les lumières sont de types autorisés et leurs composantes RGB cumulées ≤ 1.0
     */
    public boolean areLightsCorrect() {
    // On crée donc trois compteurs (au départ à 0)
    // pour additionner progressivement les couleurs.
        double totalRed = 0;
        double totalGreen = 0;
        double totalBlue = 0;
        for (AbstractLight light : lights) {
            // Vérifie que la lumière appartient à un type autorisé
            if (!(light instanceof PointLight) && !(light instanceof DirectionalLight)) {
                return false;
            }
            // Récupération de la couleur de la lumière actuelle 
            Color color = light.getColor();
         // Ajout des composantes de couleur dans les compteurs
        // On additionne  les valeurs de R/G/B
            totalRed += color.getR();
            totalGreen += color.getG();
            totalBlue += color.getB();
        }
        return totalBlue <= 1.0 && totalGreen <= 1.0 && totalRed <= 1.0;
    }

    /**
     * Calcule la plus proche intersection entre un rayon et toutes les formes de la scène.
     * @param ray rayon testé
     * @return {@code Optional<Intersection>} de la plus proche intersection
     */
    public Optional<Intersection> intersect(Ray ray) {
        if (!ray.isRayValid()) {
            return Optional.empty();
        }
        List<Optional<Intersection>> intersections = new ArrayList<>();
        for (Shape shape : shapes) {
            Optional<Intersection> intersection = shape.intersect(ray);
            if (intersection.isPresent()) {
                intersections.add(intersection);
            }
        }
        if (!intersections.isEmpty()) {
            Optional<Intersection> closestIntersection = intersections.get(0);
            for (Optional<Intersection> intersection : intersections) {
                if (intersection.get().getDistance() < closestIntersection.get().getDistance()) {
                    closestIntersection = intersection;
                }
            }
            return closestIntersection;
        } else {
            return Optional.empty();
        }
    }

    /**
     * Calcule la couleur totale en un point (ambiant + contributions directes des lumières sans ombres).
     * @param intersection intersection considérée
     * @param eyeDirection vecteur vers la caméra
     * @return {@code Color} résultant
     */
    public Color getTotalColorAt(Intersection intersection, Vector eyeDirection){
        Color totalLight = ambient;
        for (AbstractLight light : lights) {
            Ray shadowRay = new Ray(intersection.getPoint(), light.getDirectionFrom(intersection.getPoint()));
            Optional<Intersection> ombreIntersection = intersect(shadowRay);
            if (!ombreIntersection.isPresent()){
                // Use the provided eyeDirection (vector from point -> camera)
                totalLight = totalLight.addition(light.getColorAt(intersection, eyeDirection));
            }
        }
        return totalLight;
    }

    /**
     * Calcule la couleur en tenant compte des réflexions récursives jusqu'à une profondeur donnée.
     * @param intersection intersection considérée
     * @param eyeDirection vecteur vers la caméra
     * @param recursionDepth profondeur de récursion restante
     * @return {@code Color} résultat
     */
    public Color getRecursionColorAt(Intersection intersection, Vector eyeDirection, int recursionDepth) {
        Color directColor = this.getTotalColorAt(intersection, eyeDirection);
        if (recursionDepth <= 1 || intersection.getShape().getSpecular().equals(Color.BLACK)) {
            return directColor;
        }
        Vector reflectDir = eyeDirection.addition(intersection.getNormal().scalarMultiplication(2 * intersection.getNormal().scalarProduct(eyeDirection.scalarMultiplication(-1)))).normalize();
        Ray reflectRay = new Ray(intersection.getPoint(), reflectDir.scalarMultiplication(-1));
        Optional<Intersection> reflectIntersection = this.intersect(reflectRay);
        if (!reflectIntersection.isPresent()) {
            return directColor;
        }
        Color reflectedColor = this.getRecursionColorAt(reflectIntersection.get(), reflectDir, recursionDepth - 1);
        return directColor.addition(reflectedColor.schurProduct(intersection.getShape().getSpecular()));
    }

    /**
     * Helper : calcule la couleur complète en déterminant la direction vers l'oeil puis en appelant la récursion.
     * @param intersection intersection considérée
     * @return {@code Color} résultat
     */
    public Color getTotalRecursionColorAt(Intersection intersection){
        // Compute the eye/view direction for this intersection: vector from the point to the camera
        Vector eyeDirection = intersection.getRay().getDirection().scalarMultiplication(-1).normalize();
        return getRecursionColorAt(intersection, eyeDirection, maxRecursionDepth);
    }

    /**
     * Définit la taille (width x height) de la scène.
     * @param width largeur en pixels (>0)
     * @param height hauteur en pixels (>0)
     * @throws NumberFormatException si valeurs invalides
     */
    public void addSize(int width, int height) throws NumberFormatException {
        if (width <= 0 || height <= 0){
            throw new NumberFormatException("Width and height must be positive integers.");
        }
        this.width = width;
        this.height = height;
    }

    /**
     * Définit le fichier de sortie pour l'image rendue.
     * @param output chemin/fichier de sortie
     */
    public void setOutputFile(String output) {
        this.output = output;
    }

    /** @param camera définit la caméra de la scène */
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    /** @param ambient couleur ambiante */
    public void setAmbient(Color ambient) {
        this.ambient = ambient;
    }

    /** @param shape ajoute une forme à la scène */
    public void addShape(Shape shape) {
        this.shapes.add(shape);
    }

    /** @param light ajoute une lumière à la scène */
    public void addLight(AbstractLight light) {
        this.lights.add(light);
    }

    /**
     * Définit la profondeur maximale de récursion pour réflexions.
     * @param maxRecursionDepth profondeur (entier)
     */
    public void addMaxRecursionDepth(int maxRecursionDepth) throws NumberFormatException {
        // if (maxRecursionDepth <= 0){
        //     throw new NumberFormatException("Max recursion depth must be a positive integer.");
        // }
        this.maxRecursionDepth = maxRecursionDepth;
    }


    /** @return largeur de la scène */
    public int getWidth() {
        return width;
    }
    /** @return hauteur de la scène */
    public int getHeight() {
        return height;
    }

    /** @return chemin/fichier de sortie */
    public String getOutputFile() {
        return output;
    }

    /** @return caméra de la scène */
    public Camera getCamera() {
        return camera;
    }

    /** @return couleur ambiante */
    public Color getAmbient() {
        return ambient;
    }

    /** @return profondeur max de récursion */
    public int getMaxRecursionDepth() {
        return maxRecursionDepth;
    }

    /** @return liste des formes de la scène */
    public List<Shape> getShapes() {
        return shapes;
    }

    /** @return liste des lumières de la scène */
    public List<AbstractLight> getLights() {
        return lights;
    }


    /** @return représentation textuelle multi-lignes de la scène */
    public String toString() {
        StringBuilder sb = new StringBuilder();
// StringBuilder permet de construire du texte progressivement sans créer une nouvelle chaîne à chaque concaténation
//On l'utilise ici car la description d'une scène contient plusieurs lignes et plusieurs éléments (caméra, lumières, formes...),
        sb.append("Scene [width=").append(width).append(", height=").append(height).append("]\n");
        sb.append("\tcamera= ").append(camera).append("\n");
        sb.append("\toutput= ").append(output).append("\n");
        sb.append("\tambient= ").append(ambient).append("\n");
        sb.append("\tlights :\n");
        for (AbstractLight light : lights) {
            sb.append("\t\t").append(light).append("\n");
        }
        sb.append("\tshapes :\n");
        for (Shape shape : shapes) {
            sb.append("\t\t").append(shape).append("\n");
        }
        return sb.toString();
    }
}
