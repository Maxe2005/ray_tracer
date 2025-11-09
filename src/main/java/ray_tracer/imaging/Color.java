package ray_tracer.imaging;

import ray_tracer.geometry.AbstractVec3;

public class Color extends AbstractVec3 {
    private final double r;
    private final double g;
    private final double b;
    public static final Color BLACK = new Color(0.0, 0.0, 0.0);
    public static final Color WHITE = new Color(1.0, 1.0, 1.0);

    public Color(double r, double g, double b) {
        super(r > 1.0 ? 1.0 : (r < 0.0 ? 0.0 : r),
                g > 1.0 ? 1.0 : (g < 0.0 ? 0.0 : g),
                b > 1.0 ? 1.0 : (b < 0.0 ? 0.0 : b));
        this.r = this.getX();
        this.g = this.getY();
        this.b = this.getZ();
    }

    public Color() {
        this(0.0, 0.0, 0.0);
    }

    @Override
    public Color addition(AbstractVec3 other) {
        Color o = (Color) other;
        return new Color(this.getR() + o.getR(), this.getG() + o.getG(), this.getB() + o.getB());
    }

    @Override
    public Color scalarMultiplication(double scalar) {
        return new Color(this.getR() * scalar, this.getG() * scalar, this.getB() * scalar);
    }

    @Override
    public Color schurProduct(AbstractVec3 other) {
        Color o = (Color) other;
        return new Color(this.getR() * o.getR(), this.getG() * o.getG(), this.getB() * o.getB());
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

    public String toString() {
        return "Color(r:" + r + ", g:" + g + ", b:" + b + ")";
    }

}
