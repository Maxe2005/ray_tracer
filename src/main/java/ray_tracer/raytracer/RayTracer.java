package ray_tracer.raytracer;

import ray_tracer.parsing.Scene;

public class RayTracer {
    private double pixelHeight;
    private double pixelWidth;
    private final Scene scene;

    /**
     * Initialise le ray tracer pour une scène donnée.
     * @param scene scène à rendre
     */
    public RayTracer(Scene scene) {
        this.scene = scene;
    }
    /**
     * Calcule les dimensions logiques d'un pixel en unité caméra (pixelWidth/pixelHeight)
     * à partir de la caméra et de la taille de la scène.
     */
    public void setPixelsDimensions() {
        double aspectRatio = (double) scene.getWidth() / (double) scene.getHeight();
        double fovRadians = scene.getCamera().getRadiansFov();
        pixelHeight = Math.tan(fovRadians / 2.0);
        pixelWidth = pixelHeight * aspectRatio;
    }

    /** @return hauteur d'un pixel en unité caméra */
    public double getPixelHeight() {
        return pixelHeight;
    }

    /** @return largeur d'un pixel en unité caméra */
    public double getPixelWidth() {
        return pixelWidth;
    }

}
