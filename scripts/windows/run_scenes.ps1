# run_scenes.ps1 - PowerShell port du script run_scenes.sh
# Usage: PowerShell.exe -ExecutionPolicy Bypass -File .\run_scenes.ps1

$ErrorActionPreference = 'Stop'

function Write-Info($m){ Write-Host $m -ForegroundColor Cyan }
function Write-Ok($m){ Write-Host $m -ForegroundColor Green }
function Write-Warn($m){ Write-Host $m -ForegroundColor Yellow }
function Write-Err($m){ Write-Host $m -ForegroundColor Red }

$ScriptDir = Split-Path -Path $MyInvocation.MyCommand.Definition -Parent
# remonter de 2 niveaux (de `scripts\windows` -> `scripts` -> projet root)
$ScriptDir = Split-Path -Path $ScriptDir -Parent
$ScriptDir = Split-Path -Path $ScriptDir -Parent
$EnvFile = Join-Path $ScriptDir '.env'
$JalonEnvFile = Join-Path $ScriptDir 'jalon.env'

$TestsAutoDir = Join-Path $ScriptDir 'tests_auto'
New-Item -ItemType Directory -Force -Path $TestsAutoDir | Out-Null

$LoadedEnvFiles = @()
function Parse-EnvFile($path){
    if (-not (Test-Path $path)) { return }
    Get-Content $path | ForEach-Object {
        $line = $_.Trim()
        if ($line -eq '' -or $line.StartsWith('#')) { return }
        $parts = $line -split '=',2
        if ($parts.Count -lt 2) { return }
        $key = $parts[0].Trim()
        $value = $parts[1].Trim()
        if (($value.StartsWith('"') -and $value.EndsWith('"')) -or ($value.StartsWith("'") -and $value.EndsWith("'"))) {
            $value = $value.Substring(1,$value.Length-2)
        }
        Set-Variable -Name $key -Value $value -Scope Script -Force
        Set-Item -Path "Env:$key" -Value $value -Force
    }
    $LoadedEnvFiles += $path
}

Parse-EnvFile $EnvFile
Parse-EnvFile $JalonEnvFile

if ($LoadedEnvFiles.Count -eq 0) { Write-Warn "Aucun fichier d'environnement chargé (ni $EnvFile ni $JalonEnvFile)." }
else { Write-Info "Fichiers d'environnement chargés : $($LoadedEnvFiles -join ', ')" }
Write-Info "Valeurs: ACTUAL_JALON='${ACTUAL_JALON}'  MAIN_CLASS='${MAIN_CLASS}'"

if (-not $Script:ACTUAL_JALON -or $Script:ACTUAL_JALON.Trim() -eq ''){
    Write-Err "Erreur: ACTUAL_JALON non défini dans $EnvFile ou $JalonEnvFile."
    Write-Err "Définissez ACTUAL_JALON=jalon4 (ou 4) dans $EnvFile puis relancez."
    exit 1
}

$jalon = $Script:ACTUAL_JALON
if ($jalon -match '^[0-9]+$') { $jalon = "jalon$jalon" }

$ScenesDir = Join-Path $ScriptDir "src\main\resources\scenes\$jalon"
if (-not (Test-Path $ScenesDir)){
    Write-Err "Erreur: répertoire de scènes introuvable : $ScenesDir"
    exit 2
}

# détecter le JAR
$TargetDir = Join-Path $ScriptDir 'target'
$JarPath = $null
if (Test-Path $TargetDir){
    $jars = Get-ChildItem -Path $TargetDir -Filter 'ray_tracer-*.jar' -File -ErrorAction SilentlyContinue | Sort-Object Name
    if ($jars.Count -gt 0) { $JarPath = $jars[0].FullName }
}

# Build Maven before running tests (log dans tests_auto/.mvn_build.log)
Write-Info "Lancement de la build Maven (mvn package) dans $ScriptDir ..."
$MvnLogFile = Join-Path $TestsAutoDir '.mvn_build.log'
# Détecter la commande Maven : preferer mvn, sinon utiliser mvnw.cmd si présent (wrapper Windows)
$MavenCmd = $null
if (Get-Command mvn -ErrorAction SilentlyContinue) {
    $MavenCmd = 'mvn'
} else {
    $MvnwCmd = Join-Path $ScriptDir 'mvnw.cmd'
    if (Test-Path $MvnwCmd) {
        $MavenCmd = $MvnwCmd
    } else {
        Write-Err "Erreur : 'mvn' introuvable dans le PATH et 'mvnw.cmd' absent. Installez Maven ou ajoutez mvnw.cmd au projet."
        exit 6
    }
}

try{
    Push-Location $ScriptDir
    if ($null -eq $JarPath) {
        # lance mvn package et cache la sortie
        & $MavenCmd -q package > $MvnLogFile 2>&1
        $mvn_rc = $LASTEXITCODE
        if ($mvn_rc -ne 0){
            Write-Err "Erreur : mvn package a échoué (code $mvn_rc). Voir le log : $MvnLogFile"
            Write-Err "----- Dernières lignes du log Maven -----"
            Get-Content $MvnLogFile -Tail 200 | ForEach-Object { Write-Err $_ }
            Pop-Location
            exit 5
        }
        # retenter détection du JAR
        if (Test-Path $TargetDir){
            $jars = Get-ChildItem -Path $TargetDir -Filter 'ray_tracer-*.jar' -File -ErrorAction SilentlyContinue | Sort-Object Name
            if ($jars.Count -gt 0) { $JarPath = $jars[0].FullName }
        }
    }
} finally { Pop-Location }

if (-not $JarPath){
    Write-Err "Aucun JAR trouvé dans $ScriptDir/target (pattern: ray_tracer-*.jar)."
    Write-Err "Construisez d'abord le projet : mvn package"
    exit 3
}

# Classe main par défaut
if (-not $Script:MAIN_CLASS -or $Script:MAIN_CLASS.Trim() -eq ''){ $Script:MAIN_CLASS = 'ray_tracer.Main' }

Write-Info "ACTUAL_JALON = $Script:ACTUAL_JALON -> utilisation du dossier: $ScenesDir"
Write-Info "JAR utilisé : $JarPath"
Write-Info "Main class   : $Script:MAIN_CLASS"

# comparateur d'images (peut être défini dans .env)
if (-not $Script:IMGCOMPARE_JAR -or $Script:IMGCOMPARE_JAR.Trim() -eq ''){
    $ImgDefault = Join-Path $ScriptDir '..\imgcompare\imgcompare.jar'
    $IMGCOMPARE_JAR = $ImgDefault
} else { $IMGCOMPARE_JAR = $Script:IMGCOMPARE_JAR }

# dossier pour sorties
$OutputDir = Join-Path $ScriptDir 'outputs'
New-Item -ItemType Directory -Force -Path $OutputDir | Out-Null

# Rassembler fichiers .scene et .test (top level)
# Note: -Include fonctionne correctement quand Path contient un wildcard or when -Recurse is used.
# On ajoute '\*' à Path pour limiter à l'étage courant tout en supportant -Include.
$files = Get-ChildItem -Path (Join-Path $ScenesDir '*') -File -Include *.scene,*.test -ErrorAction SilentlyContinue | Sort-Object Name
if ($files.Count -eq 0){ Write-Err "Aucun fichier .scene ou .test trouvé dans $ScenesDir"; exit 4 }

Write-Info "Fichiers trouvés :"
foreach ($f in $files){ Write-Host "  - $($f.FullName)" -ForegroundColor Yellow }

foreach ($f in $files){
    Write-Host ('=' * 70) -ForegroundColor Cyan
    Write-Host "Fichier : $($f.FullName)" -ForegroundColor Cyan
    Write-Host "Commande: java -cp '$JarPath' $($Script:MAIN_CLASS) '$($f.FullName)'" -ForegroundColor Cyan

    & java -cp $JarPath $Script:MAIN_CLASS $f.FullName
    $rc = $LASTEXITCODE
    if ($rc -eq 0){ Write-Ok "Exit code: $rc" } else { Write-Err "Exit code: $rc" }

    $fname_lower = $f.Name.ToLower()
    if ($fname_lower.EndsWith('.test')){
        $base = [System.IO.Path]::GetFileNameWithoutExtension($f.Name)
        $produced = Join-Path $ScriptDir "$base.png"
        $expected = Join-Path $ScenesDir "$base.png"

        Write-Host "Comparaison d'image pour le test: $base"
        if (-not (Test-Path $produced)){ Write-Host "  Produit absent : $produced (le programme n'a pas généré $base.png)"; continue }
        if (-not (Test-Path $expected)){ Write-Host "  Image attendue introuvable : $expected"; continue }
        if (-not (Test-Path $IMGCOMPARE_JAR)){ Write-Err "  Comparateur d'images introuvable : $IMGCOMPARE_JAR"; Write-Host "  Pour l'utiliser, placez imgcompare.jar dans ..\imgcompare\ ou modifiez IMGCOMPARE_JAR dans .env."; continue }

        $out_compare = Join-Path $OutputDir "output_${base}.png"
        Write-Host "  Commande: java -jar '$IMGCOMPARE_JAR' '$produced' '$expected' '$out_compare'"
        & java -jar $IMGCOMPARE_JAR $produced $expected $out_compare
        $cmp_rc = $LASTEXITCODE
        Write-Host "  Résultat comparaison (exit code): $cmp_rc"
        if ($cmp_rc -eq 0){ Write-Host "  -> Images identiques. Résultat enregistré: $out_compare" } else { Write-Host "  -> Diff trouvé (ou erreur). Résultat enregistré: $out_compare" }

        $DestDir = Join-Path $TestsAutoDir "$jalon\$base"
        New-Item -ItemType Directory -Force -Path $DestDir | Out-Null

        Copy-Item -Path $f.FullName -Destination (Join-Path $DestDir "$base.test") -Force
        if (Test-Path $expected){ Copy-Item -Path $expected -Destination (Join-Path $DestDir "$base-model.png") -Force }
        if (Test-Path $produced){ Move-Item -Path $produced -Destination (Join-Path $DestDir "$base-genere.png") -Force }
        if (Test-Path $out_compare){ Move-Item -Path $out_compare -Destination (Join-Path $DestDir "$base-comparison.png") -Force }

        Write-Host "  Fichiers copiés dans: $DestDir"
    }
}

Remove-Item -Recurse -Force $OutputDir -ErrorAction SilentlyContinue
Write-Info "Terminé."
