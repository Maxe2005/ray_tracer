package ray_tracer.parsing;

import ray_tracer.imaging.Color;
import ray_tracer.geometry.Intersection;
import ray_tracer.geometry.Point;
import ray_tracer.geometry.Vector;
/**
Classe abstraite représentant une lumière.
Elle définit uniquement une couleur, car toutes les lumières ont au minimum une couleur.
Les classes qui héritent (PointLight, DirectionalLight) l'utiliseront.
 */
public abstract class AbstractLight {
    private Color color;

    /**
     * Constructeur d'une lumière abstraite (couleur uniquement).
     * @param color couleur/intensité de la source
     */
    public AbstractLight(Color color) {
        this.color = color;
    }

    /**
     * Calcule la couleur fournie par la lumière en un point d'intersection.
     * Doit être implémentée par les sous-classes.
     */
    public Color getColorAt(Intersection intersection, Vector eyeDirection) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("getColorAt method not implemented for this light type.");
    };

    /**
     * Retourne la direction depuis un point vers la source (ou la direction de la source).
     * Doit être implémentée par les sous-classes.
     */
    public Vector getDirectionFrom (Point point) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("getDirectionFrom method not implemented for this light type.");
    };

    /** @return couleur/intensité de la lumière */
    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "AbstractLight(color: " + color + ")";
    }

}
