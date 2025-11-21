package ray_tracer.parsing;

import ray_tracer.imaging.Color;
/**
Classe abstraite représentant une lumière.
Elle définit uniquement une couleur, car toutes les lumières ont au minimum une couleur.
Les classes qui héritent (PointLight, DirectionalLight) l'utiliseront.
 */
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
