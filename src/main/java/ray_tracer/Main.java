package ray_tracer;

import ray_tracer.parsing.Scene;
import ray_tracer.parsing.SceneFileParser;
import ray_tracer.parsing.ParserException;
import ray_tracer.imaging.GenerateImage;

public class Main {
    private static Scene scene;
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -jar ray_tracer.jar <scene_file> [--threads=N | --multithread | -t N]");
            return;
        }

        // Default: let renderer decide (RenderOptions.threadCount default)
        int threadCount = -1;

        // Parse optional args
        for (int i = 1; i < args.length; i++) {
            String a = args[i];
            if (a.startsWith("--threads=")) {
                try {
                    threadCount = Integer.parseInt(a.substring("--threads=".length()));
                } catch (NumberFormatException ex) {
                    System.err.println("Invalid threads count: " + a);
                    return;
                }
            } else if (a.equals("--multithread")) {
                threadCount = Runtime.getRuntime().availableProcessors();
            } else if (a.equals("-t") && i + 1 < args.length) {
                try {
                    threadCount = Integer.parseInt(args[++i]);
                } catch (NumberFormatException ex) {
                    System.err.println("Invalid threads count after -t");
                    return;
                }
            } else {
                System.err.println("Unknown option: " + a);
                System.out.println("Usage: java -jar ray_tracer.jar <scene_file> [--threads=N | --multithread | -t N]");
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

        // Delegate rendering; if threadCount <= 0 uses renderer defaults
        // GenerateImage.render(scene, threadCount);
        GenerateImage.renderSync(scene);
    }
}