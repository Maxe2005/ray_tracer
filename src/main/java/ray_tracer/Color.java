package ray_tracer;

public class Color extends AbstractVec3 {
    private final double r;
    private final double g;
    private final double b;

    public Color(double r, double g, double b) {
        super(r, g, b);
        this.r = this.getX();
        this.g = this.getY();
        this.b = this.getZ();
    }

    public Color() {
        this(0.0, 0.0, 0.0);
    }

    @Override
    public AbstractVec3 addition(AbstractVec3 other) {
        Color o = (Color) other;
        return new Color(this.getX() + o.getX(), this.getY() + o.getY(), this.getZ() + o.getZ());
    }

    @Override
    public AbstractVec3 scalarMultiplication(double scalar) {
        return new Color(this.getX() * scalar, this.getY() * scalar, this.getZ() * scalar);
    }

    @Override
    public AbstractVec3 schurProduct(AbstractVec3 other) {
        Color o = (Color) other;
        return new Color(this.getX() * o.getX(), this.getY() * o.getY(), this.getZ() * o.getZ());
    }

    public int toRGB() {
        int red = (int) Math.round(r * 255);
        int green = (int) Math.round(g * 255);
        int blue = (int) Math.round(b * 255);
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
