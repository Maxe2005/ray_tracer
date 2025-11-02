package ray_tracer.parsing;

import ray_tracer.geometry.Vector;
import ray_tracer.imaging.Color;

public class DirectionalLight extends AbstractLight {
    private Vector direction;

    public DirectionalLight(Vector direction, Color color) {
        super(color);
        this.direction = direction;
    }

    public Vector getDirection() {
        return direction;
    }

}
