[CmdletBinding()]
param(
    [Parameter(Position = 0)]
    [ValidateSet('devices', 'wait', 'info', 'shell', 'install', 'uninstall', 'launch', 'stop', 'clear',
        'packages', 'logcat', 'screenshot', 'push', 'pull', 'restart', 'add-to-path')]
    [string]$Action = 'devices',

    [Parameter(Position = 1, ValueFromRemainingArguments = $true)]
    [string[]]$Rest,

    # Needed only when more than one device or emulator is connected.
    [string]$Serial
)

$ErrorActionPreference = 'Stop'

$sdk = if ($env:ANDROID_HOME) { $env:ANDROID_HOME } else { Join-Path $env:LOCALAPPDATA 'Android\Sdk' }
$platformTools = Join-Path $sdk 'platform-tools'
$adb = Join-Path $platformTools 'adb.exe'

if (!(Test-Path $adb)) {
    throw "adb not found at $adb. Run scripts\setup-android-sdk.ps1 (installs platform-tools) first."
}

function Invoke-Adb {
    param([string[]]$AdbArgs)
    $prefix = if ($Serial) { @('-s', $Serial) } else { @() }
    & $adb @prefix @AdbArgs
    if ($LASTEXITCODE -ne 0) {
        throw "adb $($AdbArgs -join ' ') failed with exit code $LASTEXITCODE."
    }
}

function Get-Required {
    param([string]$Name)
    if (!$Rest -or !$Rest[0]) { throw "'$Action' requires $Name." }
    return $Rest[0]
}

switch ($Action) {
    'devices' { Invoke-Adb @('devices', '-l') }

    'wait' {
        Invoke-Adb @('wait-for-device')
        do {
            Start-Sleep -Seconds 2
            $booted = (& $adb @(if ($Serial) { '-s', $Serial }) shell getprop sys.boot_completed) -join ''
        } until ($booted.Trim() -eq '1')
        Write-Output 'Device is booted.'
    }

    'info' {
        foreach ($prop in 'ro.product.model', 'ro.build.version.release', 'ro.build.version.sdk', 'ro.product.cpu.abi') {
            $value = (& $adb @(if ($Serial) { '-s', $Serial }) shell getprop $prop) -join ''
            '{0,-28} {1}' -f $prop, $value.Trim()
        }
        Invoke-Adb @('shell', 'wm', 'size')
        Invoke-Adb @('shell', 'wm', 'density')
    }

    'shell' { Invoke-Adb (@('shell') + $Rest) }

    'install' {
        $apk = Get-Required 'an APK path'
        Invoke-Adb @('install', '-r', (Resolve-Path $apk).Path)
    }

    'uninstall' { Invoke-Adb @('uninstall', (Get-Required 'a package name')) }

    # monkey launches the default launcher Activity, so the Activity name is not needed.
    'launch' {
        $package = Get-Required 'a package name'
        $ErrorActionPreference = 'Continue'
        Invoke-Adb @('shell', 'monkey', '-p', $package, '-c', 'android.intent.category.LAUNCHER', '1') 2>&1 | Out-Null
        $ErrorActionPreference = 'Stop'
        Write-Output "Launched $package."
    }

    'stop' { Invoke-Adb @('shell', 'am', 'force-stop', (Get-Required 'a package name')) }

    'clear' { Invoke-Adb @('shell', 'pm', 'clear', (Get-Required 'a package name')) }

    'packages' {
        $filter = if ($Rest -and $Rest[0]) { $Rest[0] } else { '' }
        Invoke-Adb @('shell', 'pm', 'list', 'packages', '-3', $filter) | Where-Object { $_ }
    }

    # Defaults to the last 200 lines at warning level or higher; pass your own logcat arguments to override.
    'logcat' {
        if ($Rest) { Invoke-Adb (@('logcat') + $Rest) }
        else { Invoke-Adb @('logcat', '-d', '-t', '200', '*:W') }
    }

    # Pull through the device's storage; piping screencap output corrupts PNG bytes in Windows PowerShell.
    'screenshot' {
        $out = if ($Rest -and $Rest[0]) { $Rest[0] } else { "screenshot-$(Get-Date -Format 'yyyyMMdd-HHmmss').png" }
        $remote = '/sdcard/adb-tools-screenshot.png'
        Invoke-Adb @('shell', 'screencap', '-p', $remote)
        Invoke-Adb @('pull', $remote, $out)
        Invoke-Adb @('shell', 'rm', $remote)
        Write-Output "Saved $((Resolve-Path $out).Path)"
    }

    'push' { Invoke-Adb (@('push') + $Rest) }

    'pull' { Invoke-Adb (@('pull') + $Rest) }

    # The adb daemon inherits redirected handles, so start it through cmd with real nul handles.
    'restart' {
        & $adb kill-server
        & $env:ComSpec /c "`"$adb`" start-server >nul 2>&1"
        Invoke-Adb @('devices', '-l')
    }

    'add-to-path' {
        $userPath = [Environment]::GetEnvironmentVariable('Path', 'User')
        if (($userPath -split ';') -contains $platformTools) {
            Write-Output "$platformTools is already on your user PATH."
        } else {
            [Environment]::SetEnvironmentVariable('Path', "$userPath;$platformTools", 'User')
            Write-Output "Added $platformTools to your user PATH. Open a new terminal to use 'adb' directly."
        }
    }
}
