package ray_tracer.geometry;

import ray_tracer.raytracer.Ray;
import ray_tracer.geometry.shapes.Shape;

public class Intersection {
    private final Ray ray;
    private final double distance;
    private final Shape shape;

    public Intersection(Ray ray, double distance, Shape shape) {
        this.ray = ray;
        this.distance = distance;
        this.shape = shape;
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

}
