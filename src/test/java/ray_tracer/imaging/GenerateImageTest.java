package ray_tracer.imaging;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class GenerateImageTest {

    @Test
    public void toBufferedImage_and_writeImage_create_valid_png() throws Exception {
        Image img = new Image(2,2);
        img.setPixelColor(0,0, new Color(1.0,0.0,0.0)); // red
        img.setPixelColor(1,0, new Color(0.0,1.0,0.0)); // green
        img.setPixelColor(0,1, new Color(0.0,0.0,1.0)); // blue
        img.setPixelColor(1,1, new Color(1.0,1.0,1.0)); // white

        // invoke private toBufferedImage via reflection
        Method toBuf = GenerateImage.class.getDeclaredMethod("toBufferedImage", Image.class);
        toBuf.setAccessible(true);
        BufferedImage bf = (BufferedImage) toBuf.invoke(null, img);

        assertNotNull(bf);
        assertEquals(2, bf.getWidth());
        assertEquals(2, bf.getHeight());

        // check RGB match
        assertEquals(0xFF0000, bf.getRGB(0,0) & 0xFFFFFF);
        assertEquals(0x00FF00, bf.getRGB(1,0) & 0xFFFFFF);
        assertEquals(0x0000FF, bf.getRGB(0,1) & 0xFFFFFF);
        assertEquals(0xFFFFFF, bf.getRGB(1,1) & 0xFFFFFF);

        // test writeImage writes a non-empty file
        Path tmp = Files.createTempFile("test-image", ".png");
        try {
            Method write = GenerateImage.class.getDeclaredMethod("writeImage", Image.class, String.class);
            write.setAccessible(true);
            write.invoke(null, img, tmp.toString());

            assertTrue(Files.exists(tmp));
            assertTrue(Files.size(tmp) > 0);
        } finally {
            Files.deleteIfExists(tmp);
        }
    }
}
