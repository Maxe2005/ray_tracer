package ray_tracer.geometry.shapes;

import java.util.Optional;

import ray_tracer.geometry.Intersection;
import ray_tracer.geometry.Point;
import ray_tracer.geometry.Vector;
import ray_tracer.imaging.Color;
import ray_tracer.raytracer.Ray;

public class Plane extends Shape {
    private final Point point;
    private final Vector normal;

    public Plane(Point point, Vector normal, Color diffuse, Color specular, int shininess) {
        super(diffuse, specular, shininess);
        this.point = point;
        this.normal = normal.normalize();
    }

    @Override
    public Optional<Intersection> intersect(Ray ray) {
        double denom = normal.scalarProduct(ray.getDirection());
        if (Math.abs(denom) > EPSILON) {
            Vector p0l0 = point.subtraction(ray.getOrigin());
            double t = p0l0.scalarProduct(normal) / denom;
            if (t >= EPSILON) {
                return Optional.of(new Intersection(ray, t, this));
            }
        }
        return Optional.empty();
    }

    public Point getPoint() {
        return point;
    }

    public Vector getNormal() {
        return normal;
    }

    @Override
    public Vector getNormalAt(Point point) {
        return normal;
    }

    @Override
    public String toString() {
        return "Plane(point: " + point + ", normal: " + normal
                + ", diffuse: " + getDiffuse() + ", specular: " + getSpecular() + ")";
    }

}
