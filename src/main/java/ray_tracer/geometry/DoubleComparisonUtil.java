package ray_tracer.geometry;

public class DoubleComparisonUtil {
    // Tolérance utilisée pour décider si deux doubles sont égaux
    private static final double EPSILON = 1e-9;

    /**
     * Compare deux doubles en tenant compte d'une tolérance fixe (EPSILON).
     * @param num1 premier nombre
     * @param num2 second nombre
     * @return true si |num1 - num2| < EPSILON
     */
    public static boolean approximatelyEqual(double num1, double num2) {
        return Math.abs(num1 - num2) < EPSILON;
    }
}
