package ray_tracer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DoubleComparisonUtilTest {

    @Test
    public void approximatelyEqual_whenClose_returnsTrue() {
        assertTrue(DoubleComparisonUtil.approximatelyEqual(1.0, 1.0 + 5e-10));
    }

    @Test
    public void approximatelyEqual_whenFar_returnsFalse() {
        assertFalse(DoubleComparisonUtil.approximatelyEqual(1.0, 1.0 + 2e-9));
    }

}
