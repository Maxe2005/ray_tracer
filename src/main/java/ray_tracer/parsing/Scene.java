package ray_tracer.parsing;

import java.util.ArrayList;
import java.util.List;

import ray_tracer.imaging.Color;
import ray_tracer.geometry.shapes.Shape;

public class Scene {
    public static final String DEFAULT_OUTPUT = "output.png";
    private int width;
    private int height;
    private Camera camera;
    private String output = DEFAULT_OUTPUT;
    private Color ambient = new Color();
    private List<AbstractLight> lights = new ArrayList<>();
    private List<Shape> shapes = new ArrayList<>();

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
    }

    public void addLight(AbstractLight light) {
        this.lights.add(light);
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
}
