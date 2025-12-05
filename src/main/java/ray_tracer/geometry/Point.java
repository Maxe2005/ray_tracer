package ray_tracer.geometry;

/** 3D point class */
public class Point extends AbstractVec3 {

    /**
     * Constructeur point 3D.
     * @param x coordonnée X
     * @param y coordonnée Y
     * @param z coordonnée Z
     */
    public Point(double x, double y, double z) {
        super(x, y, z);
    }

    // Pas demandé dans le jalon 1 mais utile pour le jalon 4 : calcul du point d'intersection
    @Override
    /**
     * Ajoute un vecteur au point et retourne un nouveau point.
     * @param other vecteur à ajouter
     * @return nouveau {@code Point}
     */
    public Point addition(AbstractVec3 other) {
        return new Point(this.getX() + other.getX(), this.getY() + other.getY(), this.getZ() + other.getZ());
    }

    @Override
    /**
     * Soustrait un vecteur du point et retourne un {@code Vector}.
     * @param other vecteur à soustraire
     * @return {@code Vector} résultant
     */
    public Vector subtraction(AbstractVec3 other) {
        return new Vector(this.getX() - other.getX(), this.getY() - other.getY(), this.getZ() - other.getZ());
    }

    @Override
    /**
     * Multiplie les coordonnées du point par un scalaire.
     * @param scalar facteur
     * @return nouveau {@code Point}
     */
    public Point scalarMultiplication(double scalar) {
        return new Point(this.getX() * scalar, this.getY() * scalar, this.getZ() * scalar);
    }

    /** @return représentation textuelle du point */
    public String toString() {
        return "Point(" + getX() + ", " + getY() + ", " + getZ() + ")";
    }

}
