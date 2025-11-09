package ray_tracer.parsing;

import ray_tracer.geometry.Vector;
import ray_tracer.imaging.Color;
import ray_tracer.geometry.Intersection;

public class DirectionalLight extends AbstractLight {
    private Vector direction;

    public DirectionalLight(Vector direction, Color color) {
        super(color);
        this.direction = direction;
    }

    @Override
    public Color getColorAt(Intersection intersection) {
        return this.getColor().scalarMultiplication(Math.max(intersection.getNormal().scalarProduct(direction), 0.0)).schurProduct(intersection.getShape().getDiffuse());
    }

    public Vector getDirection() {
        return direction;
    }

    public String toString() {
        return "DirectionalLight(direction: " + direction + ", color: " + getColor() + ")";
    }

}
