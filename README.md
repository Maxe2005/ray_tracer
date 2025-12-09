# ray_tracer — Jalon 5

But du jalon 5

- Introduire un modèle d'illumination de Phong et la gestion des ombres : calculer la couleur d'un point en tenant compte des sources lumineuses, de la géométrie (occlusion) et de la position de l'observateur.

Objectifs techniques

- Remplacer l'illumination purement locale par le modèle de Phong (ambiante + diffuse + spéculaire) pour obtenir une couleur dépendante de l'œil.
- Ajouter la gestion des ombres via l'envoi de rays d'occlusion (shadow rays) depuis le point d'intersection vers chaque source de lumière.
- Additionner les contributions de chaque source en tenant compte de l'occlusion : une source occluse n'apporte pas sa contribution diffuse/spéculaire.
- Gérer le clamp éventuel des composantes de couleur pour éviter les dépassements (capping entre 0 et 1 selon l'implémentation).

Description succincte

- Pour chaque point touché par un rayon :
  - calculer la contribution ambiante globale ;
  - pour chaque source de lumière :
    - lancer un shadow ray vers la source ;
    - si la source n'est pas occluse, ajouter la contribution diffuse (Lambert) et la contribution spéculaire (Phong) selon les coefficients du matériau ;
  - sommer les contributions et appliquer le clamp si nécessaire.

Ce qui change dans le code

- Ajout/extension de la logique d'éclairement dans le renderer / raytracer (calcul des contributions par lumière).
- Envoi de shadow rays et vérification d'occlusion dans les routines d'intersection.
- Utilisation des propriétés `diffuse` / `specular` et d'un coefficient spéculaire (si présent) pour calculer la composante spéculaire dépendante de la direction de l'œil.

Fichiers et emplacements utiles

- Implémentation principale : packages `raytracer` / `renderer` et classes liées aux matériaux dans `geometry/shapes`.
- Lights & parsing : `src/main/java/ray_tracer/parsing/` (PointLight, DirectionalLight, SceneFileParser).
- Scènes et tests d'exemple : `src/main/resources/scenes/jalon5/` et tests unitaires `src/test/java/...` (tp51-\*, tp52, ...).

Utilisation rapide

- Compiler et lancer les tests :

  `mvn test`

- Exécuter un rendu d'exemple :

  `./scripts/linux/run_final.sh <chemin/vers/scene.scene>`

  Exemple :

  `./scripts/linux/run_final.sh ./src/main/resources/scenes/jalon5/tp51-diffuse.test`

- Lancer toutes les scenes de tests avec comparaison :
  
  `./scripts/linux/run_scenes.sh`

  Conseils d'utilisation de ce script:

  - copier le fichier `exemple.env`,
  - le renomer `.env` et renseigner le path du .jar du comparateur d'image pour que le script puisse effectuer les comparaisons.
  - vous trouverez un comparateur d'image sur mon github : `https://github.com/Maxe2005/imgcompare`
