package ray_tracer;

import ray_tracer.parsing.Scene;
import ray_tracer.parsing.SceneFileParser;
import ray_tracer.parsing.ParserException;
import ray_tracer.imaging.GenerateImage;

public class Main {
    private static Scene scene;
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Usage: java -jar ray_tracer.jar <scene_file>");
            return;
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

        GenerateImage.render(scene);
    }
}