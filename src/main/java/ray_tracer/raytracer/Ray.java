package ray_tracer.raytracer;

import ray_tracer.geometry.Orthonormal;
import ray_tracer.geometry.Point;
import ray_tracer.geometry.Vector;

public class Ray {
    private final Point origin;
    private Vector direction;

    /**
     * Constructeur complet d'un rayon avec direction normalisée.
     * @param origin origine du rayon
     * @param direction direction (sera normalisée)
     */
    public Ray(Point origin, Vector direction) {
        this.origin = origin;
        this.direction = direction.normalize();
    }

    /**
     * Constructeur partiel : crée un rayon sans direction définie.
     * @param origin origine du rayon
     */
    public Ray(Point origin) {
        this.origin = origin;
        this.direction = new Vector(0, 0, 0);
    }

    /**
     * Calcule et fixe la direction du rayon pour le pixel (i,j) en utilisant
     * la base caméra fournie.
     * @param basis base orthonormale de la caméra
     * @param i coordonnée x du pixel
     * @param j coordonnée y du pixel
     * @param pixelWidth largeur d'un pixel en unité caméra
     * @param pixelHeight hauteur d'un pixel en unité caméra
     * @param imageWidth largeur de l'image en pixels
     * @param imageHeight hauteur de l'image en pixels
     */
    public void setDirection(Orthonormal basis, int i, int j, double pixelWidth, double pixelHeight, int imageWidth, int imageHeight) {
        double a = (pixelWidth * (i - (imageWidth / 2.0) + 0.5)) / (imageWidth / 2.0);
        double b = (pixelHeight * (j - (imageHeight / 2.0) + 0.5)) / (imageHeight / 2.0);
        Vector dir = basis.getU().scalarMultiplication(a)
                .addition(basis.getV().scalarMultiplication(b))
                .subtraction(basis.getW()).normalize();
        this.direction = dir;
    }

    /**
     * Calcule le point situé à la distance donnée le long du rayon.
     * @param distance distance le long du rayon
     * @return {@code Point} correspondant
     */
    public Point getPointAtDistance(double distance) {
        return origin.addition(direction.scalarMultiplication(distance));
    }

    /** @return true si le rayon a une origine et une direction valides */
    public boolean isRayValid() {
        return origin != null && direction != null;
    }

    /** @return origine du rayon */
    public Point getOrigin() {
        return origin;
    }

    /** @return direction du rayon (vecteur) */
    public Vector getDirection() {
        return direction;
    }
}
