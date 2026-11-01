# Troubleshooting

## Emulator reports that acceleration is unavailable

Check the emulator's acceleration status:

```powershell
$env:ANDROID_HOME = Join-Path $env:LOCALAPPDATA 'Android\Sdk'
& "$env:ANDROID_HOME\emulator\emulator.exe" -accel-check
```

Confirm Intel VT-x or AMD-V/SVM is enabled in UEFI/BIOS. Enable **Windows Hypervisor Platform** in Windows Features and restart. Windows Hypervisor Platform is Google's recommended Windows backend; do not disable Hyper-V, WSL 2, or security features just to switch emulator drivers. See [Windows virtualization setup](windows-setup.md).

## AVD is slow or does not boot

- Use an x86_64 system image on an Intel/AMD Windows PC.
- Close other heavy applications and check free disk space; AVD snapshots and user data consume disk space.
- Cold boot the AVD: `android --no-metrics emulator start --cold medium_phone`.
- If it still fails, stop it and use **Wipe Data** for that AVD in Android Studio Device Manager. This removes that AVD's local data.
- Check the emulator and AVD list with `android --no-metrics emulator list --long`.

## `adb` is missing or the device is offline

Set the SDK path and add platform tools to the current PowerShell session:

```powershell
$env:ANDROID_HOME = Join-Path $env:LOCALAPPDATA 'Android\Sdk'
$env:Path = "$env:ANDROID_HOME\platform-tools;$env:Path"
& "$env:ANDROID_HOME\platform-tools\adb.exe" devices -l
```

For a stale ADB server:

```powershell
& "$env:ANDROID_HOME\platform-tools\adb.exe" kill-server
& "$env:ANDROID_HOME\platform-tools\adb.exe" start-server
& "$env:ANDROID_HOME\platform-tools\adb.exe" devices -l
```

The device status should be `device`, not `offline` or `unauthorized`. See [Using adb](adb.md) for the full guide and the `scripts\adb-tools.ps1 restart` shortcut.

## Gradle cannot find the Android SDK

Check `android --no-metrics info` and `ANDROID_HOME`. The expected default is `%LOCALAPPDATA%\Android\Sdk`. Android Studio writes a machine-specific `local.properties`; do not commit that file. For a terminal-only build:

```powershell
$env:ANDROID_HOME = Join-Path $env:LOCALAPPDATA 'Android\Sdk'
.\sample-app\gradlew.bat -p .\sample-app :app:assembleDebug
```

## SDK package is not found

Use the package paths shown by Android CLI rather than guessing an API suffix:

```powershell
android --no-metrics sdk list --all
android --no-metrics sdk list system-images/android-36/google_apis/x86_64
```

Install SDK packages through Android CLI or Android Studio SDK Manager. The legacy `sdkmanager` remains available in the Command-Line Tools package, but Google's current docs recommend Android CLI for new scripted workflows.
