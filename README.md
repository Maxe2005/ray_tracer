# ray_tracer — Jalon 6

But du jalon 6 — Réflexion et illumination indirecte

- Étendre le modèle d'éclairement pour prendre en compte la lumière réfléchie (illumination indirecte) : lorsqu'une surface a une composante réfléchissante (couleur non nulle), ajouter la contribution due à la réflexion de la scène environnante.

Description technique

- La couleur primaire d'un point est calculée comme au Jalon 5 (Lambert + Blinn-Phong) ; si la couleur réfléchissante de la surface est non nulle, on calcule la composante réfléchie :
  - construire le rayon réfléchi R à partir du vecteur incident et de la normale au point ;
  - lancer un rayon réfléchi dans la scène (recursion limitée par un `maxDepth`) ;
  - récupérer la couleur renvoyée par ce rayon réfléchi et la multiplier par la couleur réfléchissante (ou coefficient de réflexion) du matériau ;
  - ajouter cette contribution à la couleur finale du point (après sommation des sources directes).

Spécificités du jalon

- Convention: la couleur `specular` dans le format de scène est distincte de la couleur réfléchie ; si la couleur réfléchie d'un matériau est non nulle, on la traite comme coefficient de mélange pour la contribution réfléchie.
- Limiter la récursivité des rayons réfléchis (ex. `maxDepth = 3`) pour éviter explosion de calcul.
- Appliquer un clamp final sur chaque composante de couleur pour rester dans [0,1].

Fichiers / emplacements à consulter

- Routines de lancer de rayons et de rendu : `src/main/java/ray_tracer/raytracer/` (classe `RayTracer`, `Ray`, renderer).
- Formes et matériaux : `src/main/java/ray_tracer/geometry/shapes/` (récupération des couleurs et propriétés).
- Parsing : `src/main/java/ray_tracer/parsing/` (format de scène, nouvelles propriétés si besoin).
- Scènes exemples : `src/main/resources/scenes/jalon6/`.

Utilisation rapide

- Compiler et exécuter les tests :

  `mvn test`

- Lancer un rendu avec réflexions (ex. limiter la profondeur si le script l'accepte) :

  `./scripts/linux/run_final.sh <chemin/vers/scene.scene>`

- Lancer toutes les scenes de tests avec comparaison :
  
  `./scripts/linux/run_scenes.sh`

  Conseils d'utilisation de ce script:

  - copier le fichier `exemple.env`,
  - le renomer `.env` et renseigner le path du .jar du comparateur d'image pour que le script puisse effectuer les comparaisons.
  - vous trouverez un comparateur d'image sur mon github : `https://github.com/Maxe2005/imgcompare`
