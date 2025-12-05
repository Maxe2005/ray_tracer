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

    /**
     * Crée une exception de parsing avec message et numéro de ligne.
     * @param message message d'erreur
     * @param lineNumber numéro de ligne associé (0 = non spécifié)
     */
    public ParserException(String message, int lineNumber) {
        super(message);
        this.lineNumber = lineNumber;
    }

    /**
     * Crée une exception de parsing sans numéro de ligne.
     * @param message message d'erreur
     */
    public ParserException(String message) {
        super(message);
    }

    /**
     * Affiche l'erreur formatée sur la sortie d'erreur standard.
     */
    public void printError() {
        if (lineNumber == 0) {
            System.err.println("[SceneFileParser] GLOBAL ERROR: " + getMessage());
        } else {
            System.err.println("[SceneFileParser] ERROR at line " + lineNumber + ": " + getMessage());
        }
    }

}
