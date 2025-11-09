#!/usr/bin/env bash
set -euo pipefail

# run_scenes.sh
# Utilité : à partir de la valeur ACTUAL_JALON dans .env, exécute le JAR
# pour tous les fichiers .scene et .test présents dans
# src/main/resources/scenes/<ACTUAL_JALON>
#
# Comportement :
# - charge le fichier .env (s'il existe) et lit ACTUAL_JALON
# - normalise (ex : 4 -> jalon4)
# - détecte un JAR dans target/ray_tracer-*.jar (ou demande de build)
# - exécute pour chaque fichier .scene/.test :
#     java -cp <jar> <MAIN_CLASS> <fichier>
# - MAIN_CLASS peut être redéfini dans .env (par défaut ray_tracer.Main)

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$SCRIPT_DIR/.env"

if [ -f "$ENV_FILE" ]; then
  # shellcheck disable=SC1090
  source "$ENV_FILE"
fi

if [ -z "${ACTUAL_JALON:-}" ]; then
  echo "Erreur: ACTUAL_JALON non défini dans $ENV_FILE."
  echo "Définissez ACTUAL_JALON=jalon4 (ou 4) dans $ENV_FILE puis relancez." >&2
  exit 1
fi

# Normaliser la valeur (autorise '4' ou 'jalon4')
jalon="$ACTUAL_JALON"
if [[ "$jalon" =~ ^[0-9]+$ ]]; then
  jalon="jalon$jalon"
fi

SCENES_DIR="$SCRIPT_DIR/src/main/resources/scenes/$jalon"
if [ ! -d "$SCENES_DIR" ]; then
  echo "Erreur: répertoire de scènes introuvable : $SCENES_DIR" >&2
  exit 2
fi

# détecter le JAR (si plusieurs, prend le premier trié)
JAR_PATH=""
shopt -s nullglob
for j in "$SCRIPT_DIR"/target/ray_tracer-*.jar; do
  JAR_PATH="$j"
  break
done
shopt -u nullglob

if [ -z "$JAR_PATH" ]; then
  echo "Aucun JAR trouvé dans $SCRIPT_DIR/target (pattern: ray_tracer-*.jar)." >&2
  echo "Construisez d'abord le projet : mvn package" >&2
  exit 3
fi

# Classe main par défaut (modifiable via .env : MAIN_CLASS=...)
MAIN_CLASS="${MAIN_CLASS:-ray_tracer.Main}"

echo "ACTUAL_JALON = $ACTUAL_JALON -> utilisation du dossier: $SCENES_DIR"
echo "JAR utilisé : $JAR_PATH"
echo "Main class   : $MAIN_CLASS"
echo

# emplacement possible du comparateur d'images (modifiable si besoin)
IMGCOMPARE_JAR="$SCRIPT_DIR/../imgcompare/imgcompare.jar"

# dossier pour stocker les sorties de comparaison
OUTPUT_DIR="$SCRIPT_DIR/outputs"
mkdir -p "$OUTPUT_DIR"

# Rassembler la liste des fichiers .scene et .test
mapfile -d $'\0' files < <(find "$SCENES_DIR" -maxdepth 1 -type f \( -iname "*.scene" -o -iname "*.test" \) -print0 | sort -z)

if [ ${#files[@]} -eq 0 ]; then
  echo "Aucun fichier .scene ou .test trouvé dans $SCENES_DIR" >&2
  exit 4
fi

echo "Fichiers trouvés :"
for f in "${files[@]}"; do
  echo " - $f"
done
echo

# Exécuter chaque fichier
for f in "${files[@]}"; do
  echo "================================================================"
  echo "Fichier : $f"
  echo "Commande: java -cp '$JAR_PATH' $MAIN_CLASS '$f'"
  echo "----------------------------------------------------------------"

  # Exécuter et afficher la sortie en direct. On capture le code retour.
  set +e
  java -cp "$JAR_PATH" "$MAIN_CLASS" "$f"
  rc=$?
  set -e

  echo "----------------------------------------------------------------"
  echo "Exit code: $rc"
  echo

  # Si c'est un fichier .test, tenter une comparaison d'image
  fname_lower="${f,,}"
  if [[ "$fname_lower" == *.test ]]; then
    base="$(basename "$f" .test)"
    # fichier produit attendu (dans le répertoire du projet)
    produced="$SCRIPT_DIR/$base.png"
    # image attendue dans le dossier scenes
    expected="$SCENES_DIR/$base.png"

    echo "Comparaison d'image pour le test: $base"

    if [ ! -f "$produced" ]; then
      echo "  Produit absent : $produced (le programme n'a pas généré $base.png)"
      continue
    fi

    if [ ! -f "$expected" ]; then
      echo "  Image attendue introuvable : $expected"
      continue
    fi

    if [ ! -f "$IMGCOMPARE_JAR" ]; then
      echo "  Comparateur d'images introuvable : $IMGCOMPARE_JAR" >&2
      echo "  Pour l'utiliser, placez imgcompare.jar dans ../imgcompare/ ou modifiez IMGCOMPARE_JAR dans le script."
      continue
    fi

    out_compare="$OUTPUT_DIR/output_${base}.png"
    echo "  Commande: java -jar '$IMGCOMPARE_JAR' '$produced' '$expected' '$out_compare'"
    set +e
    java -jar "$IMGCOMPARE_JAR" "$produced" "$expected" "$out_compare"
    cmp_rc=$?
    set -e

    echo "  Résultat comparaison (exit code): $cmp_rc"
    if [ $cmp_rc -eq 0 ]; then
      echo "  -> Images identiques. Résultat enregistré: $out_compare"
    else
      echo "  -> Diff trouvé (ou erreur). Résultat enregistré: $out_compare"
    fi
    echo
  fi
done

echo "Terminé." 
