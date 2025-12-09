package ray_tracer.renderer;

import ray_tracer.raytracer.Camera;
import ray_tracer.raytracer.Scene;

import java.awt.image.BufferedImage;

/**
 * Renderer API for async and sync rendering.
 */
public interface Renderer {
    RenderTask render(Scene scene, Camera camera, int width, int height, RenderOptions opts);
    BufferedImage renderSync(Scene scene, Camera camera, int width, int height, RenderOptions opts) throws RenderException;
}
