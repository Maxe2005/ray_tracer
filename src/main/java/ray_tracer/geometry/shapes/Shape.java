package ray_tracer.geometry.shapes;

import ray_tracer.imaging.Color;

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
