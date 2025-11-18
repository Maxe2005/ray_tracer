package ray_tracer.imaging;

public class Image {
    private Color[][] pixels;
    private final int width;
    private final int height;
    private static final Color DEFAULT_COLOR = Color.BLACK;

    public Image(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new Color[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                pixels[x][y] = DEFAULT_COLOR;
            }
        }
    }

    public Color getPixelColor(int x, int y) throws IllegalArgumentException {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            throw new IllegalArgumentException("Coordinates out of bounds");
        }
        return pixels[x][y];
    }

    public void setPixelColor(int x, int y, Color color) throws IllegalArgumentException {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            throw new IllegalArgumentException("Coordinates out of bounds");
        }
        pixels[x][y] = color;
    }

    public void flipUpDown() {
        Color[][] flippedPixels = new Color[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                flippedPixels[x][height - y - 1] = pixels[x][y];
            }
        }
        pixels = flippedPixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

}
