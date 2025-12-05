package ray_tracer.geometry;

import ray_tracer.parsing.Camera;

public class Orthonormal {
    private Vector u;
    private Vector v;
    private Vector w;

    /**
     * Constructeur d'une base orthonormale explicite.
     * @param u vecteur U
     * @param v vecteur V
     * @param w vecteur W
     */
    public Orthonormal(Vector u, Vector v, Vector w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }

    /**
     * Construit une base orthonormale à partir d'une {@code Camera}.
     * @param camera caméra source
     * @return {@code Orthonormal} base
     */
    public static Orthonormal fromCamera (Camera camera) {
       Vector w = camera.getDirection().normalize();
        Vector up = camera.getUpDir().normalize();
        Vector u = up.vectorialProduct(w).normalize();
        Vector v = w.vectorialProduct(u).normalize();
        return new Orthonormal(u, v, w);
    }

    /** @return vecteur U de la base */
    public Vector getU() {
        return u;
    }

    /** @return vecteur V de la base */
    public Vector getV() {
        return v;
    }

    /** @return vecteur W de la base */
    public Vector getW() {
        return w;
    }
}
