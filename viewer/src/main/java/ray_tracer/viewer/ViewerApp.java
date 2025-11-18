package ray_tracer.viewer;

import javafx.application.Application;
import javafx.application.Platform;
// javafx.scene.Scene is referenced with fully-qualified name to avoid conflict with ray_tracer.parsing.Scene
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;

import ray_tracer.parsing.SceneFileParser;
import ray_tracer.parsing.Scene;
import ray_tracer.parsing.ParserException;
import ray_tracer.parsing.Camera;
import ray_tracer.geometry.Orthonormal;
import ray_tracer.geometry.Point;
import ray_tracer.geometry.Vector;

public class ViewerApp extends Application {
    private ViewerRenderer renderer;
    private Scene sceneModel;
    private WritableImage currentImage;

    @Override
    public void start(Stage primaryStage) {
        var params = getParameters().getRaw();
        if (params.isEmpty()) {
            System.err.println("Usage: javafx:run -Dexec.args=\"<scene_file>\"");
            Platform.exit();
            return;
        }
        try {
            sceneModel = SceneFileParser.parse(params.get(0));
        } catch (ParserException e) {
            e.printError();
            Platform.exit();
            return;
        }

        int width = sceneModel.getWidth();
        int height = sceneModel.getHeight();

        WritableImage image = new WritableImage(width, height);
        currentImage = image;
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);

        BorderPane root = new BorderPane(imageView);
        javafx.scene.Scene fxScene = new javafx.scene.Scene(root, Math.min(800, width), Math.min(600, height));
        primaryStage.setTitle("RayTracer Viewer");
        primaryStage.setScene(fxScene);
        primaryStage.show();

        renderer = new ViewerRenderer();
        renderer.requestRender(sceneModel, image);

        fxScene.addEventHandler(KeyEvent.KEY_PRESSED, e -> handleKey(e, image));
    }

    private void handleKey(KeyEvent e, WritableImage image) {
        switch (e.getCode()) {
            case LEFT -> translateCamera(-0.5, 0, 0);
            case RIGHT -> translateCamera(0.5, 0, 0);
            case UP -> translateCamera(0, 0, -0.5);
            case DOWN -> translateCamera(0, 0, 0.5);
            case R -> renderer.requestRender(sceneModel, image);
            case S -> { /* future: save frame */ }
            default -> {}
        }
    }

    private void translateCamera(double dx, double dy, double dz) {
        Camera old = sceneModel.getCamera();
        Orthonormal basis = Orthonormal.fromCamera(old);
        // Translate along camera axes: dx -> u, dy -> v, dz -> w
        Vector move = basis.getU().scalarMultiplication(dx)
                .addition(basis.getV().scalarMultiplication(dy))
                .addition(basis.getW().scalarMultiplication(dz));

        Point newFrom = old.getLookFrom().addition(move);
        Point newAt = old.getLookAt().addition(move);
        Camera newCam = new Camera(newFrom.getX(), newFrom.getY(), newFrom.getZ(),
                newAt.getX(), newAt.getY(), newAt.getZ(),
                old.getUpDir().getX(), old.getUpDir().getY(), old.getUpDir().getZ(),
                old.getFov());
        sceneModel.setCamera(newCam);
        // request new render
        renderer.requestRender(sceneModel, currentImage);
    }

    @Override
    public void stop() throws Exception {
        if (renderer != null) renderer.shutdown();
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
