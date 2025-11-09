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

# Couleurs pour sortie terminal
NC="\033[0m"       # No Color
RED="\033[0;31m"
GREEN="\033[0;32m"
YELLOW="\033[0;33m"
BLUE="\033[0;34m"
CYAN="\033[0;36m"
WHITE="\033[1;37m"

info(){ echo -e "${BLUE}$*${NC}"; }
ok(){ echo -e "${GREEN}$*${NC}"; }
warn(){ echo -e "${YELLOW}$*${NC}"; }
err(){ echo -e "${RED}$*${NC}" >&2; }
cmd(){ echo -e "${CYAN}$*${NC}"; }

if [ -z "${ACTUAL_JALON:-}" ]; then
  err "Erreur: ACTUAL_JALON non défini dans $ENV_FILE."
  err "Définissez ACTUAL_JALON=jalon4 (ou 4) dans $ENV_FILE puis relancez."
  exit 1
fi

# Normaliser la valeur (autorise '4' ou 'jalon4')
jalon="$ACTUAL_JALON"
if [[ "$jalon" =~ ^[0-9]+$ ]]; then
  jalon="jalon$jalon"
fi

SCENES_DIR="$SCRIPT_DIR/src/main/resources/scenes/$jalon"
if [ ! -d "$SCENES_DIR" ]; then
  err "Erreur: répertoire de scènes introuvable : $SCENES_DIR"
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

# Build Maven before running tests (cache la sortie dans .mvn_build.log)
info "Lancement de la build Maven (mvn package) dans $SCRIPT_DIR ..."
pushd "$SCRIPT_DIR" >/dev/null
MVN_LOG_FILE="$SCRIPT_DIR/.mvn_build.log"
rm -f "$MVN_LOG_FILE"
if command -v mvn >/dev/null 2>&1; then
  # Exécute en silencieux et redirige toute la sortie vers le fichier de log
  mvn -q package >"$MVN_LOG_FILE" 2>&1
  mvn_rc=$?
  if [ $mvn_rc -ne 0 ]; then
    echo "Erreur : mvn package a échoué (code $mvn_rc). Voir le log : $MVN_LOG_FILE" >&2
    echo "----- Dernières lignes du log Maven -----" >&2
    tail -n 200 "$MVN_LOG_FILE" >&2 || true
    popd >/dev/null
    exit 5
  fi
else
  err "Erreur : 'mvn' introuvable dans le PATH. Installez Maven ou exécutez manuellement 'mvn package'."
  popd >/dev/null
  exit 6
fi
popd >/dev/null

if [ -z "$JAR_PATH" ]; then
  err "Aucun JAR trouvé dans $SCRIPT_DIR/target (pattern: ray_tracer-*.jar)."
  err "Construisez d'abord le projet : mvn package"
  exit 3
fi

# Classe main par défaut (modifiable via .env : MAIN_CLASS=...)
MAIN_CLASS="${MAIN_CLASS:-ray_tracer.Main}"

info "ACTUAL_JALON = $ACTUAL_JALON -> utilisation du dossier: $SCENES_DIR"
cmd "JAR utilisé : $JAR_PATH"
cmd "Main class   : $MAIN_CLASS"
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

info "Fichiers trouvés :"
for f in "${files[@]}"; do
  echo -e "  ${YELLOW}- ${f}${NC}"
done
echo

# Exécuter chaque fichier
for f in "${files[@]}"; do
  echo -e "${CYAN}================================================================${NC}"
  echo -e "${CYAN}Fichier : ${WHITE:-}$f${NC}"
  cmd "Commande: java -cp '$JAR_PATH' $MAIN_CLASS '$f'"
  echo -e "${CYAN}----------------------------------------------------------------${NC}"

  # Exécuter et afficher la sortie en direct. On capture le code retour.
  set +e
  java -cp "$JAR_PATH" "$MAIN_CLASS" "$f"
  rc=$?
  set -e
  echo -e "${CYAN}----------------------------------------------------------------${NC}"
  if [ $rc -eq 0 ]; then
    ok "Exit code: $rc"
  else
    err "Exit code: $rc"
  fi
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

    # Rassembler les fichiers générés dans tests_auto/<jalon>/<base>/
    TESTS_AUTO_DIR="$SCRIPT_DIR/tests_auto"
    DEST_DIR="$TESTS_AUTO_DIR/$jalon/$base"
    mkdir -p "$DEST_DIR"

    # Copier le fichier .test original
    cp -f "$f" "$DEST_DIR/${base}.test"

    # Copier l'image modèle (attendue) si présente
    if [ -f "$expected" ]; then
      cp -f "$expected" "$DEST_DIR/${base}-model.png"
    fi

    # Déplacer l'image générée (produced)
    if [ -f "$produced" ]; then
      mv -f "$produced" "$DEST_DIR/${base}-genere.png"
    fi

    # Déplacer le résultat de comparaison
    if [ -f "$out_compare" ]; then
      mv -f "$out_compare" "$DEST_DIR/${base}-comparison.png"
    fi

    echo "  Fichiers copiés dans: $DEST_DIR"
  fi
done

rm -r "$OUTPUT_DIR"
mv "$MVN_LOG_FILE" "$TESTS_AUTO_DIR"

echo "Terminé." 
