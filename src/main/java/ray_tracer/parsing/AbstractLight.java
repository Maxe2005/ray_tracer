package ray_tracer.parsing;

import ray_tracer.imaging.Color;

public abstract class AbstractLight {
    private Color color;

    public AbstractLight(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public String toString() {
        return "AbstractLight(color: " + color + ")";
    }

}
