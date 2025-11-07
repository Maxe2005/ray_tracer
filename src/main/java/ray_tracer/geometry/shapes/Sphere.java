package ray_tracer.geometry.shapes;

import ray_tracer.imaging.Color;
import ray_tracer.geometry.Point;

public class Sphere extends Shape {
    private final Point center;
    private final double radius;

    public Sphere(double x, double y, double z, double radius, Color diffuse, Color specular) {
        super(diffuse, specular);
        this.center = new Point(x, y, z);
        this.radius = radius;
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
