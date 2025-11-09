package ray_tracer.parsing;

import ray_tracer.geometry.Intersection;
import ray_tracer.geometry.Point;
import ray_tracer.imaging.Color;
import ray_tracer.geometry.Vector;

public class PointLight extends AbstractLight {
    private Point position;

    public PointLight(Point position, Color color) {
        super(color);
        this.position = position;
    }

    @Override
    public Color getColorAt(Intersection intersection) {
        Vector direction = position.subtraction(intersection.getPoint());
        return this.getColor().scalarMultiplication(Math.max(intersection.getNormal().scalarProduct(direction), 0.0)).schurProduct(intersection.getShape().getDiffuse());
    }

    public Point getPosition() {
        return position;
    }

    public String toString() {
        return "PointLight(position: " + position + ", color: " + getColor() + ")";
    }

}
