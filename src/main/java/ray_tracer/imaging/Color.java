package ray_tracer.imaging;

import ray_tracer.geometry.AbstractVec3;

public class Color extends AbstractVec3 {
    public static final Color BLACK = new Color(0.0, 0.0, 0.0);
    public static final Color WHITE = new Color(1.0, 1.0, 1.0);

    public Color(double r, double g, double b) {
        super(r > 1.0 ? 1.0 : (r < 0.0 ? 0.0 : r),
                g > 1.0 ? 1.0 : (g < 0.0 ? 0.0 : g),
                b > 1.0 ? 1.0 : (b < 0.0 ? 0.0 : b));
    }

    public Color() {
        this(0.0, 0.0, 0.0);
    }

    @Override
    public Color addition(AbstractVec3 other) {
        Color o = (Color) other;
        return new Color(this.getX() + o.getX(), this.getY() + o.getY(), this.getZ() + o.getZ());
    }

    @Override
    public Color scalarMultiplication(double scalar) {
        return new Color(this.getX() * scalar, this.getY() * scalar, this.getZ() * scalar);
    }

    @Override
    public Color schurProduct(AbstractVec3 other) {
        Color o = (Color) other;
        return new Color(this.getX() * o.getX(), this.getY() * o.getY(), this.getZ() * o.getZ());
    }

    public int toRGB() {
        int red = (int) Math.round(this.getX() * 255);
        int green = (int) Math.round(this.getY() * 255);
        int blue = (int) Math.round(this.getZ() * 255);
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
        return "Color(r:" + this.getR() + ", g:" + this.getG() + ", b:" + this.getB() + ")";
    }

}
