package ray_tracer.geometry;

import ray_tracer.raytracer.Ray;
import ray_tracer.geometry.shapes.Shape;

public class Intersection {
    private final Ray ray;
    private final double distance;
    private final Shape shape;
    private final Vector normal;
    private final Point intersectionPoint;

    /**
     * Crée une instance d'intersection calculée entre un rayon et une forme.
     * @param ray rayon ayant provoqué l'intersection
     * @param distance distance le long du rayon
     * @param shape forme touchée
     */
    public Intersection(Ray ray, double distance, Shape shape) {
        this.ray = ray;
        this.distance = distance;
        this.shape = shape;
        this.intersectionPoint = ray.getPointAtDistance(distance);
        this.normal = shape.getNormalAt(intersectionPoint);
    }

    /** @return rayon associé à l'intersection */
    public Ray getRay() {
        return ray;
    }

    /** @return distance le long du rayon vers l'intersection */
    public double getDistance() {
        return distance;
    }

    /** @return forme touchée */
    public Shape getShape() {
        return shape;
    }

    /** @return normale à la surface au point d'intersection */
    public Vector getNormal() {
        return normal;
    }

    /** @return point d'intersection dans l'espace */
    public Point getPoint() {
        return intersectionPoint;
    }

}