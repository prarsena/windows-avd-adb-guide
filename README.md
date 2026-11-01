# Android Virtual Devices on Windows

A practical Windows 10/11 x64 guide to Android Virtual Devices (AVDs), emulator hardware acceleration, and deploying a small Android app. The sample app is a Java Activity with no third-party app dependencies.

## Quick start

1. Install a JDK 17 or newer and the official [Android CLI](https://developer.android.com/tools/agents/android-cli/download). On Windows, use:

   ```powershell
   winget install --id Google.AndroidCLI
   ```

   Open a new terminal after installation, then check `android --version`.

2. Install the Android platform and build tools used by the sample:

   ```powershell
   .\scripts\setup-android-sdk.ps1
   ```

3. Create and boot a phone AVD:

   ```powershell
   .\scripts\create-avd.ps1
   android --no-metrics emulator start medium_phone
   ```

   `medium_phone` is a device profile ID (also used as the AVD name); other profiles include `small_phone`, `medium_tablet`, and desktop sizes. Pass one with `-Profile`. If `medium_phone` already exists, skip creation and start it. The Android Studio alternative is in [AVD setup and deployment](docs/avd-and-deployment.md).

4. Build, install, and launch the sample:

   ```powershell
   .\scripts\run-sample.ps1
   ```

SDK and system-image downloads can be large. Creating an AVD may download its profile's system image; review any Android SDK license terms presented before accepting.

## Documentation

- [Windows setup and virtualization](docs/windows-setup.md) — JDK, Android CLI, SDK packages, WHPX, and Android Studio.
- [AVD setup and deployment](docs/avd-and-deployment.md) — create devices in Device Manager or Android CLI; build and deploy the sample.
- [SDK packages, AVD profiles, and switching versions](docs/sdk-and-avd-options.md) — what `medium_phone` is, system image variants, and installing/pinning SDK packages from the CLI.
- [Using adb](docs/adb.md) — how adb relates to AVDs, running it without Android Studio, and the `scripts\adb-tools.ps1` helper.
- [Android CLI and agents](docs/android-cli-agents.md) — agent workflows, `android init`, and metrics.
- [Modern Android development recommendations](docs/android-modern-recommendations.md) — current Kotlin, Compose, Material, architecture, and API guidance.
- [Troubleshooting](docs/troubleshooting.md) — emulator acceleration, ADB, and SDK path issues.

## Sample app

The app source is [MainActivity.java](sample-app/app/src/main/java/com/example/avdstarter/MainActivity.java). It displays the emulator model and Android API level, and its button increments a tap counter. Its debug APK is written to:

```text
sample-app\app\build\outputs\apk\debug\app-debug.apk
```

The project uses Android Gradle Plugin 9.4.0, Gradle 9.6.0, and Android API 36. Review the [official AVD guide](https://developer.android.com/studio/run/managing-avds), [Windows emulator acceleration guide](https://developer.android.com/studio/run/emulator-acceleration), and [Android CLI / agent guide](https://developer.android.com/tools/agents) for current platform details.

## Modern Kotlin sample

[sample-app-2](sample-app-2) is the Kotlin-first alternative: Jetpack Compose, Material 3, Navigation 3, adaptive navigation, dynamic color, and lifecycle-aware `StateFlow` UI state. It compiles and targets Android API 37.

With an AVD running, build, install, and launch it with:

```powershell
.\scripts\run-sample-2.ps1
```

The sample's Android 17 / API 37 target is intended to explore the latest SDK. See [the recommendations guide](docs/android-modern-recommendations.md) for SDK preview notes, current dependency versions, and production guidance. It uses Compose for screens; the Android manifest and localized string resources remain XML, as is normal in a Compose app.
