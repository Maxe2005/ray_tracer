package ray_tracer.parsing;

import java.util.ArrayList;
import java.util.List;

import ray_tracer.imaging.Color;
import ray_tracer.geometry.shapes.Shape;

public class Scene {
    private int width;
    private int height;
    private Camera camera;
    private String output = "output.png";
    private Color ambient = new Color();
    private List<AbstractLight> lights = new ArrayList<>();
    private List<Shape> shapes = new ArrayList<>();

    public boolean areLightsCorrect() {
        double totalRed = 0;
        double totalGreen = 0;
        double totalBlue = 0;
        for (AbstractLight light : lights) {
            if (!(light instanceof PointLight) && !(light instanceof DirectionalLight)) {
                return false;
            }
            Color color = light.getColor();
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
        return "Scene [width=" + width + ", height=" + height + "]\n\tcamera=" + camera + "\n\toutput=" + output
                + "\n\tambient=" + ambient + "\n\tlights=" + lights + "\n\tshapes=" + shapes + "\n";
    }
}
