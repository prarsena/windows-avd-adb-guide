# Using adb with the emulator

## How adb fits with AVDs

- An **AVD** is a virtual device definition; the **emulator** process runs it.
- **adb** (Android Debug Bridge) is the command-line tool that talks to any running Android device, virtual or physical. It installs apps, opens a shell, copies files, and reads logs.
- adb has three parts: a **client** (the `adb` command you type), a **server** (a background process on your PC, TCP port 5037, started automatically on first use), and a **daemon** (`adbd`) running inside the device.
- Every emulator registers itself with the adb server as `emulator-<port>`, for example `emulator-5554`. The first emulator uses console port 5554, the next 5556, and so on.

**You do not need Android Studio open.** Android Studio, Android CLI (`android run`), and Gradle all use this same adb. `adb.exe` ships in the SDK's `platform-tools` package, which [setup-android-sdk.ps1](../scripts/setup-android-sdk.ps1) installs at:

```text
%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe
```

Because it is a standalone executable, you can start an emulator with `android emulator start medium_phone` and then use adb from any terminal. `adb` must be on your PATH or called by full path.

If an adb server is already running (from Android Studio, for example), the `adb` command reuses it. Use one `platform-tools` copy, since mixed adb versions kill and restart each other's servers, which causes "adb server version doesn't match this client" errors. If you see that, run `adb kill-server`, then `adb devices`.

## Setup

Check what is installed and put adb on your PATH (one time, user scope, no admin):

```powershell
.\scripts\adb-tools.ps1 add-to-path     # then open a new terminal
adb version
adb devices -l
```

To install platform-tools if it is missing, or update it:

```powershell
android --no-metrics sdk install platform-tools
android --no-metrics sdk update platform-tools
```

You can also use the standalone [SDK Platform-Tools download](https://developer.android.com/tools/releases/platform-tools), but a second copy is not recommended if you already use the SDK one.

## The helper script

[scripts/adb-tools.ps1](../scripts/adb-tools.ps1) wraps the common commands, finds `adb.exe` without PATH, and fails with a clear message on errors.

```powershell
.\scripts\adb-tools.ps1 <action> [arguments] [-Serial emulator-5554]
```

| Action | What it does | Example |
|---|---|---|
| `devices` (default) | List connected devices | `.\scripts\adb-tools.ps1` |
| `wait` | Block until the device finishes booting | `.\scripts\adb-tools.ps1 wait` |
| `info` | Model, Android version, API level, ABI, screen size and density | `.\scripts\adb-tools.ps1 info` |
| `packages [filter]` | List user-installed apps (optional substring filter) | `.\scripts\adb-tools.ps1 packages avd` |
| `install <apk>` | Install or replace an APK (`-r`) | `.\scripts\adb-tools.ps1 install .\sample-app\app\build\outputs\apk\debug\app-debug.apk` |
| `launch <package>` | Start the app's launcher Activity | `.\scripts\adb-tools.ps1 launch com.example.avdstarter` |
| `stop <package>` | Force-stop an app | `.\scripts\adb-tools.ps1 stop com.example.avdstarter` |
| `clear <package>` | Wipe the app's data and cache | `.\scripts\adb-tools.ps1 clear com.example.avdstarter` |
| `uninstall <package>` | Remove an app | `.\scripts\adb-tools.ps1 uninstall com.example.avdstarter` |
| `logcat [args]` | Last 200 lines at warning and above; pass your own logcat arguments to override | `.\scripts\adb-tools.ps1 logcat -d -t 100 --pid=1234` |
| `screenshot [file]` | Save a PNG screenshot | `.\scripts\adb-tools.ps1 screenshot home.png` |
| `push` / `pull` | Copy files to or from the device | `.\scripts\adb-tools.ps1 pull /sdcard/Download/a.txt .` |
| `shell [command]` | Run a command (or open an interactive shell with none) | `.\scripts\adb-tools.ps1 shell getprop ro.product.model` |
| `restart` | Kill and restart the adb server | `.\scripts\adb-tools.ps1 restart` |
| `add-to-path` | Add platform-tools to your user PATH | |

The sample apps' package names are `com.example.avdstarter` ([sample-app](../sample-app)) and `com.example.avdcomposeshowcase` ([sample-app-2](../sample-app-2)).

## Core concepts and commands

The examples use `adb` directly; the script's actions map to these.

### Devices and serials

```powershell
adb devices -l
```

| State | Meaning |
|---|---|
| `device` | Connected and ready |
| `offline` | Not responding; restart the emulator or the adb server |
| `unauthorized` | Physical device waiting for you to accept its USB debugging prompt |
| `authorizing` | Briefly seen after an adb server restart; run `adb devices` again |

With more than one device or emulator attached, adb refuses ambiguous commands ("more than one device/emulator"). Pick one:

```powershell
adb -s emulator-5554 shell                # per command
$env:ANDROID_SERIAL = 'emulator-5554'     # for the rest of this terminal
adb -e shell                              # the only emulator
adb -d shell                              # the only physical USB device
```

`adb wait-for-device` returns when the device is connected, not fully booted; the helper's `wait` also waits for `sys.boot_completed`.

### Install and run apps

```powershell
adb install -r app-debug.apk              # -r replace, keep data
adb install -r -d app-debug.apk           # -d allow version downgrade
adb install -g app-debug.apk              # grant all runtime permissions
adb install-multiple base.apk split.apk   # split APKs
adb uninstall com.example.avdstarter
adb uninstall -k com.example.avdstarter   # keep data and cache

adb shell am start -n com.example.avdstarter/.MainActivity
adb shell monkey -p com.example.avdstarter -c android.intent.category.LAUNCHER 1
adb shell am force-stop com.example.avdstarter
adb shell pm clear com.example.avdstarter
adb shell pm path com.example.avdstarter  # where the APK is on the device
```

`am start -n package/.Activity` needs the Activity name; `monkey` needs only the package.

Open a URL or deep link:

```powershell
adb shell am start -a android.intent.action.VIEW -d "https://developer.android.com"
```

### Permissions, settings, and system state

```powershell
adb shell pm grant com.example.app android.permission.CAMERA
adb shell pm revoke com.example.app android.permission.CAMERA
adb shell settings put global window_animation_scale 0     # also transition_animation_scale and animator_duration_scale; useful for UI tests
adb shell cmd uimode night yes                              # dark mode (use 'no' to turn off)
adb shell wm size 1080x1920                                 # override resolution (reset with: wm size reset)
adb shell wm density 320                                    # override density (reset with: wm density reset)
adb shell input text hello
adb shell input tap 540 1200
adb shell input swipe 540 1800 540 600 300
adb shell input keyevent KEYCODE_HOME                       # also KEYCODE_BACK
```

### Logs: logcat

```powershell
adb logcat                                  # live stream; Ctrl+C to stop
adb logcat -d -t 200                        # dump the last 200 lines and exit
adb logcat -c                               # clear the log buffer
adb logcat *:W                              # warning and above
adb logcat -s MyTag:D                       # only one tag
adb logcat --pid=$(adb shell pidof -s com.example.avdstarter)
adb logcat -d > logcat.txt                  # save to a file
```

Priority letters are V (verbose), D, I, W, E, F. Use `adb bugreport report.zip` for a full diagnostic snapshot.

### Files

```powershell
adb push local.txt /sdcard/Download/
adb pull /sdcard/Download/local.txt .
adb shell ls /sdcard/Download
```

An app's private storage (`/data/data/<package>`) is readable only for debuggable apps via `adb shell run-as <package>`, for example `adb shell run-as com.example.avdstarter ls files`.

### Screenshots and screen recording

```powershell
adb shell screencap -p /sdcard/s.png ; adb pull /sdcard/s.png ; adb shell rm /sdcard/s.png
adb shell screenrecord --time-limit 30 /sdcard/r.mp4 ; adb pull /sdcard/r.mp4
```

Avoid `adb exec-out screencap -p > file.png` in Windows PowerShell 5.1, because the redirection re-encodes bytes and corrupts the image. (PowerShell 7 handles it correctly.) `screenrecord` records up to 180 seconds per file.

### Inspect the device

```powershell
adb shell getprop ro.build.version.release  # Android version
adb shell getprop ro.build.version.sdk      # API level
adb shell pm list packages -3               # user-installed apps (add -s for system)
adb shell dumpsys activity activities       # includes topResumedActivity (current app)
adb shell dumpsys battery
adb shell dumpsys meminfo com.example.avdstarter
adb shell dumpsys package com.example.avdstarter
adb shell top -n 1
```

### Port forwarding

```powershell
adb reverse tcp:8080 tcp:8080     # device localhost:8080 reaches your PC's port 8080
adb forward tcp:9000 tcp:9000     # your PC's localhost:9000 reaches the device
adb forward --list
adb reverse --remove-all
```

Inside an emulator, `10.0.2.2` is also the address of your PC's loopback, so a local dev server on port 8080 is `http://10.0.2.2:8080`. `adb reverse` lets the app use `localhost` instead.

### Emulator console through adb

```powershell
adb emu avd name                  # AVD name
adb emu kill                      # shut down the emulator
adb -s emulator-5554 emu geo fix -122.084 37.422   # set GPS (longitude then latitude)
```

Prefer `android emulator stop <name>` for a clean stop; see [Stop, restart, and delete an AVD](avd-and-deployment.md#stop-restart-and-delete-an-avd).

## adb and physical devices

Enable **Developer options** and **USB debugging** on the phone, connect it by USB, and accept the RSA prompt. If it shows `unauthorized`, revoke USB debugging authorizations on the phone and reconnect. For wireless debugging (Android 11+), on the device open **Developer options > Wireless debugging**, then:

```powershell
adb pair <ip>:<pairing-port>      # enter the pairing code shown on the device
adb connect <ip>:<port>
adb disconnect
```

Use `adb -d` or `-s <serial>` when an emulator and a physical device are both connected.

## Root and the system image

Only images **without** Play Store (`default`, `google_apis`) allow `adb root` and `adb remount`. A `google_apis_playstore` image, which is what `create-avd.ps1` produced here (see [SDK packages, AVD profiles, and switching versions](sdk-and-avd-options.md)), is production-like and rejects `adb root`. `adb shell` still works as the unprivileged `shell` user, and `run-as` works for debuggable apps.

## Troubleshooting

| Symptom | Fix |
|---|---|
| `adb` is not recognized | Run `.\scripts\adb-tools.ps1 add-to-path` and open a new terminal, or call `%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe` |
| No devices listed | Confirm the emulator is up with `android --no-metrics emulator list --long`; then `.\scripts\adb-tools.ps1 restart` |
| `offline` | `adb kill-server`, `adb start-server`; if it persists, `android --no-metrics emulator start --cold <name>` |
| `more than one device/emulator` | Use `-s <serial>`, `-e`, `-d`, or `$env:ANDROID_SERIAL` |
| `INSTALL_FAILED_UPDATE_INCOMPATIBLE` | Signature changed; `adb uninstall <package>` and install again |
| `INSTALL_FAILED_VERSION_DOWNGRADE` | `adb install -r -d <apk>` |
| `INSTALL_FAILED_INSUFFICIENT_STORAGE` | Free space or wipe the AVD data (Device Manager or a cold boot) |
| `adb server version doesn't match this client` | Two different adb copies: keep one on PATH, then `adb kill-server` |
| Port 5037 in use | Another program holds it: `netstat -ano \| findstr :5037`, or set `ANDROID_ADB_SERVER_PORT` for both client and server |

More general ADB and emulator fixes are in [Troubleshooting](troubleshooting.md). The full command reference is the official [adb guide](https://developer.android.com/tools/adb).
