package ray_tracer.renderer;

import java.awt.image.BufferedImage;
import java.util.concurrent.Future;

/**
 * Represents an ongoing render. Allows cancellation and progress listening.
 */
public interface RenderTask {
    void cancel();
    boolean isDone();
    void addProgressListener(ProgressListener listener);
    Future<BufferedImage> getFuture();
}
