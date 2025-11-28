package ray_tracer.renderer;

import java.awt.image.BufferedImage;

/**
 * Represents a partial update emitted by the renderer (tile or progressive pass).
 */
public class RenderUpdate {
    public final BufferedImage imagePart;
    public final int x;
    public final int y;
    public final int width;
    public final int height;
    public final double progressPercent;

    public RenderUpdate(BufferedImage imagePart, int x, int y, int width, int height, double progressPercent) {
        this.imagePart = imagePart;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.progressPercent = progressPercent;
    }
}
