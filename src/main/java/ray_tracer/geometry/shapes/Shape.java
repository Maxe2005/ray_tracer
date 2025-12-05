package ray_tracer.geometry.shapes;

import ray_tracer.imaging.Color;
import ray_tracer.geometry.Intersection;
import ray_tracer.raytracer.Ray;
import ray_tracer.geometry.Point;
import ray_tracer.geometry.Vector;

import java.util.Optional;

public abstract class Shape {
    protected static final double EPSILON = 1e-9;
    // la couleur principale de l'objet
    protected final Color diffuse;
    // effet miroir / brillance
    protected final Color specular;
    protected final int shininess;

    /**
     * Constructeur de base pour les formes : initialise les matériaux.
     * @param diffuse couleur diffuse
     * @param specular couleur spéculaire
     * @param shininess coefficient de brillance
     */
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

    /**
     * Tentative d'intersection entre la forme et un rayon.
     * Par défaut aucune intersection (doivent être redéfinies par les sous-classes).
     * @param ray rayon testé
     * @return {@code Optional<Intersection>} (vide si aucune intersection)
     */
    public Optional<Intersection> intersect(Ray ray) {
        return Optional.empty();
    }

    /**
     * Retourne la normale à la surface au point donné. Doit être implémentée par les formes concrètes.
     * @param point point sur la surface
     * @return {@code Vector} normale
     * @throws UnsupportedOperationException si non implémentée
     */
    public Vector getNormalAt(Point point) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("getNormalAt method not implemented for this shape type.");
    }

    /** @return couleur diffuse */
    public Color getDiffuse() {
        return diffuse;
    }

    /** @return couleur spéculaire */
    public Color getSpecular() {
        return specular;
    }

    /** @return coefficient de brillance (shininess) */
    public int getShininess() {
        return shininess;
    }
 // toString() convertit l'objet en texte lisible.
    /** @return représentation textuelle de la forme */
    public String toString() {
        return "Shape(diffuse: " + diffuse + ", specular: " + specular + ", shininess: " + shininess + ")";
    }
}
