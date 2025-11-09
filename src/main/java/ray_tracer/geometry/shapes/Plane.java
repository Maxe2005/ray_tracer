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

    public Plane(Point point, Vector normal, Color diffuse, Color specular) {
        super(diffuse, specular);
        this.point = point;
        this.normal = normal;
    }

    @Override
    public Optional<Intersection> intersect(Ray ray) {
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
