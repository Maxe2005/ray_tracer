package ray_tracer.geometry.shapes;

import ray_tracer.geometry.Point;
import ray_tracer.geometry.Vector;
import ray_tracer.imaging.Color;

public class Plane extends Shape {
    private final Point point;
    private final Vector normal;

    public Plane(Point point, Vector normal, Color diffuse, Color specular) {
        super(diffuse, specular);
        this.point = point;
        this.normal = normal;
    }

    public Point getPoint() {
        return point;
    }

    public Vector getNormal() {
        return normal;
    }

    @Override
    public String toString() {
        return "Plane(point: " + point + ", normal: " + normal
                + ", diffuse: " + getDiffuse() + ", specular: " + getSpecular() + ")";
    }

}
