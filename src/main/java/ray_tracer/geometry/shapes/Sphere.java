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

    /**
     * Constructeur d'une sphère centrée en (x,y,z).
     * @param x centre X
     * @param y centre Y
     * @param z centre Z
     * @param radius rayon
     * @param diffuse couleur diffuse
     * @param specular couleur spéculaire
     * @param shininess brillance
     */
    public Sphere(double x, double y, double z, double radius, Color diffuse, Color specular, int shininess) {
        super(diffuse, specular, shininess);
        this.center = new Point(x, y, z);
        this.radius = radius;
    }

    @Override
    /**
     * Calcule l'intersection entre un rayon et la sphère (solutions du polynôme quadratique).
     * @param ray rayon testé
     * @return {@code Optional<Intersection>} si intersection valide la plus proche
     */
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
            if (t < EPSILON) {
                return Optional.empty(); // Pas d'intersection
            }
            return Optional.of(new Intersection(ray, t, this));
        } else {
            // On calcule les deux solutions
            double t1 = (-b - Math.sqrt(discriminant)) / (2.0 * a);
            double t2 = (-b + Math.sqrt(discriminant)) / (2.0 * a);

            // On retourne l'intersection la plus proche
            double t = Math.min(t1, t2);
            if (t < EPSILON) {
                t = Math.max(t1, t2);
                if (t < EPSILON) {
                    return Optional.empty(); // Pas d'intersection
                }
            }
            // On retourne l'intersection
            return Optional.of(new Intersection(ray, t, this));
        }
    }

    @Override
    /**
     * Normal à la sphère en un point donné.
     * @param point point sur la surface
     * @return vecteur normal unitaire
     */
    public Vector getNormalAt(Point point) {
        return point.subtraction(center).normalize();
    }

    /** @return centre X */
    public double getX() {
        return center.getX();
    }

    /** @return centre Y */
    public double getY() {
        return center.getY();
    }

    /** @return centre Z */
    public double getZ() {
        return center.getZ();
    }

    /** @return rayon de la sphère */
    public double getRadius() {
        return radius;
    }

    @Override
    public String toString() {
        return "Sphere(center: " + center + ", radius: " + radius
                + ", diffuse: " + diffuse + ", specular: " + specular + ")";
    }

}
