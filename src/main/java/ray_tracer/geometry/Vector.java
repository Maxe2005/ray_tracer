package ray_tracer.geometry;

/** 3D vector class */
public class Vector extends AbstractVec3 {

    public Vector(double x, double y, double z) {
        super(x, y, z);
    }

    @Override
    public Vector addition(AbstractVec3 other) {
        Vector o = (Vector) other;
        return new Vector(this.getX() + o.getX(), this.getY() + o.getY(), this.getZ() + o.getZ());
    }

    @Override
    public Vector subtraction(AbstractVec3 other) {
        Vector o = (Vector) other;
        return new Vector(this.getX() - o.getX(), this.getY() - o.getY(), this.getZ() - o.getZ());
    }

    @Override
    public Vector scalarMultiplication(double scalar) {
        return new Vector(this.getX() * scalar, this.getY() * scalar, this.getZ() * scalar);
    }

    @Override
    public double scalarProduct(AbstractVec3 other) {
        Vector o = (Vector) other;
        return this.getX() * o.getX() + this.getY() * o.getY() + this.getZ() * o.getZ();
    }

    @Override
    public Vector vectorialProduct(AbstractVec3 other) {
        Vector o = (Vector) other;
        return new Vector(
            this.getY() * o.getZ() - this.getZ() * o.getY(),
            this.getZ() * o.getX() - this.getX() * o.getZ(),
            this.getX() * o.getY() - this.getY() * o.getX()
        );
    }

    @Override
    public double norm() {
        return Math.sqrt(this.getX() * this.getX() + this.getY() * this.getY() + this.getZ() * this.getZ());
    }
// Normalise le vecteur : conserve sa direction mais ramène sa longueur à 1 (Impossible pour le vecteur nul car cela nécessiterait une division par zéro)
    @Override
    public Vector normalize() throws ArithmeticException {
        double norm = this.norm();
        if (norm == 0) {
            throw new ArithmeticException("Cannot normalize a zero vector.");
        }
        return new Vector(this.getX() / norm, this.getY() / norm, this.getZ() / norm);
    }

    public String toString() {
        return "Vector(" + getX() + ", " + getY() + ", " + getZ() + ")";
    }


}
