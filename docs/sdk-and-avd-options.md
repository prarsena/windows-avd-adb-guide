# SDK packages, AVD profiles, and switching versions

This guide explains what `medium_phone` means in [create-avd.ps1](../scripts/create-avd.ps1), what other choices exist, and how to download or switch Android SDK versions from the command line. Output below was checked against Android CLI on this workspace; run the listed `--help` and `list` commands yourself, because package availability changes.

## What `medium_phone` is

`medium_phone` is a **device profile ID**, not just a label. Android CLI uses it as both:

- the *profile* that decides the hardware shape (screen size/density, RAM, form factor), and
- the *AVD name/ID* of the device it creates (`android emulator list` shows `medium_phone` / "Medium Phone").

[create-avd.ps1](../scripts/create-avd.ps1) takes it as `-Profile` (default `medium_phone`) and runs `android emulator create <profile>`.

List the profiles your CLI supports:

```powershell
android --no-metrics emulator create --list-profiles
```

| Profile | Form factor |
|---|---|
| `small_phone` | Phone, small screen |
| `medium_phone` | Phone, default (1080x2400, 420 dpi, 2048 MB RAM here) |
| `medium_tablet` | Tablet |
| `small_desktop`, `medium_desktop`, `large_desktop` | Desktop / freeform-window devices |

```powershell
.\scripts\create-avd.ps1 -Profile medium_tablet
android --no-metrics emulator start medium_tablet
```

Limits of `android emulator create`: its only input is the profile. It has no flags for API level, image type, or ABI. The CLI picks the system image itself. On this machine, `medium_phone` resolved to:

```text
image.sysdir.1=system-images/android-36/google_apis_playstore/x86_64/
tag.id=google_apis_playstore
PlayStore.enabled=true
```

That is a **Google Play** image, not the plain Google APIs image that [avd-and-deployment.md](avd-and-deployment.md) recommends. Check what you got in `%USERPROFILE%\.android\avd\<name>.avd\config.ini`. The CLI appears to use an installed matching image (this workspace installs API 36 images). If you need an exact image, use one of the methods in [Choosing an exact system image](#choosing-an-exact-system-image).

## Does the repo cover choosing the SDK programmatically?

Partly. [setup-android-sdk.ps1](../scripts/setup-android-sdk.ps1) installs a fixed, hardcoded list (`platforms/android-36`, `build-tools/36.0.0`, `platforms/android-37.2`, `build-tools/37.0.0`, `platform-tools`, `emulator`). It does not take parameters, does not install system images, and does not list what else is available. The sections below cover that gap.

## Package naming

Android CLI SDK package IDs use `/` separators (the legacy `sdkmanager` uses `;`).

| Package ID pattern | What it provides |
|---|---|
| `platforms/android-<api>` | Compile-time `android.jar` and framework resources for that API (`compileSdk`). Required to build. |
| `build-tools/<version>` | `aapt2`, `d8`, `apksigner`, `zipalign`. Required by Gradle to package APKs. |
| `platform-tools` | `adb`, `fastboot`. Install/debug/shell on devices. |
| `emulator` | The Android Emulator binary itself. |
| `system-images/android-<api>/<variant>/<abi>` | The OS an AVD boots. See variants below. |
| `sources/android-<api>` | Framework Java sources so the IDE can show real code when you step into Android APIs. |
| `cmdline-tools/<ver>` | `sdkmanager`, `avdmanager`, `apkanalyzer`, `lint`. Not needed with Android CLI, but required for scripts and CI that expect them. |
| `ndk/<version>` | Native toolchain for C/C++ code. Install only for NDK projects; pin the version in Gradle (`ndkVersion`). |
| `cmake/<version>` | CMake for native builds (Gradle `externalNativeBuild`). Pairs with the NDK. |
| `extras/android/m2repository` | Legacy support-library Maven repo; obsolete for AndroidX projects. |
| `add-ons/addon-google_apis-google-<api>` | Legacy Google APIs add-ons for API 10-24 era; not used on modern API levels (Google APIs now ship inside system images). |
| `build/templates`, `build/lightbuild/*` | Android CLI project templates and its light-build tooling (used by `android create`). |
| `skiaparser/<n>` | Parser used by Android Studio layout/vector tooling; installed on demand. |

Counts reported by `android sdk list --all` here: about 345 system images, 85 build-tools, 60 platforms, 46 NDKs, 28 source packages, 24 cmdline-tools, 24 add-ons, 17 CMake versions.

## System image variants

The middle part of a system image ID is the **variant** (tag). It decides what is on the device:

| Variant | What you get | Use it for |
|---|---|---|
| `default` | Plain AOSP, no Google apps/services | Fastest/smallest; apps that need no Google services |
| `google_apis` | AOSP plus Google Play services APIs (Maps, Firebase, etc.), no Play Store app, root `adb root` allowed | Most development/testing; best default |
| `google_apis_playstore` | Above plus the Play Store app; production-like, no `adb root` | Testing Play sign-in, in-app purchases, Play updates |
| `google_apis_ps16k`, `google_apis_playstore_ps16k` | Same, with a **16 KB memory page size** kernel | Verifying native code is 16 KB-page compatible (needed for current Play requirements) |
| `google_apis_tablet`, `google_apis_playstore_tablet` | Tablet-tuned images | Large-screen testing |
| `google_atd`, `aosp_atd` | **Automated Test Device**: trimmed images with background services and some UI removed | Faster, lighter headless CI / instrumented test runs |
| `android-wear`, `android-wear-signed` | Wear OS | Smartwatch apps |
| `android-tv`, `google-tv`, `google-tv-ps16k` | Android TV / Google TV | TV apps |
| `android-automotive`, `android-automotive-playstore` | Android Automotive OS | Car apps |
| `ai-glasses` | Android XR / AI glasses image (API 36 here) | Glasses form factor experiments |

ABI is the last segment: use `x86_64` on Intel/AMD Windows (hardware acceleration via WHPX, see [windows-setup.md](windows-setup.md)). `arm64-v8a` images also appear in the list but run far slower on an x86 host. Use them only to check arm-specific behavior.

API segments include extension and preview releases such as `android-36.1`, `android-36-ext18`, `android-37.0`, `android-37.2-beta3`. Stable IDs like `android-36` or `android-37.0` are normal releases; `-betaN` entries are previews and may need `--beta`.

## List, search, and inspect packages

```powershell
android --no-metrics sdk list                          # installed + available (default view)
android --no-metrics sdk list --all                    # everything in the repository (large output)
android --no-metrics sdk list "system-images/android-36/*"   # glob filter with *
android --no-metrics sdk list "ndk/*"
android --no-metrics sdk list --all-versions platform-tools   # every version of a package
android --no-metrics sdk list --beta                   # include beta packages
android --no-metrics sdk list --canary                 # include canary packages
```

In PowerShell, filter with `Select-String` or `-match`, for example:

```powershell
android --no-metrics sdk list --all "system-images/*" | Select-String 'android-37'
```

## Install, update, remove, and pin versions

```powershell
android --no-metrics sdk install platforms/android-35 build-tools/35.0.0
android --no-metrics sdk install "system-images/android-36/google_apis/x86_64"
android --no-metrics sdk install platform-tools@36.0.0   # pin an exact version
android --no-metrics sdk install ndk/28.2.13676358 cmake/4.1.2
android --no-metrics sdk update                         # update everything installed
android --no-metrics sdk update emulator                # update one package
android --no-metrics sdk remove "system-images/android-35/google_apis/x86_64"
```

Notes:

- `<package>@<version>` defaults to `latest`.
- `--no-downgrade` skips packages that would be downgraded; `--force` allows a downgrade.
- `--beta` and `--canary` are required to see/install preview channels (for example `android-37.2-beta3`).
- `--sdk=<path>` targets a specific SDK folder. This workspace uses `%LOCALAPPDATA%\Android\Sdk` and sets `ANDROID_HOME` to it.
- `--platform=<os>_<arch>` (for example `windows_x86`) fetches packages for another host platform. Normally leave it unset.
- Packages whose licenses you have not accepted prompt for acceptance. Review them first.

### Make setup-android-sdk.ps1 configurable

The script's `$packages` array can be turned into parameters, for example:

```powershell
param(
    [string[]]$Packages = @(
        'platforms/android-36', 'build-tools/36.0.0',
        'platform-tools', 'emulator',
        'system-images/android-36/google_apis/x86_64'
    )
)
```

then `& $android --no-metrics sdk install @Packages`, so you can call `.\scripts\setup-android-sdk.ps1 -Packages platforms/android-35,build-tools/35.0.0`.

## Switching the Android version your app builds against

There are two separate version choices:

1. **SDK installed on disk** (`android sdk install ...`): makes an API level *available*.
2. **What the project uses**: set in the app's Gradle file. This workspace's samples set `compileSdk`/`targetSdk` in [sample-app/app/build.gradle.kts](../sample-app/app/build.gradle.kts) and [sample-app-2/app/build.gradle.kts](../sample-app-2/app/build.gradle.kts).

Installing `platforms/android-35` does not change the project; edit `compileSdk` and `targetSdk`. Keep `minSdk` as low as your supported devices allow. `compileSdk` needs the matching `platforms/` package; `targetSdk` controls runtime behavior changes; the emulator image API only needs to be at least `minSdk`.

## Choosing an exact system image

`android emulator create <profile>` cannot choose the image. Options:

1. **Install the image you want first, then create the AVD.** The CLI can only pick from what the SDK has or can download for the profile, and this workspace ended up with `google_apis_playstore`. Verify with `config.ini` after creation.
2. **Android Studio Device Manager:** pick any profile and image in the wizard. See [avd-and-deployment.md](avd-and-deployment.md).
3. **`avdmanager`** (from `cmdline-tools`; install with `android sdk install cmdline-tools/19.0`, which is not installed here). Typical form, run from `%LOCALAPPDATA%\Android\Sdk\cmdline-tools\<ver>\bin`:

   ```powershell
   .\avdmanager.bat list device
   .\avdmanager.bat create avd -n api36_gapi -k "system-images;android-36;google_apis;x86_64" -d pixel_8
   ```

   Note `avdmanager` and `sdkmanager` use `;` in package IDs, unlike Android CLI's `/`. Check `avdmanager create avd --help` for your version.
4. **Edit an existing AVD:** `%USERPROFILE%\.android\avd\<name>.avd\config.ini` holds `image.sysdir.1`, `hw.ramSize`, `hw.lcd.*`, `PlayStore.enabled`, and more. Edit it only while the emulator is stopped, and only to point at an image that is installed.

Use separate AVDs side by side to test several API levels, and manage them with:

```powershell
android --no-metrics emulator list --long
android --no-metrics emulator start <name>            # add --cold or --headless as needed
android --no-metrics emulator stop <name>
android --no-metrics emulator remove <name>
```

## What additional add-ons buy you

| Add-on | Why install it |
|---|---|
| Extra `platforms/` + `build-tools/` | Compile against a newer/older API; reproduce older-API behavior; CI that pins versions |
| `system-images/.../google_apis` | Matches real devices with Google Play services APIs (Maps, Firebase, location, FCM) |
| `system-images/.../google_apis_playstore` | Test Play sign-in, billing, and Play-delivered updates |
| `*_ps16k` images | Validate native libraries and the app under 16 KB pages |
| `*_atd` images | Much faster, lighter instrumented tests (see [sample-app-2 tests](../sample-app-2/app/src/androidTest)) in CI |
| `*_tablet`, desktop profiles | Large-screen/adaptive layout testing (relevant to the Compose sample's adaptive navigation) |
| `android-wear`, `android-tv`, `android-automotive`, `ai-glasses` | Non-phone form factors |
| `sources/android-<api>` | Read and step into Android framework source in the IDE |
| `ndk/<ver>` + `cmake/<ver>` | Build C/C++ code via Gradle; required for native libraries |
| `cmdline-tools/<ver>` | `sdkmanager`, `avdmanager`, `apkanalyzer`, `lint` for third-party tools and CI images |
| `platform-tools` | `adb` and `fastboot`; keep current to avoid ADB/emulator mismatches |
| `emulator` | New emulator features and bug fixes; update often |

Disk-space note: each system image is typically several GB when extracted, and each NDK is about 1 GB or more. Remove what you do not use with `android sdk remove`.

## Command quick reference

| Task | Command |
|---|---|
| List device profiles | `android --no-metrics emulator create --list-profiles` |
| Create AVD from profile | `android --no-metrics emulator create <profile>` |
| List AVDs | `android --no-metrics emulator list --long` |
| Start / cold boot / headless | `android --no-metrics emulator start [--cold] [--headless] <name>` |
| Stop / delete AVD | `android --no-metrics emulator stop <name>` / `remove <name>` |
| List SDK packages | `android --no-metrics sdk list [--all] [--all-versions] [--beta] [--canary] [<glob>]` |
| Install (optionally pinned) | `android --no-metrics sdk install <package>[@<version>]` |
| Update | `android --no-metrics sdk update [<package>]` |
| Remove | `android --no-metrics sdk remove <package>` |

Official references: [Android CLI](https://developer.android.com/tools/agents/android-cli/download), [Managing AVDs](https://developer.android.com/studio/run/managing-avds), [sdkmanager](https://developer.android.com/tools/sdkmanager), [avdmanager](https://developer.android.com/tools/avdmanager).
