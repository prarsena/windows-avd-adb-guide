$ErrorActionPreference = 'Stop'

$androidCommand = Get-Command android -ErrorAction SilentlyContinue
if ($androidCommand) {
    $android = $androidCommand.Source
} else {
    $android = Join-Path $env:USERPROFILE 'AppData\AndroidCLI\android.exe'
}

if (!(Test-Path $android)) {
    throw 'Android CLI is not installed. Follow docs\windows-setup.md first.'
}

$env:ANDROID_HOME = Join-Path $env:LOCALAPPDATA 'Android\Sdk'
$env:Path = "$env:ANDROID_HOME\platform-tools;$env:ANDROID_HOME\emulator;$env:Path"

$project = Join-Path $PSScriptRoot '..\sample-app-2'
Push-Location $project
try {
    & $android --no-metrics run :app
    if ($LASTEXITCODE -ne 0) {
        throw "Sample app build or deployment failed with exit code $LASTEXITCODE."
    }
} finally {
    Pop-Location
}
