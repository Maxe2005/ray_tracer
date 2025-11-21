package ray_tracer.parsing;

import ray_tracer.geometry.Vector;
import ray_tracer.imaging.Color;

/**
 * Représente une lumière directionnelle.
 * Elle possède uniquement une DIRECTION.
 * Elle hérite de AbstractLight → donc elle possède déjà une couleur.
 */
public class DirectionalLight extends AbstractLight {
    private Vector direction;

    public DirectionalLight(Vector direction, Color color) {
        super(color);
        this.direction = direction;
    }

    public Vector getDirection() {
        return direction;
    }

    public String toString() {
        return "DirectionalLight(direction: " + direction + ", color: " + getColor() + ")";
    }

}
