package ray_tracer.imaging;

import ray_tracer.parsing.Scene;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import ray_tracer.renderer.DefaultRenderer;
import ray_tracer.renderer.ImageUtils;
import ray_tracer.renderer.RenderException;
import ray_tracer.renderer.RenderOptions;
import ray_tracer.renderer.Renderer;
import ray_tracer.renderer.RenderTask;
import ray_tracer.renderer.ProgressListener;
import ray_tracer.renderer.RenderUpdate;
import javax.imageio.ImageIO;

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

            // write directly to file
            Path outPath = Paths.get(scene.getOutputFile());
            try (OutputStream stream = Files.newOutputStream(outPath)) {
                ImageUtils.writePNG(img, stream);
            }
        } catch (RenderException | IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage toBufferedImage(Image image) {
        BufferedImage buffer = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = image.getPixelColor(x, y);
                buffer.setRGB(x, y, color.toRGB());
            }
        }
        return buffer;
    }

    private static void writeImage(Image image, String name) {
        Path outPath = Paths.get(name);
        try (OutputStream stream = Files.newOutputStream(outPath)) {
            ImageIO.write(toBufferedImage(image), "png", stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
