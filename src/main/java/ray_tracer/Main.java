package ray_tracer;

import ray_tracer.parsing.Scene;
import ray_tracer.parsing.SceneFileParser;
import ray_tracer.parsing.ParserException;
import ray_tracer.imaging.GenerateImage;

public class Main {
    private static Scene scene;
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -jar ray_tracer.jar <scene_file> [--progress|--no-progress]");
            return;
        }

        boolean showProgress = true; // default
        // Parse optional flags or legacy true/false
        for (int i = 1; i < args.length; i++) {
            String a = args[i].trim();
            if ("--no-progress".equals(a)) {
                showProgress = false;
            } else if ("--progress".equals(a)) {
                showProgress = true;
            } else if ("true".equalsIgnoreCase(a) || "false".equalsIgnoreCase(a)) {
                showProgress = Boolean.parseBoolean(a);
            } else {
                System.out.println("Unknown option: " + a);
                System.out.println("Usage: java -jar ray_tracer.jar <scene_file> [--progress|--no-progress]");
                return;
            }
        }

        try {
            scene = SceneFileParser.parse(args[0]);
            System.out.println("\nScene parsed successfully: " + (scene != null));
            // if (scene != null) {
            //     System.out.println("\n" + scene);
            // }
        } catch (ParserException e) {
            System.err.println("Error parsing scene file: ");
            e.printError();
        }

        if (scene != null) {
            GenerateImage.render(scene, showProgress);
        } else {
            System.err.println("Scene is null — rendering skipped.");
        }
    }
}