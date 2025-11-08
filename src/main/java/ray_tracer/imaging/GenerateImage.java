package ray_tracer.imaging;

import ray_tracer.parsing.Scene;
import ray_tracer.raytracer.Ray;
import ray_tracer.raytracer.RayTracer;
import ray_tracer.geometry.Orthonormal;
import ray_tracer.geometry.Intersection;

import java.util.Optional;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

public class GenerateImage {
    private static Orthonormal basis;
    private static RayTracer rayTracer;
    private static Image image;

    public static void render (Scene scene){
        basis = Orthonormal.fromCamera(scene.getCamera());
        rayTracer = new RayTracer(scene);
        rayTracer.setPixelsDimensions();
        image = new Image(scene.getWidth(), scene.getHeight());

        for (int j = 0; j < scene.getHeight(); j++) {
            for (int i = 0; i < scene.getWidth(); i++) {
                Ray ray = new Ray(scene.getCamera().getLookFrom());
                ray.setDirection(basis, i, j, rayTracer.getPixelWidth(), rayTracer.getPixelHeight(), scene.getWidth(), scene.getHeight());
                Optional<Intersection> intersection = scene.intersect(ray);
                if (intersection.isPresent()) {
                    image.setPixelColor(i, j, rayTracer.getPixelColor(intersection.get()));
                }
            }
        }
        writeImage(image, scene.getOutputFile());
    }

    private static BufferedImage toBufferedImage(Image image) {
        BufferedImage buffer = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = image.getPixelColor(x, y);
                buffer.setRGB(x, y, color.toRGB());
            }
        }
        return buffer;
    }

    private static void writeImage(Image image, String name) {
        Path outPath = Paths.get(name);
        try (OutputStream stream = Files.newOutputStream(outPath)) {
            ImageIO.write(toBufferedImage(image), "png", stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
