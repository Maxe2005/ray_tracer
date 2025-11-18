package ray_tracer.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ray_tracer.imaging.Color;
import ray_tracer.geometry.shapes.Shape;
import ray_tracer.geometry.Intersection;
import ray_tracer.raytracer.Ray;

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

    public Color getTotalColorAt(Intersection intersection){
        Color totalLight = ambient;
        for (AbstractLight light : lights) {
            Ray shadowRay = new Ray(intersection.getPoint(), light.getDirectionFrom(intersection.getPoint()));
            Optional<Intersection> ombreIntersection = intersect(shadowRay);
            if (!ombreIntersection.isPresent()){//} || ombreIntersection.get().getDistance() < 1e-9) {
                totalLight = totalLight.addition(light.getColorAt(intersection, camera.getDirection()));
            }
        }
        return totalLight;
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
