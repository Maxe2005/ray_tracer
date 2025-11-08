package ray_tracer.geometry;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DoubleComparisonUtilTest {

    @Test
    public void approximatelyEqual_behaviour() {
        assertTrue(DoubleComparisonUtil.approximatelyEqual(1.0, 1.0 + 1e-12));
        assertFalse(DoubleComparisonUtil.approximatelyEqual(1.0, 1.1));
    }

}
