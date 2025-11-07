package ray_tracer.parsing;

import ray_tracer.geometry.Point;
import ray_tracer.imaging.Color;

public class PointLight extends AbstractLight {
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
