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

$sdk = Join-Path $env:LOCALAPPDATA 'Android\Sdk'
$env:ANDROID_HOME = $sdk
$env:Path = "$sdk\platform-tools;$sdk\emulator;$env:Path"

$packages = @(
    'platforms/android-36',
    'build-tools/36.0.0',
    'platforms/android-37.2',
    'build-tools/37.0.0',
    'platform-tools',
    'emulator'
)

& $android --no-metrics sdk install @packages
if ($LASTEXITCODE -ne 0) {
    throw "Android SDK installation failed with exit code $LASTEXITCODE."
}

Write-Output "Android SDK packages are installed under $sdk."
