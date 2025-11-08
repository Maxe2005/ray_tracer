package ray_tracer.raytracer;

import ray_tracer.geometry.Orthonormal;
import ray_tracer.geometry.Point;
import ray_tracer.geometry.Vector;

public class Ray {
    private final Point origin;
    private Vector direction;

    public Ray(Point origin, Vector direction) {
        this.origin = origin;
        this.direction = direction;
    }

    public Ray(Point origin) {
        this.origin = origin;
        this.direction = new Vector(0, 0, 0);
    }

    public void setDirection(Orthonormal basis, int i, int j, double pixelWidth, double pixelHeight, int imageWidth, int imageHeight) {
        double a = (pixelWidth * (i - (imageWidth / 2.0) + 0.5)) / (imageWidth / 2.0);
        double b = (pixelHeight * (j - (imageHeight / 2.0) + 0.5)) / (imageHeight / 2.0);
        Vector dir = basis.getU().scalarMultiplication(a)
                .addition(basis.getV().scalarMultiplication(b))
                .subtraction(basis.getW()).normalize();
        this.direction = dir;
    }

    public Point getOrigin() {
        return origin;
    }

    public Vector getDirection() {
        return direction;
    }
}
