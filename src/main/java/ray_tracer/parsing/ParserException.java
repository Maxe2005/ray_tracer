package ray_tracer.parsing;
/**
  ParserException représente une erreur rencontrée pendant la lecture du fichier de scène.
 Elle permet de :
  - STOPPER immédiatement le parsing
  - SAVOIR exactement à QUELLE LIGNE il y a une erreur
  - AFFICHER un message beaucoup plus clair pour l'utilisateur
 */

public class ParserException extends Exception {
 // La ligne du fichier où l'erreur a été trouvée
    // (0 signifie : pas de ligne spécifique → erreur globale)
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
