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
    public Color getColorAt(Intersection intersection, Vector eyeDirection) {
        Vector direction = getDirectionFrom(intersection.getPoint());
        return calculDiffusionLambert(intersection, direction).addition(calculSpeculairePhong(intersection, eyeDirection, direction));
    }

    private Color calculDiffusionLambert(Intersection intersection, Vector direction) {
        double intensity = Math.max(intersection.getNormal().scalarProduct(direction), 0.0);
        return this.getColor().scalarMultiplication(intensity).schurProduct(intersection.getShape().getDiffuse());
    }

    private Color calculSpeculairePhong(Intersection intersection, Vector eyeDirection, Vector direction) {
        Vector reflectDir = direction.addition(eyeDirection).normalize();
        double specAngle = Math.max(reflectDir.scalarProduct(intersection.getNormal()), 0.0);
        double specularCoefficient = Math.pow(specAngle, intersection.getShape().getShininess());
        return this.getColor().scalarMultiplication(specularCoefficient).schurProduct(intersection.getShape().getSpecular());
    }

    @Override
    public Vector getDirectionFrom(Point point) {
        return position.subtraction(point).normalize();
    }

    public Point getPosition() {
        return position;
    }

    public String toString() {
        return "PointLight(position: " + position + ", color: " + getColor() + ")";
    }

}
