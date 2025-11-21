package ray_tracer.geometry.shapes;

import ray_tracer.imaging.Color;

public abstract class Shape {
// la couleur principale de l'objet
    protected final Color diffuse;
// effet miroir / brillance
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
 // toString() convertit l'objet en texte lisible.
    public String toString() {
        return "Shape(diffuse: " + diffuse + ", specular: " + specular + ")";
    }
}
