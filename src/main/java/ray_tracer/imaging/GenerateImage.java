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
import javax.imageio.ImageIO;

public class GenerateImage {
    // Backwards-compatible helpers retained below; rendering delegates to renderer.

    public static void render (Scene scene){
        Renderer renderer = new DefaultRenderer();
        RenderOptions opts = new RenderOptions();
        try {
            BufferedImage img = renderer.renderSync(scene, scene.getCamera(), scene.getWidth(), scene.getHeight(), opts);
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
