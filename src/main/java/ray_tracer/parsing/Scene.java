package ray_tracer.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ray_tracer.imaging.Color;
import ray_tracer.geometry.shapes.Shape;
import ray_tracer.geometry.shapes.Plane;
import ray_tracer.geometry.accel.BVHNode;
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
    private List<Shape> unboundedShapes = new ArrayList<>();
    // acceleration structure / dirty flag
    private boolean dirty = true;
    private BVHNode bvhRoot = null;

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

    public Optional<Intersection> intersect(Ray ray) {
        if (!ray.isRayValid()) {
            return Optional.empty();
        }
        // If acceleration structure is dirty, rebuild it lazily
        if (isDirty() || bvhRoot == null) {
            buildAcceleration();
        }
        Intersection closest = null;
        double minDist = Double.POSITIVE_INFINITY;

        // Query BVH for bounded shapes
        if (bvhRoot != null) {
            Optional<Intersection> opt = bvhRoot.intersect(ray);
            if (opt.isPresent()) {
                Intersection i = opt.get();
                closest = i;
                minDist = i.getDistance();
            }
        }

        // Always test unbounded shapes (e.g., infinite planes) separately
        for (Shape shape : unboundedShapes) {
            Optional<Intersection> opt = shape.intersect(ray);
            if (opt.isPresent()) {
                Intersection inter = opt.get();
                if (inter.getDistance() < minDist) {
                    minDist = inter.getDistance();
                    closest = inter;
                }
            }
        }

        // If no BVH was present (or it was empty), ensure we still test all shapes as fallback
        if (bvhRoot == null) {
            for (Shape shape : shapes) {
                Optional<Intersection> opt = shape.intersect(ray);
                if (opt.isPresent()) {
                    Intersection inter = opt.get();
                    if (inter.getDistance() < minDist) {
                        minDist = inter.getDistance();
                        closest = inter;
                    }
                }
            }
        }

        return (closest != null) ? Optional.of(closest) : Optional.empty();
    }

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

    public Color getTotalRecursionColorAt(Intersection intersection){
        // Compute the eye/view direction for this intersection: vector from the point to the camera
        Vector eyeDirection = intersection.getRay().getDirection().scalarMultiplication(-1).normalize();
        return getRecursionColorAt(intersection, eyeDirection, maxRecursionDepth);
    }

    public void addSize(int width, int height) throws NumberFormatException {
        if (width <= 0 || height <= 0){
            throw new NumberFormatException("Width and height must be positive integers.");
        }
        this.width = width;
        this.height = height;
    }

    public void setOutputFile(String output) {
        this.output = output;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setAmbient(Color ambient) {
        this.ambient = ambient;
    }

    public void addShape(Shape shape) {
        this.shapes.add(shape);
        if (shape instanceof Plane) {
            this.unboundedShapes.add(shape);
        } else {
            this.dirty = true;
        }
    }

    public void addLight(AbstractLight light) {
        this.lights.add(light);
    }

    public void addMaxRecursionDepth(int maxRecursionDepth) throws NumberFormatException {
        // if (maxRecursionDepth <= 0){
        //     throw new NumberFormatException("Max recursion depth must be a positive integer.");
        // }
        this.maxRecursionDepth = maxRecursionDepth;
    }


    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

    public String getOutputFile() {
        return output;
    }

    public Camera getCamera() {
        return camera;
    }

    public Color getAmbient() {
        return ambient;
    }

    public int getMaxRecursionDepth() {
        return maxRecursionDepth;
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    public List<AbstractLight> getLights() {
        return lights;
    }


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

    /**
     * Build or rebuild acceleration structures (BVH, etc.).
     * This is a no-op placeholder for now; implementations may build a real BVH.
     */
    public synchronized void buildAcceleration() {
        // Build a BVH from current shapes for faster intersection tests.
        try {
            List<Shape> bounded = new ArrayList<>();
            for (Shape s : this.shapes) {
                if (!(s instanceof Plane)) bounded.add(s);
            }
            this.bvhRoot = BVHNode.build(bounded);
        } catch (Exception e) {
            // Ensure we don't break rendering: keep bvhRoot null on failure
            this.bvhRoot = null;
        }
        this.dirty = false;
    }

    public synchronized boolean isDirty() {
        return dirty;
    }

    public synchronized void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    /**
     * Create a shallow copy of the scene suitable for use by a renderer. The shapes and
     * lights are shared (assumed read-mostly); the camera and primitives are copied where useful.
     */
    public Scene copyForRender() {
        Scene s = new Scene();
        s.width = this.width;
        s.height = this.height;
        s.output = this.output;
        s.ambient = this.ambient;
        s.lights = new ArrayList<>(this.lights);
        s.shapes = new ArrayList<>(this.shapes);
        s.unboundedShapes = new ArrayList<>(this.unboundedShapes);
        s.camera = (this.camera != null) ? this.camera.copy() : null;
        s.dirty = this.dirty;
        // Propagate the configured recursion depth so renderers respect scene maxdepth
        s.maxRecursionDepth = this.maxRecursionDepth;
        return s;
    }
}
