# Ray Tracer — Projet

Ce dépôt contient un petit moteur de ray tracing écrit en Java, utilisé pour des travaux pratiques/jalons. Il permet de parser des fichiers de scène (.scene / .test) et de produire des images PNG via un rendu par lancer de rayons.

**Résumé**

- **Langage :** Java
- **Build :** Maven
- **Entrée :** fichiers `.scene` dans `src/main/resources/scenes/` (ex. `final.scene`)
- **Sortie :** image PNG indiquée par la directive `output` dans le fichier `.scene` (ex. `dragon3.png`).

**Compiler le projet (Maven)**

1. Depuis la racine du projet, exécutez :

```bash
mvn package
```

2. Après la compilation, un JAR exécutable est disponible dans `target/` (pattern : `target/ray_tracer-*.jar`).

3. Lancer une scène manuellement (ex : `final.scene`) :

```bash
java -cp target/ray_tracer-*.jar ray_tracer.Main src/main/resources/scenes/final.scene
```

Le programme parse le fichier `.scene`, construit la scène (caméra, lumières, formes, matériaux, profondeur de récursion, etc.), puis écrit l'image dans le fichier précisé par la directive `output` du `.scene`. Le programme propose également des warnings sur la composition du fichier de scene pour aider l'utilisateur à comprendre un mauvais rendu ou une erreur. Ces warnings sont écrits dans la console.

**Scripts fournis**
Le dépôt inclut plusieurs scripts pour faciliter l'exécution. Choisissez le script adapté à votre système d'exploitation (Linux/macOS → script `*.sh`; Windows/PowerShell → script `*.ps1`).

- `run_scenes.sh` :

  - Emplacement : `./scripts/linux/run_scenes.sh`
  - Objectif : build le projet (via `mvn package`) puis exécute la `Main` pour chaque fichier `.scene` et `.test` trouvé dans le dossier de scènes du jalon courant (défini par la variable `ACTUAL_JALON`).
  - Comportement supplémentaire pour les fichiers `.test` : compare l'image produite avec une image modèle (si un comparateur Java `IMGCOMPARE_JAR` est indiqué) et organise les résultats dans `tests_auto/<jalon>/<test>/`.

- `run_final.sh` :
  - Emplacement : `./scripts/linux/run_final.sh`
  - Objectif : build si nécessaire et lancer le rendu de `src/main/resources/scenes/final.scene` (ou d'un fichier `.scene` passé en argument).
  - Usage :

```bash
./run_final.sh                # lance final.scene
./run_final.sh mon.scene      # lance mon.scene
```

- `run_final.ps1` :
  - Emplacement : `.\scripts\windows\run_final.ps1`
  - Objectif : même fonction que `run_final.sh` mais pour PowerShell/Windows.
  - Usage (PowerShell) :

```powershell
# depuis le dossier du projet
.\run_final.ps1
.\run_final.ps1 -Scene 'src\main\resources\scenes\final.scene'
```

Remarques sur les scripts

- Les scripts lisent, si présents, les fichiers d'environnement `./.env` puis `./jalon.env`. Ces fichiers peuvent définir :
  - `ACTUAL_JALON` : numéro ou nom du dossier de scènes (ex : `6` ou `jalon6`).
  - `MAIN_CLASS` : classe principale Java à exécuter (par défaut `ray_tracer.Main`).
  - `IMGCOMPARE_JAR` : chemin vers un comparateur d'images si vous utilisez la comparaison automatique pour les `.test`.
- Si aucun JAR n'est trouvé dans `target/`, les scripts tenteront d'exécuter `mvn package` (si `mvn` est disponible) pour reconstruire le projet.
- Les images produites sont écrites selon la directive `output` dans chaque fichier `.scene`. Si `output` indique un nom relatif (ex. `dragon3.png`), l'image sera créée dans le répertoire courant d'exécution.

**Exemples d'utilisation rapides**

- Compiler puis lancer `final.scene` :

```bash
mvn package
java -cp target/ray_tracer-*.jar ray_tracer.Main src/main/resources/scenes/final.scene
```

- Lancer toutes les scènes du jalon configuré (via `jalon.env` ou `.env`) :

```bash
./run_scenes.sh
```

- Lancer uniquement `final.scene` (script bash) :

```bash
./run_final.sh
```

- Lancer `final.scene` sous PowerShell :

```powershell
.\run_final.ps1
```

**Dépannage**

- Si `mvn` n'est pas installé, exécutez manuellement `mvn package` sur une machine où Maven est disponible, ou installez Maven avant d'utiliser les scripts.
- Si le programme échoue à parser une scène, la sortie d'erreur indique la ligne et la cause (vérifiez la syntaxe du `.scene`).
- Si `run_scenes.sh` ne trouve pas de scènes, vérifiez la valeur de `ACTUAL_JALON` dans `jalon.env` / `.env` et que le dossier `src/main/resources/scenes/jalonX` existe.

**Organisation & contribution**

- Le code source se trouve dans `src/main/java`.
- Les scènes d'exemple sont dans `src/main/resources/scenes/`.
- Les résultats automatiques de tests sont rassemblés dans `tests_auto/` par le script `run_scenes.sh`.
