# Modern Android development recommendations

**Verified against Android's documentation on October 29, 2026.** Android platform and AndroidX releases move quickly; check the linked release pages before upgrading or shipping. This guide distinguishes stable libraries from the Android 17 preview SDK.

## Recommended starting stack

| Area | This sample | Guidance |
|---|---|---|
| Language | Kotlin 2.4.10 | Use Kotlin for new Android application code. With AGP 9+, use built-in Kotlin support; do not also apply `org.jetbrains.kotlin.android`. |
| UI | Jetpack Compose | Google's strongly recommended UI toolkit for new apps. Compose is declarative and supports responsive layouts without parallel XML screen layouts. |
| Design system | Material 3 | Use Material components and semantic color roles. The sample uses opt-in dynamic color with a light/dark fallback. |
| Compose versions | Compose BOM 2026.09.00 | Import the BOM and omit individual Compose artifact versions so the libraries stay aligned. |
| Navigation | Navigation 3 1.2.0 | Use a back stack of typed, serializable keys when an app has multiple screens. The sample uses stable Navigation 3, not the available 1.3 alpha. |
| Adaptive navigation | Material adaptive navigation suite 1.4.0 | Let navigation adapt between a bottom bar and a rail as the app window changes size. |
| Lifecycle | Activity Compose 1.13.0; Lifecycle 2.11.0 | Collect flows with `collectAsStateWithLifecycle`; keep screen state in a screen-level ViewModel where that provides value. |
| Build | AGP 9.5.0; Gradle 9.6.0; JDK 17+ | AGP 9.5 supports API 37 and requires Gradle 9.6.0 and JDK 17. Pin versions; avoid dynamic versions such as `9.5.+`. |
| Platform | compileSdk / targetSdk 37 | The Android 17 setup page currently identifies API 37 as the Android 17 preview SDK. Use it for evaluation, not as a production target until the platform and required behavior changes are final. |

The versions above are stable releases where applicable. A newer alpha, beta, or preview can exist without being the right production default.

## Kotlin, Compose, and resources

- Use Compose with a single `ComponentActivity` as the app entry point. Compose is the screen UI; do not add XML layout files for screens unless interoperating with an existing View-based feature.
- Android apps still normally have XML for the manifest, themes, and localized string resources. Compose and XML resources are complementary; “Compose instead of XML layouts” does not mean “no XML files at all.”
- Use Material 3 components and `MaterialTheme` roles instead of hard-coding colors, typography, and shapes.
- On Android 12 and newer, use dynamic color when it suits the product; always retain a deliberate light/dark fallback for older devices and user preferences.
- Put user-facing text in localized string resources and provide meaningful accessibility labels for icon-only or otherwise ambiguous controls.
- Prefer standard Compose components and semantics over custom drawing when a standard component expresses the interaction. Test with larger font sizes, keyboard input, and TalkBack.

## State and architecture

Follow unidirectional data flow:

1. The screen renders immutable UI state.
2. User input calls an action on a screen-level state holder such as a `ViewModel`.
3. The state holder updates and exposes observable state.
4. The UI collects that state in a lifecycle-aware way.

The sample's `ShowcaseViewModel` exposes a `StateFlow<ShowcaseUiState>`, and its Compose root collects it with `collectAsStateWithLifecycle()`. The tap count and theme preference use `SavedStateHandle` because this demo intends to restore small UI state after process recreation.

For production apps:

- Keep business logic and data access out of composables. Put real data operations behind repositories and use `Flow`/suspending functions where appropriate.
- Use a domain/use-case layer when it centralizes complex or reused business rules; do not add a layer just to satisfy a template.
- Use `SavedStateHandle` for small, transient state needed to restore a screen. Use persistent storage such as DataStore or Room for durable user data.
- Process ViewModel events into state rather than relying on one-off ViewModel-to-UI event streams.
- Keep composables stateless where practical: pass the state they need and callbacks for actions. This improves reuse, previews, and testing.

## Navigation and adaptive layouts

- For multiple destinations, use Navigation 3 with serializable route keys and an explicit back stack. Add deep links and restore navigation state when the product needs them.
- Design for the **app window**, not just the physical device category. Users resize app windows, rotate devices, and use tablets and foldables.
- Use Material 3 adaptive APIs, window size classes, or layout constraints to change information density and navigation placement. Avoid merely stretching a phone layout across a tablet.
- `NavigationSuiteScaffold` automatically selects an appropriate navigation bar or rail. The overview in `sample-app-2` also moves its content cards into two columns when the available width allows it.
- Test compact and expanded windows, split-screen, rotation, and foldable postures. Do not equate a device model with a fixed screen size.

## Platform APIs, edge-to-edge, and release targets

- `compileSdk` controls which platform APIs the app can compile against. `targetSdk` opts the app into that platform's compatibility and behavior changes; it is not the same as `minSdk`.
- Keep `compileSdk` current and regularly test the behavior changes for the `targetSdk` you plan to ship. For a real release, target the latest finalized Android release required by your release plan and Play policy.
- `sample-app-2` sets both SDK values to 37 to exercise the Android 17 preview. The API 36 `medium_phone` emulator can still run the app's API-36-compatible code, but this does not replace testing on an API 37 emulator.
- Call `enableEdgeToEdge()` from the Activity and let inset-aware Compose components such as app bars and `Scaffold` lay out content around system bars. Avoid hard-coded status/navigation bar heights.
- Guard calls to newer platform APIs with their API-level checks or use AndroidX compatibility APIs. Prefer public platform APIs; test behavior changes and back navigation on the latest emulator.

## Build, dependency, and test practices

- Keep plugin versions in the version catalog and use the Gradle wrapper. Review AGP, Gradle, Kotlin, and JDK compatibility together before upgrading.
- Use the Compose BOM for Compose artifacts. Pin versions of libraries outside the BOM, and choose stable releases for production dependencies unless a preview is a deliberate requirement.
- AGP 9 enables built-in Kotlin. Apply the Compose compiler Gradle plugin at the same Kotlin version, and avoid applying the separate Kotlin Android plugin alongside built-in Kotlin.
- Add unit tests for state holders and business logic, and Compose UI tests for important user flows and semantics. Add instrumentation tests on multiple API levels and form factors where platform behavior matters.
- Run release builds and inspect app size, startup, and accessibility before shipping. Keep signing keys and other secrets out of source control.

## Build and run this showcase

From the workspace root, with the API 36 AVD running:

```powershell
.\sample-app-2\gradlew.bat -p .\sample-app-2 testDebugUnitTest assembleDebug
.\scripts\run-sample-2.ps1
```

The SDK setup script installs API 37 and Build Tools 37 for the sample. To create and test against an Android 17 AVD, select the Android 17 system image in Android Studio Device Manager. The existing CLI-created `medium_phone` device uses API 36.

## Official references

- [Android 17 SDK setup](https://developer.android.com/about/versions/17/setup-sdk)
- [Android 17 features and APIs](https://developer.android.com/about/versions/17/features)
- [Android Gradle Plugin 9.5 release notes](https://developer.android.com/build/releases/agp-9-5-0-release-notes)
- [Migrate to built-in Kotlin](https://developer.android.com/build/migrate-to-built-in-kotlin)
- [Jetpack Compose](https://developer.android.com/develop/ui/compose)
- [Material 3 in Compose](https://developer.android.com/develop/ui/compose/designsystems/material3)
- [Compose BOM](https://developer.android.com/develop/ui/compose/bom)
- [Compose compiler setup](https://developer.android.com/develop/ui/compose/compiler)
- [Compose state](https://developer.android.com/develop/ui/compose/state)
- [Architecture recommendations](https://developer.android.com/topic/architecture/recommendations)
- [Navigation 3](https://developer.android.com/guide/navigation/navigation-3)
- [Adaptive Compose layouts](https://developer.android.com/develop/ui/compose/layouts/adaptive)
- [Lifecycle-aware state collection](https://developer.android.com/topic/libraries/architecture/coroutines)
