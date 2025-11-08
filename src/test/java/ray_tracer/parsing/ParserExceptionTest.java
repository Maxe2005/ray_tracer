package ray_tracer.parsing;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParserExceptionTest {

    @Test
    public void construct_and_print() {
        ParserException ex1 = new ParserException("Erreur globale");
        assertEquals("Erreur globale", ex1.getMessage());

        ParserException ex2 = new ParserException("Erreur ligne", 5);
        assertEquals("Erreur ligne", ex2.getMessage());
        // printError writes to stderr; ensure it doesn't throw
        ex2.printError();
    }

}
