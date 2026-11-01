# Windows setup and Android virtualization

## What is being virtualized?

An Android Virtual Device (AVD) is a saved device profile plus an Android system image. The Android Emulator runs that AVD as a virtual device. On Windows, the emulator uses CPU virtualization and a hypervisor to make x86_64 images run at usable speed; this is separate from creating the AVD itself.

For Intel PCs, enable Intel VT-x in UEFI/BIOS. For AMD PCs, enable AMD-V/SVM. Google recommends the Microsoft Windows Hypervisor Platform (WHPX) for emulator VM acceleration on Windows.

## Enable or verify Windows Hypervisor Platform

1. Open **Start** and search for **Turn Windows features on or off**.
2. Enable **Windows Hypervisor Platform** and select **OK**.
3. Restart Windows if prompted.
4. Keep Hyper-V, Virtual Machine Platform, WSL 2, and Windows security virtualization features enabled if you use them. Prefer WHPX rather than disabling Hyper-V or security features to use a different emulator driver.

To see whether Windows reports an active hypervisor:

```powershell
systeminfo.exe | Select-String 'Hyper-V Requirements|A hypervisor has been detected'
```

After installing the Android Emulator, check its own acceleration status:

```powershell
$env:ANDROID_HOME = Join-Path $env:LOCALAPPDATA 'Android\Sdk'
& "$env:ANDROID_HOME\emulator\emulator.exe" -accel-check
```

The processor must support hardware virtualization and it must be enabled in firmware. If the check fails, see Google's [Windows emulator acceleration instructions](https://developer.android.com/studio/run/emulator-acceleration#vm-windows-whpx).

## Install Android tools

Install the official Android CLI for the current Windows user with WinGet:

```powershell
winget install --id Google.AndroidCLI
```

Alternatively, run the local installer from Command Prompt:

```cmd
curl.exe -fsSL https://dl.google.com/android/cli/latest/windows_x86_64/install.cmd -o "%TEMP%\android-cli-install.cmd"
"%TEMP%\android-cli-install.cmd"
```

The local installer places `android.exe` under `%USERPROFILE%\AppData\AndroidCLI` and adds that directory to the user `PATH`. Open a new terminal after installing. Verify the CLI and its Android SDK location:

```powershell
android --no-metrics --version
android --no-metrics info
java -version
```

The Android CLI's default SDK location on Windows is `%LOCALAPPDATA%\Android\Sdk`. This workspace's setup script installs the Android platform, build tools, platform tools, and emulator:

```powershell
.\scripts\setup-android-sdk.ps1
```

The script sets `ANDROID_HOME` and adds the SDK's `platform-tools` and `emulator` directories to the current PowerShell process. AVD creation downloads its selected system image when needed. To set the SDK location manually in a terminal:

```powershell
$env:ANDROID_HOME = Join-Path $env:LOCALAPPDATA 'Android\Sdk'
$env:Path = "$env:ANDROID_HOME\platform-tools;$env:ANDROID_HOME\emulator;$env:Path"
```

Android Studio includes its own Java runtime. For command-line builds, this sample uses Android Gradle Plugin 9.4.0 and Gradle 9.6.0, which require JDK 17 or newer. Check the [Android Gradle Plugin compatibility notes](https://developer.android.com/build/releases/gradle-plugin) if you change the plugin or Gradle wrapper.

## Optional: Android Studio

Install [Android Studio](https://developer.android.com/studio) if you want the IDE, SDK Manager, and graphical Device Manager. The SDK Manager can install the same SDK packages as the CLI. You can use the Android CLI alongside Android Studio; you do not need to use the deprecated `sdkmanager` for this guide.
