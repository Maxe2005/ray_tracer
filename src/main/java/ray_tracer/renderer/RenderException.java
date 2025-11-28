package ray_tracer.renderer;

/**
 * Exception thrown during rendering.
 */
public class RenderException extends Exception {
    public RenderException(String msg) { super(msg); }
    public RenderException(String msg, Throwable t) { super(msg, t); }
}
