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

    public AbstractLight(Color color) {
        this.color = color;
    }

    public Color getColorAt(Intersection intersection, Vector eyeDirection) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("getColorAt method not implemented for this light type.");
    };

    public Vector getDirectionFrom (Point point) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("getDirectionFrom method not implemented for this light type.");
    };

    public Color getColor() {
        return color;
    }

    public String toString() {
        return "AbstractLight(color: " + color + ")";
    }

}
