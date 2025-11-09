package ray_tracer.geometry;

import ray_tracer.raytracer.Ray;
import ray_tracer.geometry.shapes.Shape;

public class Intersection {
    private final Ray ray;
    private final double distance;
    private final Shape shape;
    private final Vector normal;
    private final Point intersectionPoint;

    public Intersection(Ray ray, double distance, Shape shape) {
        this.ray = ray;
        this.distance = distance;
        this.shape = shape;
        this.intersectionPoint = ray.getPointAtDistance(distance);
        this.normal = shape.getNormalAt(intersectionPoint);
    }

    public Ray getRay() {
        return ray;
    }

    public double getDistance() {
        return distance;
    }

    public Shape getShape() {
        return shape;
    }

    public Vector getNormal() {
        return normal;
    }

    public Point getPoint() {
        return intersectionPoint;
    }

}