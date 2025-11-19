package ray_tracer;

/** Abstract base class for 3D vectors */
public abstract class AbstractVec3 {
    protected final double x;
    protected final double y;
    protected final double z;

    public AbstractVec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Addition. Default: not supported.
     * Default implementation throws
     * UnsupportedOperationException so that only types that support
     * it override it.
     */
    public AbstractVec3 addition(AbstractVec3 other) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Addition not supported for this type.");
    }

    /**
     * Subtraction. Default: not supported.
     * Default implementation throws
     * UnsupportedOperationException so that only types that support
     * it override it.
     */
    public AbstractVec3 subtraction(AbstractVec3 other) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Subtraction not supported for this type.");
    }

    /**
     * Multiplication by a scalar. Default: not supported.
     * Default implementation throws
     * UnsupportedOperationException so that only types that support
     * it override it.
     */
    public AbstractVec3 scalarMultiplication(double scalar) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Scalar multiplication not supported for this type.");
    }

    /**
     * Scalar (dot) product. Default: not supported.
     * Default implementation throws
     * UnsupportedOperationException so that only types that support
     * it override it.
     */
    public double scalarProduct(AbstractVec3 other) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Scalar product not supported for this  type.");
    }

    /**
     * Vector (cross) product. Default: not supported.
     * Default implementation throws
     * UnsupportedOperationException so that only types that support
     * it override it.
     */
    public AbstractVec3 vectorialProduct(AbstractVec3 other) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Vectorial product not supported for this type.");
    }

    /**
     * Schur (Hadamard) product. Default implementation throws
     * UnsupportedOperationException so that only types that support
     * component-wise multiplication override it.
     */
    public AbstractVec3 schurProduct(AbstractVec3 other) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Schur product not supported for this  type.");
    }

    /**
     * Norm/length. Default: not supported.
     * Default implementation throws
     * UnsupportedOperationException so that only types that support
     * it override it.
     */
    public double norm() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Norm not supported for this type.");
    }

    /**
     * Normalization. Default: not supported.
     * Default implementation throws
     * UnsupportedOperationException so that only types that support
     * it override it.
     */
    public AbstractVec3 normalize() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Normalization not supported for this  type.");
    }

    /**
     * Equality check with approximate comparison for doubles.
     */
    public boolean equals(AbstractVec3 other) {
        return DoubleComparisonUtil.approximatelyEqual(this.getX(), other.getX()) &&
               DoubleComparisonUtil.approximatelyEqual(this.getY(), other.getY()) &&
               DoubleComparisonUtil.approximatelyEqual(this.getZ(), other.getZ());
    }

    protected double getX() {
        return x;
    }

    protected double getY() {
        return y;
    }

    protected double getZ() {
        return z;
    }
}
