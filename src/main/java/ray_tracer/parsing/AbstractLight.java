package ray_tracer.parsing;

import ray_tracer.imaging.Color;
import ray_tracer.geometry.Intersection;

public abstract class AbstractLight {
    private Color color;

    public AbstractLight(Color color) {
        this.color = color;
    }

    public Color getColorAt(Intersection intersection) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("getColorAt method not implemented for this light type.");
    };

    public Color getColor() {
        return color;
    }

    public String toString() {
        return "AbstractLight(color: " + color + ")";
    }

}
