package ray_tracer.imaging;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ImageTest {

    @Test
    public void constructor_and_getters_and_default_color() {
        Image img = new Image(3, 2);
        assertEquals(3, img.getWidth());
        assertEquals(2, img.getHeight());

        // default pixels should be black
        assertEquals(Color.BLACK, img.getPixelColor(0,0));
        assertEquals(Color.BLACK, img.getPixelColor(2,1));
    }

    @Test
    public void set_and_get_pixel_color() {
        Image img = new Image(2, 2);
        img.setPixelColor(1, 0, new Color(1.0, 0.0, 0.0));
        assertEquals(0xFF0000, img.getPixelColor(1,0).toRGB());
    }

    @Test
    public void out_of_bounds_get_throws() {
        Image img = new Image(2, 2);
        assertThrows(IllegalArgumentException.class, () -> img.getPixelColor(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> img.getPixelColor(0, -1));
        assertThrows(IllegalArgumentException.class, () -> img.getPixelColor(2, 0));
        assertThrows(IllegalArgumentException.class, () -> img.getPixelColor(0, 2));
    }

    @Test
    public void out_of_bounds_set_throws() {
        Image img = new Image(2, 2);
        assertThrows(IllegalArgumentException.class, () -> img.setPixelColor(-1, 0, Color.BLACK));
        assertThrows(IllegalArgumentException.class, () -> img.setPixelColor(0, -1, Color.BLACK));
        assertThrows(IllegalArgumentException.class, () -> img.setPixelColor(2, 0, Color.BLACK));
        assertThrows(IllegalArgumentException.class, () -> img.setPixelColor(0, 2, Color.BLACK));
    }

    @Test
    public void flip_up_down_reverses_rows() {
        Image img = new Image(2, 3);
        // set distinct colors per row
        img.setPixelColor(0, 0, new Color(1.0, 0.0, 0.0)); // row 0
        img.setPixelColor(0, 1, new Color(0.0, 1.0, 0.0)); // row 1
        img.setPixelColor(0, 2, new Color(0.0, 0.0, 1.0)); // row 2

        img.flipUpDown();

        // after flip, original row 0 should be at row 2
        assertEquals(0xFF0000, img.getPixelColor(0, 2).toRGB());
        assertEquals(0x00FF00, img.getPixelColor(0, 1).toRGB());
        assertEquals(0x0000FF, img.getPixelColor(0, 0).toRGB());
    }

}
