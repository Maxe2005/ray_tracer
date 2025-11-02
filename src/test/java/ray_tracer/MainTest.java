package ray_tracer;

import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    @Test
    public void main_printsHelloWorld() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));
        try {
            Main.main(new String[] {});
            String out = baos.toString();
            assertTrue(out.contains("Hello world!"));
        } finally {
            System.setOut(originalOut);
        }
    }

}
