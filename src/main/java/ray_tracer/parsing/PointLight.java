package ray_tracer.parsing;

import ray_tracer.geometry.Point;
import ray_tracer.imaging.Color;

/**
  Représente une lumière ponctuelle, c'est-à-dire une lumière située à un point précis dans la scène.
  Elle hérite de AbstractLight, donc elle possède déjà une couleur.
 */
public class PointLight extends AbstractLight {
    // Position de la lumière dans l'espace 3D
    private Point position;

    public PointLight(Point position, Color color) {
        super(color);
        this.position = position;
    }

    public Point getPosition() {
        return position;
    }

    public String toString() {
        return "PointLight(position: " + position + ", color: " + getColor() + ")";
    }

}
