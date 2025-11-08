package ray_tracer.imaging;

import org.junit.jupiter.api.Test;

import ray_tracer.geometry.DoubleComparisonUtil;
import ray_tracer.geometry.AbstractVec3;

import static org.junit.jupiter.api.Assertions.*;

public class ColorTest {

    @Test
    public void clamp_and_getters_and_toRGB() {
        Color c = new Color(1.5, -0.2, 0.5);
        // values should be clamped to [0,1]
        assertTrue(DoubleComparisonUtil.approximatelyEqual(1.0, c.getR()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(0.0, c.getG()));
        assertTrue(DoubleComparisonUtil.approximatelyEqual(0.5, c.getB()));

        int rgb = c.toRGB();
        // check that components are packed: red is 255
        int red = (rgb >> 16) & 0xff;
        assertEquals(255, red);
    }

    @Test
    public void addition_and_scalar_and_schur() {
        Color a = new Color(0.2, 0.3, 0.4);
        Color b = new Color(0.1, 0.2, 0.3);

        AbstractVec3 sum = a.addition(b);
        assertTrue(sum instanceof Color);
        assertTrue(DoubleComparisonUtil.approximatelyEqual(0.3, ((Color) sum).getR()));

        AbstractVec3 scaled = a.scalarMultiplication(2.0);
        assertTrue(DoubleComparisonUtil.approximatelyEqual(0.4, ((Color) scaled).getR()));

        AbstractVec3 schur = a.schurProduct(b);
        assertTrue(DoubleComparisonUtil.approximatelyEqual(0.02, ((Color) schur).getR()));
    }

}
