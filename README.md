# ray_tracer — Jalon 4

But du jalon 4

- Calculer la couleur d'un point visible en sommant les contributions de toutes les sources de lumière présentes dans la scène.

Description

- Pour chaque point d'intersection trouvé par la boucle de ray tracing, la couleur finale est obtenue en additionnant les contributions de chaque source : ambiante + diffuse + spéculaire (si présentes).

Composantes prises en compte

- **Ambiante** : contribution globale (couleur ambiante de la scène).
- **Diffuse** : modèle de Lambert (k_d _ (N · L) _ I_light) appliqué pour chaque source.
- **Spéculaire** : composante spéculaire basique (p. ex. Phong) si le matériau fournit des coefficients spéculaires.
- Les contributions de toutes les sources sont additionnées pour produire la couleur finale (avec clamp si le pipeline l'applique).

Remarques

- À ce stade les contributions ne sont pas occluses par d'autres objets (pas de gestion des ombres) : chaque source de lumière participe à la couleur du point indépendamment.
- Les optimisations et effets avancés (ombres, réflexions, réfractions, attenuation, anti-aliasing) seront ajoutés dans les jalons suivants.

Fichiers et endroits à consulter

- Calcul d'éclairement : classes du package `raytracer` / `renderer` et méthodes liées aux matériaux dans `geometry/shapes`.
- Scènes d'exemple : `src/main/resources/scenes/jalon4/`.
- Tests unitaires : `src/test/java/...` (les tests `tp41*` et `tp42*` valident les comportements directionnel/ponctuel).

Utilisation rapide

- Compiler et exécuter les tests :

  `mvn test`

- Lancer un rendu d'exemple :

  `./scripts/linux/run_final.sh <chemin/vers/scene.scene>`

  Exemple :

  `./scripts/linux/run_final.sh ./src/main/resources/scenes/jalon4/tp41-dir.test`

- Lancer toutes les scenes de tests avec comparaison :
  
  `./scripts/linux/run_scenes.sh`

  Conseils d'utilisation de ce script:

  - copier le fichier `exemple.env`,
  - le renomer `.env` et renseigner le path du .jar du comparateur d'image pour que le script puisse effectuer les comparaisons.
  - vous trouverez un comparateur d'image sur mon github : `https://github.com/Maxe2005/imgcompare`
