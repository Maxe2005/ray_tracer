package ray_tracer.imaging;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class GenerateImageTest {

    @Test
    public void testFlipUpDown() throws Exception {
        // Create a simple 2x2 image for testing
        BufferedImage img = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        img.setRGB(0, 0, 0xFF0000); // Top-left: Red
        img.setRGB(1, 0, 0x00FF00); // Top-right: Green
        img.setRGB(0, 1, 0x0000FF); // Bottom-left: Blue
        img.setRGB(1, 1, 0xFFFFFF); // Bottom-right: White

        // Use reflection to access the private flipUpDown method
        Method flipMethod = GenerateImage.class.getDeclaredMethod("flipUpDown", BufferedImage.class);
        flipMethod.setAccessible(true);
        BufferedImage flippedImg = (BufferedImage) flipMethod.invoke(null, img);

        // Verify that the image has been flipped correctly (mask out alpha)
        assertEquals(0x0000FF, flippedImg.getRGB(0, 0) & 0xFFFFFF); // New Top-left: Blue
        assertEquals(0xFFFFFF, flippedImg.getRGB(1, 0) & 0xFFFFFF); // New Top-right: White
        assertEquals(0xFF0000, flippedImg.getRGB(0, 1) & 0xFFFFFF); // New Bottom-left: Red
        assertEquals(0x00FF00, flippedImg.getRGB(1, 1) & 0xFFFFFF); // New Bottom-right: Green
    }
}
