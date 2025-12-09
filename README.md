# ray_tracer — Jalon 1

Projet d'un mini-moteur de lancer de rayons en Java (Maven).

Objectif du jalon 1

- Implémenter la géométrie de base et le pipeline de rendu minimal.

Ce qui est réalisé

- **Géométrie**: vecteurs, points, normales.
- **Formes**: sphère, plan, triangle (intersections et normales).
- **Raytracing**: calcul d'intersections, ombrage simple, gestion des lumières.
- **Parsing**: lecteur de fichiers de scène minimal.
- **Imagerie**: génération d'images via la couche `imaging`.
- **Tests**: suite de tests unitaires fournie dans `src/test` (tests Maven/Surefire).

Utilisation rapide

- **Builder & tester** : exécuter `mvn test` à la racine du projet.
- Les tests incluent des exemples de scènes et de génération d'images.

Notes

- Nécessite Java et Maven installés.
- Ce jalon pose les fondations; le rendu d'images avancé (reflets, réfractions, anti-aliasing) viendra dans les jalons suivants.
