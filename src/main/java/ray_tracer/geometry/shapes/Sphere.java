package ray_tracer.geometry.shapes;

import ray_tracer.imaging.Color;
import ray_tracer.geometry.Point;
import ray_tracer.geometry.Intersection;
import ray_tracer.raytracer.Ray;
import ray_tracer.geometry.Vector;

import java.util.Optional;

public class Sphere extends Shape {
    private final Point center;
    private final double radius;

    public Sphere(double x, double y, double z, double radius, Color diffuse, Color specular) {
        super(diffuse, specular);
        this.center = new Point(x, y, z);
        this.radius = radius;
    }

    @Override
    public Optional<Intersection> intersect(Ray ray) {
        // On résout l'équation du second degré pour trouver les intersections
        // a*t² + b*t + c = 0 avec :
        Vector oc = ray.getOrigin().subtraction(center);
        double a = ray.getDirection().scalarProduct(ray.getDirection());
        double b = 2.0 * oc.scalarProduct(ray.getDirection());
        double c = oc.scalarProduct(oc) - radius * radius;

        // On résout le discriminant
        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) {
            return Optional.empty(); // Pas d'intersection
        } else if (discriminant == 0) {
            // Une seule intersection
            double t = -b / (2.0 * a);
            if (t < 0) {
                return Optional.empty(); // Pas d'intersection
            }
            return Optional.of(new Intersection(ray, t, this));
        } else {
            // On calcule les deux solutions
            double t1 = (-b - Math.sqrt(discriminant)) / (2.0 * a);
            double t2 = (-b + Math.sqrt(discriminant)) / (2.0 * a);

            // On retourne l'intersection la plus proche
            double t = Math.min(t1, t2);
            if (t < 0) {
                t = Math.max(t1, t2);
                if (t < 0) {
                    return Optional.empty(); // Pas d'intersection
                }
            }
            // On retourne l'intersection
            return Optional.of(new Intersection(ray, t, this));
        }
    }

    public double getX() {
        return center.getX();
    }

    public double getY() {
        return center.getY();
    }

    public double getZ() {
        return center.getZ();
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return "Sphere(center: " + center + ", radius: " + radius
                + ", diffuse: " + diffuse + ", specular: " + specular + ")";
    }

}
