[CmdletBinding()]
param(
    [string]$Profile = 'medium_phone'
)

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

& $android --no-metrics emulator create $Profile
if ($LASTEXITCODE -ne 0) {
    throw "AVD creation failed with exit code $LASTEXITCODE."
}

& $android --no-metrics emulator list --long
if ($LASTEXITCODE -ne 0) {
    throw "AVD listing failed with exit code $LASTEXITCODE."
}
