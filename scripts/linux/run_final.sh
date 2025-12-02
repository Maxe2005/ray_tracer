#!/usr/bin/env bash
set -euo pipefail

# run_final.sh
# Usage:
#   ./run_final.sh                # lance src/main/resources/scenes/final.scene
#   ./run_final.sh path/to/scene  # lance le fichier scene donné

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="$SCRIPT_DIR/.env"
JALON_ENV_FILE="$SCRIPT_DIR/jalon.env"

# charger .env et jalon.env si présents (permet d'utiliser MAIN_CLASS ou autres variables)
if [ -f "$ENV_FILE" ]; then
  # shellcheck disable=SC1090
  source "$ENV_FILE"
fi
if [ -f "$JALON_ENV_FILE" ]; then
  # shellcheck disable=SC1090
  source "$JALON_ENV_FILE"
fi

SCENE_ARG="${1:-}"
DEFAULT_SCENE="$SCRIPT_DIR/src/main/resources/scenes/final.scene"
SCENE_PATH="${SCENE_ARG:-$DEFAULT_SCENE}"

MAIN_CLASS="${MAIN_CLASS:-ray_tracer.Main}"

if [ ! -f "$SCENE_PATH" ]; then
  echo "Erreur: fichier scène introuvable: $SCENE_PATH" >&2
  exit 3
fi

# détecter JAR
JAR_PATH=""
shopt -s nullglob
for j in "$SCRIPT_DIR"/target/ray_tracer-*.jar; do
  JAR_PATH="$j"
  break
done
shopt -u nullglob

if [ -z "$JAR_PATH" ]; then
  echo "Aucun JAR trouvé dans $SCRIPT_DIR/target — lancement de 'mvn package'..."
  if command -v mvn >/dev/null 2>&1; then
    pushd "$SCRIPT_DIR" >/dev/null
    mvn -q package || { echo "Erreur: mvn package a échoué." >&2; popd >/dev/null; exit 1; }
    popd >/dev/null
    shopt -s nullglob
    for j in "$SCRIPT_DIR"/target/ray_tracer-*.jar; do
      JAR_PATH="$j"
      break
    done
    shopt -u nullglob
    if [ -z "$JAR_PATH" ]; then
      echo "Erreur: après build aucun JAR trouvé." >&2
      exit 4
    fi
  else
    echo "Erreur: 'mvn' introuvable. Construisez le projet manuellement (mvn package)." >&2
    exit 2
  fi
fi

echo "Utilisation du JAR : $JAR_PATH"
echo "Lancement : java -cp '$JAR_PATH' $MAIN_CLASS '$SCENE_PATH'"

java -cp "$JAR_PATH" "$MAIN_CLASS" "$SCENE_PATH"
rc=$?
if [ $rc -ne 0 ]; then
  echo "Processus terminé avec code: $rc" >&2
  exit $rc
fi

echo "Terminé. L'image est écrite selon la directive 'output' du fichier .scene (ex: dragon3.png)."
