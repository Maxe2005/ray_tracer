package ray_tracer.renderer;

/**
 * Options controlling rendering quality and performance.
 */
public class RenderOptions {
    public int samplesPerPixel = 1;
    public int maxDepth = 5;
    public int tileSize = 32;
    public int threadCount = Runtime.getRuntime().availableProcessors();
    public boolean enableDenoiser = false;
    public double lowResFactor = 1.0; // 0.25..1.0
    public boolean progressive = false;
    public boolean prioritizeCenter = false;

    public RenderOptions() {}
}
