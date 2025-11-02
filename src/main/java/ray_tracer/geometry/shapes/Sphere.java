package ray_tracer.geometry.shapes;

import ray_tracer.imaging.Color;

public class Sphere extends Shape {
    private final double x;
    private final double y;
    private final double z;
    private final double radius;

    public Sphere(double x, double y, double z, double radius, Color diffuse, Color specular) {
        super(diffuse, specular);
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
    }

    @Override
    public Color getDiffuse() {
        return diffuse;
    }

    @Override
    public Color getSpecular() {
        return specular;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double getRadius() {
        return radius;
    }

}
