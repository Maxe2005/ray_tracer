package ray_tracer.renderer;

/**
 * Listener for render progress updates.
 */
public interface ProgressListener {
    void onUpdate(RenderUpdate update);
}
