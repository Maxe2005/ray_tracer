package ray_tracer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AbstractVec3Test {

    @Test
    public void defaultOperations_throwUnsupported() {
        AbstractVec3 v = new AbstractVec3(1.0, 2.0, 3.0) {
        };

        assertThrows(UnsupportedOperationException.class, () -> v.addition(v));
        assertThrows(UnsupportedOperationException.class, () -> v.subtraction(v));
        assertThrows(UnsupportedOperationException.class, () -> v.scalarMultiplication(2.0));
        assertThrows(UnsupportedOperationException.class, () -> v.scalarProduct(v));
        assertThrows(UnsupportedOperationException.class, () -> v.vectorialProduct(v));
        assertThrows(UnsupportedOperationException.class, () -> v.schurProduct(v));
        assertThrows(UnsupportedOperationException.class, () -> v.norm());
        assertThrows(UnsupportedOperationException.class, () -> v.normalize());
    }

    @Test
    public void equals_usesApproximation() {
        AbstractVec3 a = new AbstractVec3(1.0, 2.0, 3.0) {
        };
        AbstractVec3 b = new AbstractVec3(1.0 + 5e-10, 2.0, 3.0) {
        };

        assertTrue(a.equals(b));
    }

}
