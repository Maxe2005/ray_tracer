package ray_tracer.parsing;

import ray_tracer.geometry.Intersection;
import ray_tracer.geometry.Point;
import ray_tracer.imaging.Color;
import ray_tracer.geometry.Vector;

/**
  Représente une lumière ponctuelle, c'est-à-dire une lumière située à un point précis dans la scène.
  Elle hérite de AbstractLight, donc elle possède déjà une couleur.
 */
public class PointLight extends AbstractLight {
    // Position de la lumière dans l'espace 3D
    private Point position;

    /**
     * Constructeur d'une lumière ponctuelle.
     * @param position position de la lumière
     * @param color couleur/intensité
     */
    public PointLight(Point position, Color color) {
        super(color);
        this.position = position;
    }

    @Override
    /**
     * Calcule la contribution lumineuse (diffuse + spéculaire) en un point d'intersection.
     * @param intersection intersection considérée
     * @param eyeDirection vecteur vers l'oeil
     * @return {@code Color} contribution lumineuse
     */
    public Color getColorAt(Intersection intersection, Vector eyeDirection) {
        Vector direction = getDirectionFrom(intersection.getPoint());
        return calculDiffusionLambert(intersection, direction).addition(calculSpeculairePhong(intersection, eyeDirection, direction));
    }

    /**
     * Calcul de la composante diffuse (Lambert).
     */
    private Color calculDiffusionLambert(Intersection intersection, Vector direction) {
        double intensity = Math.max(intersection.getNormal().scalarProduct(direction), 0.0);
        return this.getColor().scalarMultiplication(intensity).schurProduct(intersection.getShape().getDiffuse());
    }

    /**
     * Calcul de la composante spéculaire (Phong).
     */
    private Color calculSpeculairePhong(Intersection intersection, Vector eyeDirection, Vector direction) {
        Vector reflectDir = direction.addition(eyeDirection).normalize();
        double specAngle = Math.max(reflectDir.scalarProduct(intersection.getNormal()), 0.0);
        double specularCoefficient = Math.pow(specAngle, intersection.getShape().getShininess());
        return this.getColor().scalarMultiplication(specularCoefficient).schurProduct(intersection.getShape().getSpecular());
    }

    @Override
    /**
     * Retourne la direction (vers la source) depuis le point donné.
     * @param point point d'où on calcule la direction
     * @return vecteur normalisé point → lumière
     */
    public Vector getDirectionFrom(Point point) {
        return position.subtraction(point).normalize();
    }

    /** @return position de la lumière */
    public Point getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return "PointLight(position: " + position + ", color: " + getColor() + ")";
    }

}
