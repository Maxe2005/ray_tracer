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

    /**
     * Constructeur d'un plan défini par un point et une normale (perpendiculaire au plan).
     * @param point point appartenant au plan
     * @param normal vecteur normal (sera normalisé)
     * @param diffuse couleur diffuse
     * @param specular couleur spéculaire
     * @param shininess brillance
     */
    public Plane(Point point, Vector normal, Color diffuse, Color specular, int shininess) {
        super(diffuse, specular, shininess);
        this.point = point;
        // Store normalized normal vector to ensure lighting calculations use unit normals.
        this.normal = normal.normalize();
    }

    @Override
    /**
     * Calcule l'intersection entre un rayon et le plan (si elle existe).
     * @param ray rayon testé
     * @return {@code Optional<Intersection>} contenant l'intersection la plus proche si existante
     */
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

    /** @return point appartenant au plan */
    public Point getPoint() {
        return point;
    }

    /** @return normale (unit) du plan */
    public Vector getNormal() {
        return normal;
    }

    @Override
    /**
     * Normal au plan (constante quel que soit le point).
     * @param point point sur le plan (ignoré)
     * @return vecteur normal unitaire
     */
    public Vector getNormalAt(Point point) {
        return normal;
    }

    @Override
    public String toString() {
        return "Plane(point: " + point + ", normal: " + normal
                + ", diffuse: " + getDiffuse() + ", specular: " + getSpecular() + ")";
    }

}
