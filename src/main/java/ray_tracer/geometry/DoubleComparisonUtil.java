package ray_tracer.geometry;

public class DoubleComparisonUtil {
    // Tolérance utilisée pour décider si deux doubles sont égaux
    private static final double EPSILON = 1e-9;

    public static boolean approximatelyEqual(double num1, double num2) {
        return Math.abs(num1 - num2) < EPSILON;
    }
}
