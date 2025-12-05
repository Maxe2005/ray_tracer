# BVH pour le ray tracer

Ce document explique en détail ce qu'est un BVH (Bounding Volume Hierarchy), comment il fonctionne en général, et comment il est appliqué et intégré dans ce projet de ray tracer (`ray_tracer`). Le texte vise à couvrir les principes, l'implémentation, les choix faits ici, les réglages possibles, les problèmes connus et des pistes d'amélioration.

---

## Table des matières

- **Qu'est-ce qu'un BVH ?**
- **AABB (Axis-Aligned Bounding Box)**
- **Construction du BVH**
  - stratégie utilisée (médiane sur centroïdes)
  - alternatives (SAH, spatial splits)
  - paramètres (taille de feuille)
- **Parcours / Intersection**
  - test boite/ rayon (slab method)
  - pruning avec tMax
  - ordre des enfants
- **Complexité & Gains de performance**
- **Intégration dans ce ray tracer**
  - fichiers modifiés / ajoutés
  - points d'attention (plans infinis, double comptage, précision)
  - code et extraits (liens vers fichiers)
- **Concurrence, rendu et sécurité**
- **Tests et benchmark**
- **Limitations et pièges**
- **Améliorations possibles**
- **Références et lectures recommandées**

---

## Qu'est-ce qu'un BVH ?

Un BVH (Bounding Volume Hierarchy) est une structure d'accélération spatiale utilisée couramment dans les moteurs de rendu et les moteurs physiques pour réduire considérablement le nombre d'intersections rayon/géometrie à effectuer.

- Idée : regrouper les primitives géométriques (sphères, triangles, plans, etc.) dans une hiérarchie d'enveloppes (bounding volumes).
- Généralement l'enveloppe est une AABB (axis-aligned bounding box) car les calculs de test sont simples et rapides.
- Lors d'un test d'intersection pour un rayon donné, on traverse l'arbre : si le rayon ne touche pas la boîte d'un nœud, on peut ignorer toutes les primitives contenues dans ce sous-arbre.
- Lorsque le rayon touche la boîte, on descend dans les enfants et on teste au besoin les primitives en feuille.

Le bénéfice est énorme sur des scènes avec beaucoup de primitives : au lieu de tester le rayon contre chaque primitive (O(N)), on teste log(N) boîtes + un petit nombre de primitives en feuille. Dans la pratique, les économies sont substantielles.

## AABB (Axis-Aligned Bounding Box)

Une AABB est définie par deux points : `min (x,y,z)` et `max (x,y,z)`. Elle représente le produit cartésien des intervalles [min.x, max.x] × [min.y, max.y] × [min.z, max.z].

Propriétés utiles :

- Construction facile à partir de primitives (par ex. pour une sphère : min = center - r, max = center + r).
- Fusion de deux AABB : `surroundingBox(a,b)` prend le min des mins et le max des maxs.
- Calcul du centroïde : `(min + max) / 2`.

Test rayon/AABB (méthode "slab") :

- Pour chaque axe (x,y,z) on résout l'intersection du rayon avec les plans parallèles à l'axe (t0,t1).
- On construit l'intervalle d'intersection global [tmin, tmax] en tant que l'intersection des trois intervalles par axe.
- Si tmax >= tmin et l'intervalle se recoupe avec l'intervalle d'intérêt du rayon (p.ex. t > 0 et < tMax), alors le rayon touche la boîte.

Dans ce projet la méthode `AABB.hit(Ray r, double tMax)` implémente cette logique (code dans `src/main/java/ray_tracer/geometry/AABB.java`).

## Construction du BVH

Plusieurs stratégies existent pour construire la hiérarchie :

- Split médian (par centroïdes) : trier les primitives par la coordonnée du centroïde le long d'un axe choisi (l'axe avec la plus grande étendue) et couper au milieu. Simple, rapide et produit des arbres équilibrés mais pas nécessairement optimaux.

- Surface Area Heuristic (SAH) : évaluer pour chaque partition possible la probabilité estimée d'intersection (basée sur l'aire des boîtes) et choisir la meilleure coupe. Produit généralement de bien meilleures performances mais est plus cher à construire.

- Spatial splits : couper des primitives qui traversent la frontière en les scindant (utile pour éviter des boîtes fortement chevauchantes), mais requiert plus de complexité (et parfois duplication).

Paramètres importants :

- `maxLeafSize` : nombre max de primitives dans une feuille. Un compromis entre profondeur de l'arbre et coût par feuille. Dans notre implémentation nous utilisons `DEFAULT_MAX_LEAF = 4`.

### Stratégie utilisée ici (SAH avec fallback médian)

Dans `ray_tracer/geometry/accel/BVHNode.java` j'ai implémenté une stratégie SAH (Surface Area Heuristic) simple basée sur du binning, avec un fallback vers un split médian lorsque la coupe SAH n'est pas profitable.

- Construction récursive : on calcule la boîte englobante (AABB) du nœud courant.
- Si le nombre de primitives <= `maxLeafSize`, on crée une feuille contenant la liste des `Shape`.
- Sinon on tente une partition basée sur SAH via binning :

  - On choisit l'axe dont l'étendue des centroïdes est la plus grande.
  - On répartit les primitives dans `NUM_SAH_BUCKETS` buckets selon la coordonnée du centroïde le long de cet axe.
  - Pour chaque séparation possible entre buckets on calcule une estimation de coût SAH :

    cost(split) = traversalCost + (area(left) _ count(left) + area(right) _ count(right)) / area(node)

    Dans le code la constante `traversalCost` est simplifiée à `1.0` et le coût d'intersection par primitive est également normalisé à `1.0`, ce qui rend la formule implémentée :

    cost = 1.0 + (leftArea _ leftCount + rightArea _ rightCount) / totalArea

  - On choisit la séparation minimisant `cost(split)` ; si aucune séparation n'est bénéfique (ou si une bucket split produit une partition vide d'un côté), on retombe sur un split médian des centroïdes.

Cette approche (SAH par binning) est un très bon compromis : elle capture l'essentiel des avantages de SAH (partitions de meilleure qualité, moins de chevauchements) tout en restant linéaire en pratique (O(N) pour remplir les buckets + coût logarithmique pour la récursion). Le code utilise `NUM_SAH_BUCKETS = 12` par défaut et `DEFAULT_MAX_LEAF = 4`.

Pourquoi SAH est utile :

- SAH favorise les partitions qui réduisent l'aire totale des boîtes enfants, donc diminue la probabilité qu'un rayon traverse les deux enfants. Moins de chevauchement → moins de tests de primitives.
- Sur des distributions inégales (gros objets côtoyant petits objets ou clusters), SAH produit souvent des arbres bien meilleurs que le split médian.

Coûts et compromis :

- Construction légèrement plus coûteuse que le split médian (remplir buckets, calculer areas), mais la traversée moyenne est généralement plus rapide.
- SAH peut être plus coûteux en mémoire temporaire (buckets) et un peu plus complexe à implémenter.

Dans ce projet j'ai choisi SAH-binning avec fallback médian afin d'obtenir un bon gain en temps de parcours sans complexifier l'outil de construction.

### Amélioration implémentée : exclusion des plans infinis

Problème : les plans sont des primitives géométriques mathématiquement infinies. Pour les inclure dans un BVH il faut leur donner une boîte englobante finie (`AABB`) — typiquement on pourrait choisir une très grande boîte. Mais ces grandes boîtes couvrent souvent une large portion de la scène et chevauchent de nombreuses autres boîtes : cela annule l'effet du BVH (beaucoup de tests de boîtes et peu d'élagage).

Solution choisie ici : exclure les `Plane` (ou plus généralement les primitives non-bornées) du BVH et les tester séparément.

- Lors de l'ajout d'une forme (`Scene.addShape`) : si la forme est une instance de `Plane` on la conserve dans une liste `unboundedShapes` séparée ; sinon elle entre dans la collection normale de formes.
- Lors de la construction du BVH (`Scene.buildAcceleration`) on filtre les formes bornées et on construit le BVH uniquement à partir de celles-ci.
- Lors d'une intersection (`Scene.intersect`) on interroge d'abord le BVH pour obtenir l'intersection la plus proche sur les formes bornées, puis on teste toutes les `unboundedShapes` (plans) et on compare les distances pour retourner la plus proche intersection globale.

Avantages :

- Évite d'introduire des boîtes énormes qui chevauchent la scène, ce qui maintient un BVH efficace et des partitions utiles.
- Tester explicitement quelques plans (s'il n'y en a pas beaucoup) est souvent beaucoup moins coûteux que dégrader tout le BVH.

Coûts et compromis :

- Chaque rayon doit tester explicitement la petite liste `unboundedShapes` en plus du BVH ; si tu as des milliers de plans, il faudra repenser la stratégie (par ex. regrouper les plans, culling par orientation, ou construire un BVH spécifique aux grandes surfaces).

Référence dans le code :

- `Scene.addShape(Shape shape)` : ajout conditionnel à `unboundedShapes`.
- `Scene.buildAcceleration()` : construit la BVH en filtrant `Plane`.
- `Scene.intersect(Ray ray)` : combine le résultat du BVH et les intersections sur `unboundedShapes`.

Effet sur la rapidité de l'API :

- En évitant que des primitives non-bornées ruinent la hiérarchie, la traversée du BVH reste efficace et les temps de rendu chutent de façon significative sur des scènes contenant peu de plans mais beaucoup d'autres primitives. C'est un excellent exemple de « prétraitement de la topologie » pour préserver la qualité d'une structure d'accélération.

## Parcours / Intersection

Algorithme général de parcours :

1. À l'appel `intersect(ray)` sur la racine, tester `box.hit(ray, tMax)`. Si faux, retourner aucun résultat.
2. Si c'est une feuille, tester le rayon contre chacune des primitives de la feuille et retourner la plus proche intersection trouvée (t minimal > EPSILON).
3. Si c'est un nœud interne, appeler récursivement sur l'enfant gauche puis droit (ou inversement), en passant `tMax = closestT` pour permettre l'élagage : si on a déjà trouvé une intersection à `t = t0`, on ne s'intéresse qu'aux intersections plus proches (t < t0) pour améliorer le pruning lors des tests de `AABB.hit`.

Optimisations fréquentes :

- Ordre des enfants : tester d'abord l'enfant dont la boîte est intersectée plus proche, afin d'obtenir rapidement un `closestT` réduit et élaguer l'autre enfant.
- Implémentation itérative avec une pile explicite (réduit le coût d'appel récursif et facilite certaines optimisations).

Dans notre implémentation `BVHNode.intersect(Ray ray, double tMax)` :

- On vérifie d'abord la boîte du nœud.
- Pour les feuilles on teste toutes les `Shape` et on choisit la plus proche.
- Pour les nœuds internes on interroge d'abord `left` puis `right` (on pourrait choisir dynamiquement l'ordre en évaluant l'intersection des boîtes).

## Complexité & Gains de performance

- Construction (split médian simple) : O(N log N) en pratique (tri récurrent), ou O(N log^2 N) selon implémentation du tri. SAH peut être O(N log N) avec implémentations optimisées.
- Requête (intersection d'un rayon) : en moyenne bien mieux que O(N). En pratique, le coût est ~O(log N) pour l'arbre + O(k) primitives traitées dans les feuilles, où k dépend du paramétrage et de la topologie.

Le facteur de réduction dépend fortement de la scène (distribution des primitives) et de la qualité de la partition. Scènes bien partitionnables bénéficient énormément.

## Intégration dans ce ray tracer

Voici les éléments concrets implémentés et leur rôle :

- `src/main/java/ray_tracer/geometry/AABB.java`

  - Contient la classe `AABB` avec `min`, `max`, `surroundingBox(a,b)`, `centroid()` et `hit(Ray r, double tMax)` (méthode du slab).

- `src/main/java/ray_tracer/geometry/accel/BVHNode.java`

  - Implémentation du BVH : construction récursive (`build` / `buildRecursive`) et méthode d'intersection `intersect(ray)`.
  - Feuilles contiennent jusqu'à `DEFAULT_MAX_LEAF = 4` primitives.

- `src/main/java/ray_tracer/geometry/shapes/Shape.java` (modifié)

  - Ajout d'une méthode abstraite `public abstract ray_tracer.geometry.AABB getBounds();` que chaque shape doit fournir.

- `src/main/java/ray_tracer/geometry/shapes/*` (modifié)

  - `Sphere.java` : implémentation de `getBounds()` par min=max=center ± radius.
  - `Triangle.java` : `getBounds()` donné par min/max des trois sommets.
  - `Plane.java` : plan infini approximé par une grosse AABB centrée au point du plan (extent = 1e6). Ce choix est pragmatique mais peut être sous-optimal — voir plus bas.

- `src/main/java/ray_tracer/parsing/Scene.java` (modifié)
  - Ajout d'un champ `BVHNode bvhRoot` et modification de `intersect(Ray ray)` pour :
    - reconstruire paresseusement la BVH si `dirty` ou `bvhRoot == null` (appel de `buildAcceleration()`),
    - utiliser `bvhRoot.intersect(ray)` comme parcours principal,
    - fallback en mode brut-force si BVH absent.
  - `addShape(Shape shape)` marque la scène comme `dirty = true` afin de rebuild la BVH au prochain `intersect`.
  - `buildAcceleration()` construit la BVH : `this.bvhRoot = BVHNode.build(this.shapes);` et `this.dirty=false`.

### Extraits utiles

- Signature Shape.getBounds :

```java
// dans Shape.java
public abstract ray_tracer.geometry.AABB getBounds();
```

- Appel de construction dans `Scene` :

```java
public synchronized void buildAcceleration() {
    this.bvhRoot = BVHNode.build(this.shapes);
    this.dirty = false;
}
```

- Test boite/raion (extrait simplifié du `AABB.hit`) :

```java
// Pour chaque axe:
if (abs(dir) < eps) {
    if (origin < min || origin > max) return false;
} else {
    invD = 1.0 / dir;
    t0 = (min - origin) * invD;
    t1 = (max - origin) * invD;
    if (invD < 0) swap(t0,t1);
    tmin = max(tmin, t0);
    tmax = min(tmax, t1);
    if (tmax <= tmin) return false;
}
```

## Points d'attention dans cette intégration

1. Précision numérique :

   - Tests du type `Math.abs(discriminant) < EPS` ou divisions par 0 dans `AABB.hit` demandent de choisir des epsilons raisonnables. Le slab method gère `dir` proche de zéro en testant l'origine par rapport aux bornes.

2. Scènes dynamiques :

   - Le BVH ici est construit pour des scènes statiques. Si vous bougez fréquemment les objets à chaque image, il faudra :
     - soit reconstruire entièrement le BVH (coût élevé),
     - soit utiliser une structure dynamique (refit rapide des boîtes) ou BVH top-down réordonnable.

3. Construction paresseuse :

   - `Scene.intersect()` reconstruit la BVH si `dirty`. Avantage : pas de reconstruction inutile si on charge une scène puis on l'interroge.

4. Partage d'objets/copyForRender :
   - `Scene.copyForRender()` retourne une copie superficielle des listes `shapes` et `lights`. Si vous construisez la BVH dans une `Scene` et que vous partagez les mêmes objets dans plusieurs threads, vous devez vous assurer que la construction/lecture est thread-safe. Dans le code, `buildAcceleration()` est `synchronized`.

## Concurrence et rendu

- `Scene.buildAcceleration()` est `synchronized` pour éviter des reconstructions concurrentes.
- Si vous lancez un rendu multi-thread (par pixel ou par bloc), il est préférable de construire le BVH une seule fois (par ex. avant le rendu) et rendre la scène immuable ensuite. Le BVH est en lecture seule pendant le rendu, donc les threads peuvent l'utiliser sans verrou supplémentaires.

## Références et lectures recommandées

- Ingo Wald, "On fast construction of SAH-based bounding volume hierarchies" (et autres papiers sur SAH).
- PBRT (Physically Based Rendering) book — chapitre sur acceleration structures.
- Articles/Tutoriels sur "BVH construction" et "Ray-box intersection slab method".

---

## Où chercher dans le code (raccourcis)

- `src/main/java/ray_tracer/geometry/AABB.java` — définition et `hit`
- `src/main/java/ray_tracer/geometry/accel/BVHNode.java` — construction et traversal
- `src/main/java/ray_tracer/geometry/shapes/Shape.java` — nouveau `getBounds()`
- `src/main/java/ray_tracer/geometry/shapes/Sphere.java`, `Triangle.java`, `Plane.java` — implémentations de `getBounds()`
- `src/main/java/ray_tracer/parsing/Scene.java` — intégration du BVH (rebuild paresseux, utilisation dans `intersect`)
