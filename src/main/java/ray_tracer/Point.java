package ray_tracer;

/** 3D point class */
public class Point extends AbstractVec3 {

    public Point(double x, double y, double z) {
        super(x, y, z);
    }

    @Override
    public AbstractVec3 subtraction(AbstractVec3 other) {
        return new Vector(this.getX() - other.getX(), this.getY() - other.getY(), this.getZ() - other.getZ());
    }

    @Override
    public AbstractVec3 scalarMultiplication(double scalar) {
        return new Point(this.getX() * scalar, this.getY() * scalar, this.getZ() * scalar);
    }

}
