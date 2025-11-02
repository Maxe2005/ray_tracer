package ray_tracer.geometry.shapes;

import ray_tracer.imaging.Color;

public abstract class Shape {
    protected final Color diffuse;
    protected final Color specular;

    public Shape(Color diffuse, Color specular) {
        this.diffuse = diffuse;
        this.specular = specular;
    }

    public abstract Color getDiffuse();
    public abstract Color getSpecular();
}
