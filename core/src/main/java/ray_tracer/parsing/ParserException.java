package ray_tracer.parsing;

public class ParserException extends Exception {

    private int lineNumber = 0;

    public ParserException(String message, int lineNumber) {
        super(message);
        this.lineNumber = lineNumber;
    }

    public ParserException(String message) {
        super(message);
    }

    public void printError() {
        if (lineNumber == 0) {
            System.err.println("[SceneFileParser] GLOBAL ERROR: " + getMessage());
        } else {
            System.err.println("[SceneFileParser] ERROR at line " + lineNumber + ": " + getMessage());
        }
    }

}
