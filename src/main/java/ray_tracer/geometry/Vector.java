package ray_tracer.geometry;

/** 3D vector class */
public class Vector extends AbstractVec3 {

    /**
     * Constructeur vecteur 3D.
     * @param x composante X
     * @param y composante Y
     * @param z composante Z
     */
    public Vector(double x, double y, double z) {
        super(x, y, z);
    }

    @Override
    /**
     * Addition vectorielle.
     * @param other vecteur à ajouter
     * @return nouveau vecteur somme
     */
    public Vector addition(AbstractVec3 other) {
        Vector o = (Vector) other;
        return new Vector(this.getX() + o.getX(), this.getY() + o.getY(), this.getZ() + o.getZ());
    }

    @Override
    /**
     * Soustraction vectorielle.
     * @param other vecteur à soustraire
     * @return nouveau vecteur
     */
    public Vector subtraction(AbstractVec3 other) {
        Vector o = (Vector) other;
        return new Vector(this.getX() - o.getX(), this.getY() - o.getY(), this.getZ() - o.getZ());
    }

    @Override
    /**
     * Multiplication par scalaire.
     * @param scalar facteur
     * @return nouveau vecteur
     */
    public Vector scalarMultiplication(double scalar) {
        return new Vector(this.getX() * scalar, this.getY() * scalar, this.getZ() * scalar);
    }

    @Override
    /**
     * Produit scalaire (dot product).
     * @param other autre vecteur
     * @return scalaire résultat
     */
    public double scalarProduct(AbstractVec3 other) {
        Vector o = (Vector) other;
        return this.getX() * o.getX() + this.getY() * o.getY() + this.getZ() * o.getZ();
    }

    @Override
    /**
     * Produit vectoriel (cross product).
     * @param other autre vecteur
     * @return vecteur résultat
     */
    public Vector vectorialProduct(AbstractVec3 other) {
        Vector o = (Vector) other;
        return new Vector(
            this.getY() * o.getZ() - this.getZ() * o.getY(),
            this.getZ() * o.getX() - this.getX() * o.getZ(),
            this.getX() * o.getY() - this.getY() * o.getX()
        );
    }

    @Override
    /** @return norme (longueur) du vecteur */
    public double norm() {
        return Math.sqrt(this.getX() * this.getX() + this.getY() * this.getY() + this.getZ() * this.getZ());
    }

    @Override
    /**
     * Normalise le vecteur.
     * @return vecteur unitaire
     * @throws ArithmeticException si vecteur nul
     */
    public Vector normalize() throws ArithmeticException {
        double norm = this.norm();
        if (norm == 0) {
            throw new ArithmeticException("Cannot normalize a zero vector.");
        }
        return new Vector(this.getX() / norm, this.getY() / norm, this.getZ() / norm);
    }

    /** @return représentation textuelle du vecteur */
    public String toString() {
        return "Vector(" + getX() + ", " + getY() + ", " + getZ() + ")";
    }


}
