package ray_tracer.geometry.shapes;

import java.util.Optional;

import ray_tracer.geometry.Intersection;
import ray_tracer.geometry.Point;
import ray_tracer.imaging.Color;
import ray_tracer.raytracer.Ray;
import ray_tracer.geometry.Vector;

public class Triangle extends Shape {
    private final Point a;
    private final Point b;
    private final Point c;

    public Triangle(Point v0, Point v1, Point v2, Color diffuse, Color specular, int shininess) {
        super(diffuse, specular, shininess);
        this.a = v0;
        this.b = v1;
        this.c = v2;
    }

    @Override
    public Optional<Intersection> intersect(Ray ray) {
        Vector edge1 = b.subtraction(a);
        Vector edge2 = c.subtraction(a);
        Vector p = ray.getDirection().vectorialProduct(edge2);
        double det = edge1.scalarProduct(p);
        if (Math.abs(det) < EPSILON) {
            return Optional.empty(); // Pas d'intersection
        }
        double invDet = 1.0 / det;
        Vector tv = ray.getOrigin().subtraction(a);
        double beta = invDet * tv.scalarProduct(p);
        if (beta < 0 || beta > 1) {
            return Optional.empty(); // Pas d'intersection
        }
        Vector q = tv.vectorialProduct(edge1);
        double gama = invDet * ray.getDirection().scalarProduct(q);
        if (gama < 0 || beta + gama > 1) {
            return Optional.empty(); // Pas d'intersection
        }
        double t = invDet * edge2.scalarProduct(q);
        if (t < EPSILON) {
            return Optional.empty(); // Pas d'intersection
        }
        return Optional.of(new Intersection(ray, t, this));
    }

    public Point getV0() {
        return a;
    }

    public Point getV1() {
        return b;
    }

    public Point getV2() {
        return c;
    }

    @Override
    public Vector getNormalAt(Point point) {
        Vector edge1 = b.subtraction(a);
        Vector edge2 = c.subtraction(a);
        return edge1.vectorialProduct(edge2).normalize();
    }

    @Override
    public String toString() {
        return "Triangle(a: " + a + ", b: " + b + ", c: " + c
                + ", diffuse: " + getDiffuse() + ", specular: " + getSpecular() + ")";
    }


}
