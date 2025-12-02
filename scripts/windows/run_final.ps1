<#
run_final.ps1
Usage:
  .\run_final.ps1            # lance src/main/resources/scenes/final.scene
  .\run_final.ps1 -Scene foo.scene
#>
[CmdletBinding()]
param(
    [string]$Scene = "$PSScriptRoot\src\main\resources\scenes\final.scene"
)

$ErrorActionPreference = 'Stop'
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$EnvFile = Join-Path $ScriptDir '.env'
$JalonEnv = Join-Path $ScriptDir 'jalon.env'
if (Test-Path $EnvFile) { . $EnvFile }
if (Test-Path $JalonEnv) { . $JalonEnv }

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
