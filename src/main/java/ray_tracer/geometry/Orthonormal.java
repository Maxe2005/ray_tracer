package ray_tracer.geometry;

import ray_tracer.parsing.Camera;

public class Orthonormal {
    private Vector u;
    private Vector v;
    private Vector w;

    public Orthonormal(Vector u, Vector v, Vector w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }

    public static Orthonormal fromCamera (Camera camera) {
       Vector w = camera.getDirection().smul(-1).normalize();
        Vector up = camera.getUpDir().normalize();
        Vector u = up.vectorialProduct(w).normalize();
        Vector v = w.vectorialProduct(u).normalize();
        return new Orthonormal(u, v, w);
    }

    public Vector getU() {
        return u;
    }

    public Vector getV() {
        return v;
    }

    public Vector getW() {
        return w;
    }
}
