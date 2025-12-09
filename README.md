# ray_tracer — Jalon 3

But du jalon 3

- Implémenter la boucle principale du lanceur de rayons : génération des rayons de la caméra, détection des intersections entre rayons et objets, et remplissage des pixels selon une règle simple de couleur.

Comportement implémenté

- Boucle principale du rendu qui parcourt les pixels, génère un rayon par pixel et recherche la première intersection.
- Si aucun objet n'est touché, le pixel est noir (`Color.BLACK`).
- Si un objet est touché, le pixel prend la couleur de cet objet (couleur définie par le matériau via le parsing).
- Les effets d'éclairement avancés (ombres, réflexions, réfractions, anti-aliasing) ne sont pas encore pris en compte et seront ajoutés dans les jalons suivants.

Objectif pédagogique

- Séparer la mécanique de balayage/génération de rayons et la logique d'intersection du calcul d'éclairement afin de faciliter les tests et l'évolution du moteur.

Fichiers et points d'entrée utiles

- Voir les sources du moteur (package `raytracer` / classes de rendu et d'intersection) pour la boucle principale et les tests dans `src/test`.
- Scènes d'exemple : `src/main/resources/scenes/`.
- Scripts d'exécution : `scripts/linux/run_final.sh` et `scripts/linux/run_scenes.sh`.

Utilisation rapide

- Compiler et lancer les tests :

  `mvn test`

- Exécuter le rendu d'une scène :

  `./scripts/linux/run_final.sh <chemin/vers/scene.scene>`

  Exemple :

  `./scripts/linux/run_final.sh ./src/main/resources/scenes/jalon3/example.scene`

Notes

- Les tests valident principalement la détection d'intersections (miss vs hit) et la correspondance pixel <-> premier objet touché.
- Les couleurs affichées pour un hit proviennent directement des propriétés de la forme (définies par le parsing) sans modèle d'éclairement pour l'instant.
