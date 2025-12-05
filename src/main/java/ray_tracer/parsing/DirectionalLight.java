package ray_tracer.parsing;

import ray_tracer.geometry.Vector;
import ray_tracer.geometry.Point;
import ray_tracer.imaging.Color;
import ray_tracer.geometry.Intersection;

/**
 * Représente une lumière directionnelle.
 * Elle possède uniquement une DIRECTION.
 * Elle hérite de AbstractLight → donc elle possède déjà une couleur.
 */
public class DirectionalLight extends AbstractLight {
    private Vector direction;

    /**
     * Constructeur d'une lumière directionnelle.
     * @param direction direction de la source
     * @param color couleur/intensité
     */
    public DirectionalLight(Vector direction, Color color) {
        super(color);
        // Store a normalized direction vector as provided by the scene (preserve orientation),
        // but normalize to ensure consistent lighting computations.
        this.direction = direction.normalize();
    }

    @Override
    /**
     * Calcule la couleur reçue en un point d'intersection pour cette lumière.
     * @param intersection intersection considérée
     * @param eyeDirection vecteur vers l'oeil
     * @return {@code Color} contribution lumineuse
     */
    public Color getColorAt(Intersection intersection, Vector eyeDirection) {
        return calculDiffusionLambert(intersection).addition(calculSpeculairePhong(intersection, eyeDirection));
    }

    /**
     * Calcul diffusion Lambert pour lumière directionnelle.
     */
    private Color calculDiffusionLambert(Intersection intersection) {
        double intensity = Math.max(intersection.getNormal().scalarProduct(direction), 0.0);
        return this.getColor().scalarMultiplication(intensity).schurProduct(intersection.getShape().getDiffuse());
    }

    /**
     * Calcul spéculaire Phong pour lumière directionnelle.
     */
    private Color calculSpeculairePhong(Intersection intersection, Vector eyeDirection) {
        Vector reflectDir = direction.addition(eyeDirection).normalize();
        double specAngle = Math.max(reflectDir.scalarProduct(intersection.getNormal()), 0.0);
        double specularCoefficient = Math.pow(specAngle, intersection.getShape().getShininess());
        return this.getColor().scalarMultiplication(specularCoefficient).schurProduct(intersection.getShape().getSpecular());
    }

    @Override
    /**
     * Pour une lumière directionnelle, la direction est la même quel que soit le point.
     * @param point point d'où la direction est demandée (ignoré)
     * @return vecteur direction normalisé
     */
    public Vector getDirectionFrom(Point point) {
        return direction;
    }

    /** @return vecteur direction de la lumière */
    public Vector getDirection() {
        return direction;
    }

    public String toString() {
        return "DirectionalLight(direction: " + direction + ", color: " + getColor() + ")";
    }

}
