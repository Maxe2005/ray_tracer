package ray_tracer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ColorTest {

    @Test
    public void defaultConstructor_black() {
        Color c = new Color();
        assertEquals(0.0, c.getR());
        assertEquals(0.0, c.getG());
        assertEquals(0.0, c.getB());
        assertEquals(0x000000, c.toRGB());
    }

    @Test
    public void addition_and_schur_and_scalarMultiplication_work() {
        Color a = new Color(0.2, 0.3, 0.4);
        Color b = new Color(0.1, 0.1, 0.1);

        Color sum = (Color) a.addition(b);
        assertTrue(DoubleComparisonUtil.approximatelyEqual(0.3, sum.getR()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(0.4, sum.getG()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(0.5, sum.getB()));

        Color schur = (Color) a.schurProduct(b);
        assertTrue(DoubleComparisonUtil.approximatelyEqual(0.02, schur.getR()));

        Color scaled = (Color) a.scalarMultiplication(2.0);
        assertTrue(DoubleComparisonUtil.approximatelyEqual(0.4, scaled.getR()));
    }

    @Test
    public void toRGB_rounding_and_bits() {
        Color red = new Color(1.0, 0.0, 0.0);
        assertEquals(0xFF0000, red.toRGB());

        Color mix = new Color(0.5, 0.5, 0.0);
        int rgb = mix.toRGB();
        // 0.5 * 255 = 127.5 -> rounded 128
        assertEquals((128 << 16) + (128 << 8) + 0, rgb);
    }

}
