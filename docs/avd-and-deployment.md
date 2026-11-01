# AVD setup and app deployment

An AVD combines a hardware profile (for example, a phone screen and memory settings) with a system image (the Android version and device architecture). Use an x86_64 Google APIs image on a typical Intel/AMD Windows PC. Choose a Google Play image only when you need Play Store behavior.

## Option A: Android Studio Device Manager

1. Open Android Studio.
2. From the Welcome screen, select **More Actions > Virtual Device Manager**. In an open project, use **View > Tool Windows > Device Manager**.
3. Select **+ > Create Virtual Device**.
4. Choose a phone hardware profile, then select an Android system image. Download an image if prompted; for this workspace, use API 36 with **Google APIs** and **x86_64**.
5. Finish the wizard and select the start icon beside the new AVD.

The [official AVD guide](https://developer.android.com/studio/run/managing-avds) covers editing profiles, snapshots, storage, and other device properties.

## Option B: Android CLI

Install the SDK platform, build tools, emulator, and platform tools:

```powershell
.\scripts\setup-android-sdk.ps1
```

Create a phone AVD once:

```powershell
.\scripts\create-avd.ps1
```

Or use Android CLI directly:

```powershell
android --no-metrics emulator create --list-profiles
android --no-metrics emulator create medium_phone
android --no-metrics emulator list --long
android --no-metrics emulator start medium_phone
```

`android emulator start` waits until the emulator is ready. To cold boot an existing device:

```powershell
android --no-metrics emulator start --cold medium_phone
```

`medium_phone` is a device profile ID; run `android --no-metrics emulator create --list-profiles` for the others. The CLI chooses the system image itself (here it picked `google_apis_playstore`, API 36, x86_64); see [SDK packages, AVD profiles, and switching versions](sdk-and-avd-options.md) for image variants and exact-image options. For an exact image choice, use Device Manager; API 36 Google APIs x86_64 is a suitable choice for this sample on Intel/AMD Windows.

If you prefer the Android Studio Device Manager, do not create the same AVD a second time with the CLI.

## Stop, restart, and delete an AVD

Stopping an emulator does not delete the AVD; its disk image stays under `%USERPROFILE%\.android\avd\<name>.avd`.

```powershell
android --no-metrics emulator list --long      # shows each AVD's Status and Serial
android --no-metrics emulator stop medium_phone
```

`stop` accepts the AVD name or its serial (for example `emulator-5554`), and the argument is optional when only one emulator is running. The emulator window can also simply be closed, or stopped from Android Studio's Device Manager (the stop icon beside the device).

Other ways to stop it, for when `stop` does not respond:

```powershell
adb -s emulator-5554 emu kill                  # asks the emulator to exit through its console
```

Last resort: end the `qemu-system-x86_64.exe` process for that emulator with `Stop-Process -Id <PID>` (find the ID with `Get-Process qemu-system*`). Do this only if the emulator is frozen, because it is an unclean shutdown.

Notes:

- Run `android --no-metrics emulator list --long` afterwards to confirm the Status is no longer `Online`, and `adb devices` to confirm `emulator-5554` is gone.
- Closing the emulator normally can save a quick-boot snapshot, so the next `emulator start` resumes where you left off. To discard it and boot from scratch, use `android --no-metrics emulator start --cold medium_phone`.
- Stop the emulator before editing the AVD's `config.ini` or deleting it.
- Stopping it also stops running apps. The adb server is separate and keeps running; `adb kill-server` stops only that.

To delete an AVD and its data permanently:

```powershell
android --no-metrics emulator stop medium_phone
android --no-metrics emulator remove medium_phone
```

`remove --force` also deletes the AVD's `.ini` file when its `.avd` folder is already gone. Removing an AVD does not remove its system image; to free that space, use `android --no-metrics sdk remove "system-images/<api>/<variant>/<abi>"` (see [SDK packages, AVD profiles, and switching versions](sdk-and-avd-options.md)).

## Build, install, and launch the sample

With an AVD running, from the workspace root run:

```powershell
.\scripts\run-sample.ps1
```

The script calls `android run :app` from the sample project. Android CLI builds the debug variant, installs it, and launches its main Activity.

To run the steps separately:

```powershell
$env:ANDROID_HOME = Join-Path $env:LOCALAPPDATA 'Android\Sdk'
.\sample-app\gradlew.bat -p .\sample-app :app:assembleDebug
& "$env:ANDROID_HOME\platform-tools\adb.exe" devices -l
& "$env:ANDROID_HOME\platform-tools\adb.exe" install -r .\sample-app\app\build\outputs\apk\debug\app-debug.apk
& "$env:ANDROID_HOME\platform-tools\adb.exe" shell am start -n 'com.example.avdstarter/.MainActivity'
```

The generated APK is `sample-app\app\build\outputs\apk\debug\app-debug.apk`. The sample screen displays the device model and Android API level; tap its button to increment the counter.

## Kotlin and Jetpack Compose sample

The second project uses Kotlin, Jetpack Compose, Material 3, Navigation 3, and Android API 37. With an emulator running, build, install, and launch it from the workspace root:

```powershell
.\scripts\run-sample-2.ps1
```

Its APK is written to `sample-app-2\app\build\outputs\apk\debug\app-debug.apk`. For the version pins, architecture choices, and API 37 preview caveat, see [Modern Android development recommendations](android-modern-recommendations.md).

If multiple devices are connected, select the emulator serial shown by `adb devices -l`:

```powershell
android --no-metrics run --device=emulator-5554 :app
```

To install an already-built APK without launching the app, use `android install --apks=...`; use `android run --apks=...` to install and launch it.
