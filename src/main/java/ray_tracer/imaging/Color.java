package ray_tracer.imaging;

import ray_tracer.geometry.AbstractVec3;
/**
 * Color hérite de AbstractVec3, donc elle fonctionne comme un vecteur 3D.
 * x = r, y = g, z = b.
 */

public class Color extends AbstractVec3 {
    public static final Color BLACK = new Color(0.0, 0.0, 0.0);
    public static final Color WHITE = new Color(1.0, 1.0, 1.0);

    /**
     * Constructeur principal. Initialise les composantes R,G,B et les clamp
     * dans l'intervalle [0,1].
     * @param r composante rouge (0..1)
     * @param g composante verte (0..1)
     * @param b composante bleue (0..1)
     */
    public Color(double r, double g, double b) {
        // Clamp : force chaque composante r, g, b à rester dans l'intervalle [0,1]
        super(r > 1.0 ? 1.0 : (r < 0.0 ? 0.0 : r),
                g > 1.0 ? 1.0 : (g < 0.0 ? 0.0 : g),
                b > 1.0 ? 1.0 : (b < 0.0 ? 0.0 : b));
    }

    /**
     * Constructeur par défaut : retourne la couleur noire.
     */
    public Color() {
        this(0.0, 0.0, 0.0);
    }

    @Override
    /**
     * Addition composante-à-composante entre couleurs.
     * @param other autre couleur/vecteur 3D
     * @return nouvelle instance de {@code Color}
     */
    public Color addition(AbstractVec3 other) {
        Color o = (Color) other;
        return new Color(this.getX() + o.getX(), this.getY() + o.getY(), this.getZ() + o.getZ());
    }

    @Override
    /**
     * Multiplie chaque composante par un scalaire.
     * @param scalar facteur multiplicatif
     * @return nouvelle {@code Color}
     */
    public Color scalarMultiplication(double scalar) {
        return new Color(this.getX() * scalar, this.getY() * scalar, this.getZ() * scalar);
    }

    @Override
    /**
     * Produit de Hadamard (multiplication composante par composante).
     * @param other autre couleur/vecteur
     * @return nouvelle {@code Color}
     */
    public Color schurProduct(AbstractVec3 other) {
        Color o = (Color) other;
        return new Color(this.getX() * o.getX(), this.getY() * o.getY(), this.getZ() * o.getZ());
    }

    /**
     * Convertit la couleur en entier RGB 24-bit (0xRRGGBB).
     * @return valeur entière RGB
     */
    public int toRGB() {
        int red = (int) Math.round(this.getX() * 255);
        int green = (int) Math.round(this.getY() * 255);
        int blue = (int) Math.round(this.getZ() * 255);
        return (((red & 0xff) << 16)
                + ((green & 0xff) << 8)
                + (blue & 0xff));
    }

    /** @return composante rouge (0..1) */
    public double getR() {
        return this.getX();
    }
    /** @return composante verte (0..1) */
    public double getG() {
        return this.getY();
    }
    /** @return composante bleue (0..1) */
    public double getB() {
        return this.getZ();
    }
    /**
     * Représentation textuelle compacte.
     * @return chaîne descriptive
     */
    public String toString() {
        return "Color(r:" + this.getR() + ", g:" + this.getG() + ", b:" + this.getB() + ")";
    }

}
