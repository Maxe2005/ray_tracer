package ray_tracer.geometry.shapes;

import ray_tracer.imaging.Color;
import ray_tracer.geometry.Intersection;
import ray_tracer.raytracer.Ray;
import ray_tracer.geometry.Point;
import ray_tracer.geometry.Vector;

import java.util.Optional;

public abstract class Shape {
    protected static final double EPSILON = 1e-6;
    protected final Color diffuse;
    protected final Color specular;
    protected final int shininess;

    public Shape(Color diffuse, Color specular, int shininess) {
        if (diffuse == null) {
            this.diffuse = Color.BLACK;
        } else {
            this.diffuse = diffuse;
        }
        if (specular == null) {
            this.specular = Color.BLACK;
        } else {
            this.specular = specular;
        }
        this.shininess = shininess;
    }

    public Optional<Intersection> intersect(Ray ray) {
        return Optional.empty();
    }

    public Vector getNormalAt(Point point) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("getNormalAt method not implemented for this shape type.");
    }

    public Color getDiffuse() {
        return diffuse;
    }

    public Color getSpecular() {
        return specular;
    }

    public int getShininess() {
        return shininess;
    }

    public String toString() {
        return "Shape(diffuse: " + diffuse + ", specular: " + specular + ", shininess: " + shininess + ")";
    }
}
