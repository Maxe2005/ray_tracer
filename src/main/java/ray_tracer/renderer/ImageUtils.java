package ray_tracer.renderer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

/**
 * Utility helpers for image encoding and streams.
 */
public final class ImageUtils {
    private ImageUtils() {}

    public static byte[] toPNGBytes(BufferedImage img) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(img, "png", baos);
            return baos.toByteArray();
        }
    }

    public static void writePNG(BufferedImage img, OutputStream out) throws IOException {
        ImageIO.write(img, "png", out);
    }
}
