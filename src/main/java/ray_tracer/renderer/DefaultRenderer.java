package ray_tracer.renderer;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;

import ray_tracer.geometry.Orthonormal;
import ray_tracer.geometry.Intersection;
import ray_tracer.imaging.Color;
import ray_tracer.parsing.Camera;
import ray_tracer.parsing.Scene;
import ray_tracer.raytracer.RayTracer;
import ray_tracer.raytracer.Ray;

/**
 * Default tile-based renderer using an ExecutorService.
 */
public class DefaultRenderer implements Renderer {

    private final ExecutorService executor;
    public DefaultRenderer() {
        this(createDaemonPool(Runtime.getRuntime().availableProcessors()));
    }

    private static ExecutorService createDaemonPool(int nThreads) {
        ThreadFactory tf = new ThreadFactory() {
            private final AtomicInteger cnt = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "rt-renderer-" + cnt.getAndIncrement());
                t.setDaemon(true);
                return t;
            }
        };
        return Executors.newFixedThreadPool(nThreads, tf);
    }

    public DefaultRenderer(ExecutorService executor) {
        this.executor = executor;
    }

    @Override
    public RenderTask render(Scene scene, Camera camera, int width, int height, RenderOptions opts) {
        AtomicBoolean cancelled = new AtomicBoolean(false);
        List<ProgressListener> listeners = Collections.synchronizedList(new ArrayList<>());

        Callable<BufferedImage> callable = () -> {
            // Snapshot scene/camera for thread-safety
            Scene renderScene = (scene != null) ? scene.copyForRender() : null;
            Camera renderCamera = (camera != null) ? camera.copy() : null;

            if (renderScene == null || renderCamera == null) {
                throw new RenderException("Scene or Camera is null");
            }

            if (renderScene.isDirty()) {
                renderScene.buildAcceleration();
            }

            // Low-res preview pass if requested
            double low = Math.max(0.01, Math.min(1.0, opts.lowResFactor));
            if (low < 1.0) {
                int lw = Math.max(1, (int) Math.round(width * low));
                int lh = Math.max(1, (int) Math.round(height * low));
                BufferedImage lowImg = renderInternal(renderScene, renderCamera, lw, lh, opts, cancelled, listeners, true);
                // upscale and send preview
                BufferedImage up = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                up.getGraphics().drawImage(lowImg, 0, 0, width, height, null);
                RenderUpdate ru = new RenderUpdate(up, 0, 0, width, height, 0.05);
                for (ProgressListener l : listeners) l.onUpdate(ru);
                if (cancelled.get()) throw new RenderException("Render cancelled");
            }

            // Full render (possibly progressive: multiple passes)
            BufferedImage finalImg = renderInternal(renderScene, renderCamera, width, height, opts, cancelled, listeners, false);
            return finalImg;
        };

        Future<BufferedImage> future = executor.submit(callable);
        return new RenderTaskImpl(future, listeners, cancelled);
    }

    @Override
    public BufferedImage renderSync(Scene scene, Camera camera, int width, int height, RenderOptions opts) throws RenderException {
        try {
            return render(scene, camera, width, height, opts).getFuture().get();
        } catch (Exception e) {
            throw new RenderException("Synchronous render failed", e);
        }
    }

    private BufferedImage renderInternal(Scene scene, Camera camera, int width, int height, RenderOptions opts, AtomicBoolean cancelled, List<ProgressListener> listeners, boolean preview) throws RenderException {
        Orthonormal basis = Orthonormal.fromCamera(camera);
        RayTracer rt = new RayTracer(scene);
        rt.setPixelsDimensions();

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        int tileSize = Math.max(1, opts.tileSize);
        List<int[]> tiles = new ArrayList<>();
        for (int y = 0; y < height; y += tileSize) {
            for (int x = 0; x < width; x += tileSize) {
                int w = Math.min(tileSize, width - x);
                int h = Math.min(tileSize, height - y);
                tiles.add(new int[]{x, y, w, h});
            }
        }

        // Optional prioritization: simple center-first sort
        if (opts.prioritizeCenter) {
            final int cx = width/2, cy = height/2;
            tiles.sort((a,b)->{
                int da = (a[0]+a[2]/2 - cx)*(a[0]+a[2]/2 - cx)+(a[1]+a[3]/2 - cy)*(a[1]+a[3]/2 - cy);
                int db = (b[0]+b[2]/2 - cx)*(b[0]+b[2]/2 - cx)+(b[1]+b[3]/2 - cy)*(b[1]+b[3]/2 - cy);
                return Integer.compare(da, db);
            });
        }

        // Use the renderer's executor for tile tasks to avoid creating a new pool each render.
        ExecutorService pool = this.executor;
        try {
            List<Future<?>> futures = new ArrayList<>();
            final int totalTiles = tiles.size();
            final int[] doneCount = {0};

            // Thread-local reusable Ray to avoid allocating a Ray per pixel
            final ThreadLocal<Ray> rayLocal = ThreadLocal.withInitial(() -> new Ray(camera.getLookFrom()));

            for (int[] t : tiles) {
                if (cancelled.get()) break;
                Runnable task = () -> {
                    int tx = t[0], ty = t[1], tw = t[2], th = t[3];
                    for (int yy = 0; yy < th; yy++) {
                        if (cancelled.get()) break;
                        int py = ty + yy;
                        for (int xx = 0; xx < tw; xx++) {
                            if (cancelled.get()) break;
                            int px = tx + xx;
                            // compute ray for pixel (px,py)
                            Ray ray = rayLocal.get();
                            // update origin in case camera moved
                            ray.setOrigin(camera.getLookFrom());
                            ray.setDirection(basis, px, py, rt.getPixelWidth(), rt.getPixelHeight(), width, height);
                            java.util.Optional<Intersection> intersection = scene.intersect(ray);
                            if (intersection.isPresent()) {
                                Color c = scene.getTotalRecursionColorAt(intersection.get());
                                // No need to synchronize per-pixel: tiles are disjoint
                                img.setRGB(px, py, c.toRGB());
                            }
                        }
                    }
                    int done;
                    synchronized (doneCount) { doneCount[0]++; done = doneCount[0]; }
                    double progress = (double)done / (double)totalTiles;
                    // emit update for this tile (snapshot under lock)
                    BufferedImage copy;
                    synchronized (img) {
                        BufferedImage part = img.getSubimage(tx, ty, tw, th);
                        copy = new BufferedImage(part.getWidth(), part.getHeight(), BufferedImage.TYPE_INT_RGB);
                        copy.getGraphics().drawImage(part, 0, 0, null);
                    }
                    RenderUpdate ru = new RenderUpdate(copy, tx, ty, tw, th, progress);
                    for (ProgressListener l : listeners) {
                        try { l.onUpdate(ru); } catch (Exception ex) { /* listeners should handle errors */ }
                    }
                };
                futures.add(pool.submit(task));
            }

            // wait for tiles
            for (Future<?> f : futures) {
                try { f.get(); } catch (Exception e) { if (cancelled.get()) break; }
            }

            if (cancelled.get()) throw new RenderException("Cancelled");
            return img;
        } finally {
            // do not shutdown shared executor
        }
    }

    private static class RenderTaskImpl implements RenderTask {
        private final Future<BufferedImage> future;
        private final List<ProgressListener> listeners;
        private final AtomicBoolean cancelled;

        RenderTaskImpl(Future<BufferedImage> future, List<ProgressListener> listeners, AtomicBoolean cancelled) {
            this.future = future;
            this.listeners = listeners;
            this.cancelled = cancelled;
        }

        @Override
        public void cancel() {
            cancelled.set(true);
            future.cancel(true);
        }

        @Override
        public boolean isDone() {
            return future.isDone();
        }

        @Override
        public void addProgressListener(ProgressListener listener) {
            listeners.add(listener);
        }

        @Override
        public Future<BufferedImage> getFuture() {
            return future;
        }
    }
}
