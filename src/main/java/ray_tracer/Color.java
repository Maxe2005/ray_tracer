package ray_tracer;

public class Color extends AbstractVec3 {
    private final double r;
    private final double g;
    private final double b;

    public Color(double r, double g, double b) {
// Clamp : force chaque composante r, g, b à rester dans l'intervalle [0,1]
                super(r > 1.0 ? 1.0 : (r < 0.0 ? 0.0 : r),
                g > 1.0 ? 1.0 : (g < 0.0 ? 0.0 : g),
                b > 1.0 ? 1.0 : (b < 0.0 ? 0.0 : b));
        this.r = this.getX();
        this.g = this.getY();
        this.b = this.getZ();
    }
 // Constructeur par défaut : couleur noire (0,0,0).
     
    public Color() {
        this(0.0, 0.0, 0.0);
    }

    @Override
    public AbstractVec3 addition(AbstractVec3 other) {
        Color o = (Color) other;
        return new Color(this.getR() + o.getR(), this.getG() + o.getG(), this.getB() + o.getB());
    }
    @Override
    public AbstractVec3 scalarMultiplication(double scalar) {
        return new Color(this.getR() * scalar, this.getG() * scalar, this.getB() * scalar);
    }

    @Override
    public AbstractVec3 schurProduct(AbstractVec3 other) {
        Color o = (Color) other;
        return new Color(this.getR() * o.getR(), this.getG() * o.getG(), this.getB() * o.getB());
    }
// Convertit la couleur (stockée en [0,1]) en une valeur RGB 24 bits.

    public int toRGB() {
        int red = (int) Math.round(r * 255);
        int green = (int) Math.round(g * 255);
        int blue = (int) Math.round(b * 255);
// Construction de l'entier au format 0xRRGGBB
        return (((red & 0xff) << 16)
                + ((green & 0xff) << 8)
                + (blue & 0xff));
    }

    public double getR() {
        return this.getX();
    }

    public double getG() {
        return this.getY();
    }

    public double getB() {
        return this.getZ();
    }

}
