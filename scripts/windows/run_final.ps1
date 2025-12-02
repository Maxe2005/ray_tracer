<#
run_final.ps1
Usage:
  .\run_final.ps1            # lance src/main/resources/scenes/final.scene
  .\run_final.ps1 -Scene foo.scene
#>
[CmdletBinding()]
param(
    [string]$Scene
)

# Si aucun `Scene` fourni, on le construira après avoir calculé `ScriptDir`
$ErrorActionPreference = 'Stop'
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
# remonter de 2 niveaux (de `scripts\windows` -> `scripts` -> projet root)
$ScriptDir = Split-Path -Path $ScriptDir -Parent
$ScriptDir = Split-Path -Path $ScriptDir -Parent
$EnvFile = Join-Path $ScriptDir '.env'
$JalonEnv = Join-Path $ScriptDir 'jalon.env'

# Charger les fichiers .env sans les dot-sourcer (évite d'exécuter/ouvrir le fichier)
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
        if ($value.StartsWith('"') -and $value.EndsWith('"')) { $value = $value.Substring(1,$value.Length-2) }
        Set-Variable -Name $key -Value $value -Scope Script -Force
        Set-Item -Path "Env:$key" -Value $value -Force
    }
    $LoadedEnvFiles += $path
}

Parse-EnvFile $EnvFile
Parse-EnvFile $JalonEnv

# si non fourni en paramètre, construire le chemin de la scène à partir de la racine projet
if (-not $Scene -or $Scene.Trim() -eq '') {
    $Scene = Join-Path $ScriptDir 'src\main\resources\scenes\final.scene'
}

if (-not (Test-Path $Scene)) {
    Write-Error "Fichier scène introuvable : $Scene"
    exit 3
}

$MainClass = if ($env:MAIN_CLASS) { $env:MAIN_CLASS } else { 'ray_tracer.Main' }

# trouver un JAR ray_tracer-*.jar dans target
$targetDir = Join-Path $ScriptDir 'target'
$jar = Get-ChildItem -Path $targetDir -Filter 'ray_tracer-*.jar' -File -ErrorAction SilentlyContinue | Select-Object -First 1
if (-not $jar) {
    if (Get-Command mvn -ErrorAction SilentlyContinue) {
        Write-Host "Aucun JAR trouvé : exécution de 'mvn package'..."
        Push-Location $ScriptDir
        & mvn package
        Pop-Location
        $jar = Get-ChildItem -Path $targetDir -Filter 'ray_tracer-*.jar' -File | Select-Object -First 1
    } else {
        Write-Error "mvn introuvable. Construisez le projet manuellement (mvn package)."
        exit 2
    }
}

if (-not $jar) {
    Write-Error "Aucun JAR trouvé après build."
    exit 4
}

$jarPath = $jar.FullName
Write-Host "Utilisation du JAR : $jarPath"
Write-Host "Exécution: java -cp '$jarPath' $MainClass '$Scene'"

& java -cp $jarPath $MainClass $Scene
if ($LASTEXITCODE -ne 0) {
    Write-Error "Processus terminé avec code $LASTEXITCODE"
    exit $LASTEXITCODE
}

Write-Host "Terminé. L'image est écrite selon la directive 'output' dans la scène."
