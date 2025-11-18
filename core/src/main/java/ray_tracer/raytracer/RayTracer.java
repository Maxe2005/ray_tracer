package ray_tracer.raytracer;

import ray_tracer.parsing.Scene;

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

    public double getPixelHeight() {
        return pixelHeight;
    }

    public double getPixelWidth() {
        return pixelWidth;
    }

}
