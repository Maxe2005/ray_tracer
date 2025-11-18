package ray_tracer.viewer;

import javafx.application.Platform;
import javafx.image.PixelFormat;
import javafx.image.WritableImage;
import javafx.scene.image.PixelWriter;
import java.nio.IntBuffer;
import ray_tracer.geometry.Orthonormal;
import ray_tracer.imaging.Color;
import ray_tracer.parsing.Scene;
import ray_tracer.raytracer.Ray;
import ray_tracer.raytracer.RayTracer;
import ray_tracer.geometry.Intersection;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ViewerRenderer {
    private final ExecutorService pool;
    private CancelToken currentToken;

    public ViewerRenderer() {
        int threads = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
        pool = Executors.newFixedThreadPool(threads);
        currentToken = null;
    }

    public synchronized void requestRender(Scene scene, WritableImage image) {
        if (currentToken != null) {
            currentToken.cancel();
        }
        currentToken = new CancelToken();
        renderAsync(scene, image, currentToken);
    }

    private void renderAsync(Scene scene, WritableImage image, CancelToken token) {
        final int width = (int) image.getWidth();
        final int height = (int) image.getHeight();

        RayTracer rayTracer = new RayTracer(scene);
        rayTracer.setPixelsDimensions();
        Orthonormal basis = Orthonormal.fromCamera(scene.getCamera());

        PixelWriter writer = image.getPixelWriter();
        PixelFormat<IntBuffer> pf = PixelFormat.getIntArgbInstance();

        for (int y = 0; y < height; y++) {
            final int row = y;
            pool.submit(() -> {
                if (token.isCanceled()) return;
                int[] rowPixels = new int[width];
                for (int x = 0; x < width; x++) {
                    Ray ray = new Ray(scene.getCamera().getLookFrom());
                    ray.setDirection(basis, x, row, rayTracer.getPixelWidth(), rayTracer.getPixelHeight(), width, height);
                    Optional<Intersection> inter = scene.intersect(ray);
                    Color color = inter.map(scene::getTotalColorAt).orElse(Color.BLACK);
                    rowPixels[x] = 0xFF000000 | color.toRGB();
                }
                if (token.isCanceled()) return;
                Platform.runLater(() -> writer.setPixels(0, row, width, 1, pf, rowPixels, 0, width));
            });
        }
    }

    public synchronized void shutdown() {
        if (currentToken != null) currentToken.cancel();
        pool.shutdownNow();
    }

    public static class CancelToken {
        private final AtomicBoolean canceled = new AtomicBoolean(false);
        public void cancel() { canceled.set(true); }
        public boolean isCanceled() { return canceled.get(); }
    }
}
