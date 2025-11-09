package ray_tracer.geometry;

/** 3D point class */
public class Point extends AbstractVec3 {

    public Point(double x, double y, double z) {
        super(x, y, z);
    }

    // Pas demandé dans le jalon 1 mais utile pour le jalon 4 : calcul du point d'intersection
    @Override
    public Point addition(AbstractVec3 other) {
        return new Point(this.getX() + other.getX(), this.getY() + other.getY(), this.getZ() + other.getZ());
    }

    @Override
    public Vector subtraction(AbstractVec3 other) {
        return new Vector(this.getX() - other.getX(), this.getY() - other.getY(), this.getZ() - other.getZ());
    }

    @Override
    public Point scalarMultiplication(double scalar) {
        return new Point(this.getX() * scalar, this.getY() * scalar, this.getZ() * scalar);
    }

    public String toString() {
        return "Point(" + getX() + ", " + getY() + ", " + getZ() + ")";
    }

}
