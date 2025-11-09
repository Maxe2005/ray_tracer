package ray_tracer.geometry.shapes;

import ray_tracer.imaging.Color;
import ray_tracer.geometry.Intersection;
import ray_tracer.raytracer.Ray;
import ray_tracer.geometry.Point;
import ray_tracer.geometry.Vector;

import java.util.Optional;

public abstract class Shape {
    protected final Color diffuse;
    protected final Color specular;

    public Shape(Color diffuse, Color specular) {
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

    public String toString() {
        return "Shape(diffuse: " + diffuse + ", specular: " + specular + ")";
    }
}
