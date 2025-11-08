package ray_tracer.raytracer;

import ray_tracer.parsing.Scene;
import ray_tracer.imaging.Color;

public class RayTracer {
    private double pixelHeight;
    private double pixelWidth;
    private final Scene scene;

    public RayTracer(Scene scene) {
        this.scene = scene;
    }

    public void setPixelsDimensions() {
        double aspectRatio = (double) scene.getWidth() / (double) scene.getHeight();
        double fovRadians = scene.getCamera().getRadiansFov();
        pixelHeight = Math.tan(fovRadians / 2.0);
        pixelWidth = pixelHeight * aspectRatio;
    }

    public Color getPixelColor(Ray ray) {
        return scene.getAmbient();
    }

    public double getPixelHeight() {
        return pixelHeight;
    }

    public double getPixelWidth() {
        return pixelWidth;
    }

}
