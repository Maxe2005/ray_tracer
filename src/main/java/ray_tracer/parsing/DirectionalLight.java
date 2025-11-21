package ray_tracer.parsing;

import ray_tracer.geometry.Vector;
import ray_tracer.geometry.Point;
import ray_tracer.imaging.Color;
import ray_tracer.geometry.Intersection;

public class DirectionalLight extends AbstractLight {
    private Vector direction;

    public DirectionalLight(Vector direction, Color color) {
        super(color);
        // Store a normalized direction vector as provided by the scene (preserve orientation),
        // but normalize to ensure consistent lighting computations.
        this.direction = direction.normalize();
    }

    @Override
    public Color getColorAt(Intersection intersection, Vector eyeDirection) {
        return calculDiffusionLambert(intersection).addition(calculSpeculairePhong(intersection, eyeDirection));
    }

    private Color calculDiffusionLambert(Intersection intersection) {
        double intensity = Math.max(intersection.getNormal().scalarProduct(direction), 0.0);
        return this.getColor().scalarMultiplication(intensity).schurProduct(intersection.getShape().getDiffuse());
    }

    private Color calculSpeculairePhong(Intersection intersection, Vector eyeDirection) {
        Vector reflectDir = direction.addition(eyeDirection).normalize();
        double specAngle = Math.max(reflectDir.scalarProduct(intersection.getNormal()), 0.0);
        double specularCoefficient = Math.pow(specAngle, intersection.getShape().getShininess());
        return this.getColor().scalarMultiplication(specularCoefficient).schurProduct(intersection.getShape().getSpecular());
    }

    @Override
    public Vector getDirectionFrom(Point point) {
        return direction;
    }

    public Vector getDirection() {
        return direction;
    }

    public String toString() {
        return "DirectionalLight(direction: " + direction + ", color: " + getColor() + ")";
    }

}
