package ray_tracer.renderer;

import ray_tracer.parsing.Scene;
import ray_tracer.parsing.Camera;
import java.awt.image.BufferedImage;

/**
 * Renderer API for async and sync rendering.
 */
public interface Renderer {
    RenderTask render(Scene scene, Camera camera, int width, int height, RenderOptions opts);
    BufferedImage renderSync(Scene scene, Camera camera, int width, int height, RenderOptions opts) throws RenderException;
}
