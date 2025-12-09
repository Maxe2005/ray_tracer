package ray_tracer.imaging;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ray_tracer.raytracer.Scene;
import ray_tracer.renderer.DefaultRenderer;
import ray_tracer.renderer.ImageUtils;
import ray_tracer.renderer.RenderException;
import ray_tracer.renderer.RenderOptions;
import ray_tracer.renderer.Renderer;
import ray_tracer.renderer.RenderTask;
import ray_tracer.renderer.ProgressListener;
import ray_tracer.renderer.RenderUpdate;

public class GenerateImage {
    // Backwards-compatible helpers retained below; rendering delegates to renderer.

    // Backwards-compatible default render (uses renderer defaults)
    public static void render(Scene scene) {
        render(scene, -1);
    }

    /**
     * Render the scene and write PNG file. If threadCount &gt; 0, override RenderOptions.threadCount.
     * If threadCount &lt;= 0, renderer defaults are used.
     */
    public static void render(Scene scene, int threadCount) {
        Renderer renderer = new DefaultRenderer();
        RenderOptions opts = new RenderOptions();
        if (threadCount > 0) {
            opts.threadCount = threadCount;
        }
        try {
            // Start async render so we can listen to progress
            RenderTask task = renderer.render(scene, scene.getCamera(), scene.getWidth(), scene.getHeight(), opts);
            task.addProgressListener(new ProgressListener() {
                @Override
                public void onUpdate(RenderUpdate update) {
                    double p = Math.max(0.0, Math.min(1.0, update.progressPercent));
                    System.out.print(String.format("\rRendering: %.1f%%", p * 100.0));
                    System.out.flush();
                }
            });

            BufferedImage img = null;
            try {
                img = task.getFuture().get();
                System.out.println("\rRendering: 100.0% - done");
            } catch (Exception e) {
                throw new RenderException("Render failed", e);
            }

            img = flipUpDown(img);

            // write directly to file
            Path outPath = Paths.get(scene.getOutputFile());
            try (OutputStream stream = Files.newOutputStream(outPath)) {
                ImageUtils.writePNG(img, stream);
            }
        } catch (RenderException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void renderSync(Scene scene) {
        Renderer renderer = new DefaultRenderer();
        RenderOptions opts = new RenderOptions();

        try {
            // Start async render so we can listen to progress
            BufferedImage img = renderer.renderSync(scene, scene.getCamera(), scene.getWidth(), scene.getHeight(), opts);

            img = flipUpDown(img);

            // write directly to file
            Path outPath = Paths.get(scene.getOutputFile());
            try (OutputStream stream = Files.newOutputStream(outPath)) {
                ImageUtils.writePNG(img, stream);
            }
        } catch (RenderException | IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage flipUpDown(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        BufferedImage flipped = new BufferedImage(width, height, img.getType());
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                flipped.setRGB(x, height - y - 1, img.getRGB(x, y));
            }
        }
        return flipped;
    }
}
