# ray_tracer — Jalon 2

But du jalon 2

- Implémentation du parser de fichiers de scène (`.scene`). Le parser transforme une description textuelle d'une scène en un objet `Scene` exploitable par le moteur.

Ce qui a été implémenté

- Mots-clés supportés : `size`, `output`, `camera`, `ambient`, `diffuse`, `specular`, `sphere`, `directional`, `point`, `maxverts`, `vertex`, `tri`, `plane`.
- Validation stricte des paramètres et levée de `ParserException` pour les erreurs bloquantes.
- Collection et affichage de warnings pour les problèmes non bloquants (matériaux manquants, redéfinitions, limites de `maxverts`, scènes sans lumières, etc.).
- Gestion d'état entre parsings (`initVariables`, `clearState`) et helpers pour tests (`getWarnings`, `getMaxVerts`, `getVertexList`, `getWaitingDiffuse`, `getWaitingSpecular`).
- Vérifications finales après lecture (`handleFinalsErrors`) pour s'assurer que la scène est cohérente.

Fichier principal

- `src/main/java/ray_tracer/parsing/SceneFileParser.java` — logique complète du parsing (voir le code pour les détails et les messages d'erreur/warning).

Utilisation rapide

- Compiler et lancer la suite de tests unitaires :

  `mvn test`

- Lancer un rendu (script d'exécution fourni) :

  `./scripts/linux/run_final.sh <chemin/vers/scene.scene>`

  Exemple :

  `./scripts/linux/run_final.sh ./src/main/resources/scenes/jalon2/test6.scene`

- Lancer toutes les scenes de tests avec comparaison :
  
  `./scripts/linux/run_scenes.sh`

Notes

- Les warnings collectés pendant le parsing sont affichés sur la sortie d'erreur (stderr) à la fin du parsing.
- Le parser est conçu pour être testé : les méthodes d'accès (helpers) facilitent les tests unitaires.
