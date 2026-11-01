# Android CLI and coding agents

Google's [Android CLI](https://developer.android.com/tools/agents) gives terminal tools and coding agents a consistent way to manage Android SDK packages, scaffold projects, manage AVDs, build apps, and deploy to devices. It complements Android Studio rather than replacing the emulator or the Android SDK.

## Useful commands

Run these from a PowerShell terminal after installing Android CLI:

```powershell
android --no-metrics info
android --no-metrics sdk list
android --no-metrics sdk install platform-tools emulator platforms/android-36
android --no-metrics emulator list
android --no-metrics build :app
android --no-metrics run :app
```

Run build and deployment commands from `sample-app` when using the `:app` project reference:

```powershell
Push-Location .\sample-app
try {
    android --no-metrics build :app
    android --no-metrics run :app
}
finally {
    Pop-Location
}
```

The CLI can also scaffold projects (`android create`) and install an APK built elsewhere (`android install --apks=...`). See the [Android CLI command reference](https://developer.android.com/tools/agents/android-cli/commands).

## Add Android CLI skills to an agent

If you use an agent integration that supports Google's Android CLI skills, run:

```powershell
android init
```

This installs the bundled `android-cli` skill into detected agent skill directories. Review the CLI documentation and your agent's setup before running it; it can write files to those agent configuration locations. Google's page lists integrations and prerequisites in [Agent tools and resources](https://developer.android.com/tools/agents).

Android CLI reports basic command and option usage by default. To disable its metrics, keep the `--no-metrics` flag before each command, as in the examples above.
