package ray_tracer.geometry.shapes;

import ray_tracer.geometry.Point;
import ray_tracer.imaging.Color;

public class Triangle extends Shape {
    private final Point v0;
    private final Point v1;
    private final Point v2;

    public Triangle(Point v0, Point v1, Point v2, Color diffuse, Color specular) {
        super(diffuse, specular);
        this.v0 = v0;
        this.v1 = v1;
        this.v2 = v2;
    }

    public Point getV0() {
        return v0;
    }

    public Point getV1() {
        return v1;
    }

    public Point getV2() {
        return v2;
    }

    @Override
    public String toString() {
        return "Triangle(v0: " + v0 + ", v1: " + v1 + ", v2: " + v2
                + ", diffuse: " + getDiffuse() + ", specular: " + getSpecular() + ")";
    }


}
