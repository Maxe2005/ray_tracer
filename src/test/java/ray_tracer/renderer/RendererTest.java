package ray_tracer.renderer;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import ray_tracer.imaging.Color;
import ray_tracer.geometry.shapes.Sphere;
import ray_tracer.parsing.Scene;
import ray_tracer.parsing.DirectionalLight;
import ray_tracer.parsing.Camera;
import ray_tracer.geometry.Vector;

public class RendererTest {

    @Test
    public void testRenderSyncSmallScene() throws Exception {
        Scene scene = new Scene();
        scene.addSize(3,3);
        Camera cam = new Camera(0,0,0, 0,0,-1, 0,1,0, 90);
        scene.setCamera(cam);
        scene.addShape(new Sphere(0,0,-3, 1.0, Color.WHITE, Color.WHITE, 10));
        scene.addLight(new DirectionalLight(new Vector(0,0,-1), Color.WHITE));

        DefaultRenderer renderer = new DefaultRenderer();
        RenderOptions opts = new RenderOptions();
        BufferedImage img = renderer.renderSync(scene, cam, 3, 3, opts);
        assertNotNull(img);
        boolean anyNonBlack = false;
        for (int y=0;y<3;y++) for (int x=0;x<3;x++) if (img.getRGB(x,y) != Color.BLACK.toRGB()) anyNonBlack = true;
        assertTrue(anyNonBlack, "Rendered image should contain non-black pixels");
    }

    @Test
    public void testCancellation() throws Exception {
        Scene scene = new Scene();
        scene.addSize(500,500);
        Camera cam = new Camera(0,0,0, 0,0,-1, 0,1,0, 90);
        scene.setCamera(cam);
        scene.addShape(new Sphere(0,0,-3, 1.0, Color.WHITE, Color.WHITE, 10));
        scene.addLight(new DirectionalLight(new Vector(0,0,-1), Color.WHITE));

        DefaultRenderer renderer = new DefaultRenderer();
        RenderOptions opts = new RenderOptions();
        RenderTask task = renderer.render(scene, cam, 500, 500, opts);
        Thread.sleep(20);
        task.cancel();
        // give a bit of time
        Thread.sleep(50);
        assertTrue(task.isDone());
    }

    @Test
    public void testProgressListenerReceivesUpdate() throws Exception {
        Scene scene = new Scene();
        scene.addSize(64,64);
        Camera cam = new Camera(0,0,0, 0,0,-1, 0,1,0, 90);
        scene.setCamera(cam);
        scene.addShape(new Sphere(0,0,-3, 1.0, Color.WHITE, Color.WHITE, 10));
        scene.addLight(new DirectionalLight(new Vector(0,0,-1), Color.WHITE));

        DefaultRenderer renderer = new DefaultRenderer();
        RenderOptions opts = new RenderOptions();
        RenderTask task = renderer.render(scene, cam, 64, 64, opts);
        AtomicBoolean got = new AtomicBoolean(false);
        task.addProgressListener(update -> got.set(true));
        // wait for some updates or finish
        task.getFuture().get(2, TimeUnit.SECONDS);
        assertTrue(got.get(), "Expected at least one progress update");
    }
}
