package ray_tracer.imaging;

public class Image {
    private Color[][] pixels;
    private final int width;
    private final int height;
    private static final Color DEFAULT_COLOR = Color.BLACK;

    /**
     * Crée une image en mémoire initialisée avec la couleur par défaut.
     * @param width largeur en pixels
     * @param height hauteur en pixels
     */
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

    /**
     * Récupère la couleur du pixel aux coordonnées (x,y).
     * @param x coordonnée X (0..width-1)
     * @param y coordonnée Y (0..height-1)
     * @return {@code Color} stockée
     * @throws IllegalArgumentException si coordonnées hors bornes
     */
    public Color getPixelColor(int x, int y) throws IllegalArgumentException {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            throw new IllegalArgumentException("Coordinates out of bounds");
        }
        return pixels[x][y];
    }

    /**
     * Définit la couleur du pixel (x,y).
     * @param x coordonnée X
     * @param y coordonnée Y
     * @param color couleur à écrire
     * @throws IllegalArgumentException si coordonnées hors bornes
     */
    public void setPixelColor(int x, int y, Color color) throws IllegalArgumentException {
        if (x < 0 || x >= getWidth() || y < 0 || y >= getHeight()) {
            throw new IllegalArgumentException("Coordinates out of bounds");
        }
        pixels[x][y] = color;
    }

    /**
     * Inverse verticalement l'image (ligne 0 devient dernière ligne).
     */
    public void flipUpDown() {
        Color[][] flippedPixels = new Color[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                flippedPixels[x][height - y - 1] = pixels[x][y];
            }
        }
        pixels = flippedPixels;
    }

    /** @return largeur de l'image en pixels */
    public int getWidth() {
        return width;
    }

    /** @return hauteur de l'image en pixels */
    public int getHeight() {
        return height;
    }

}
