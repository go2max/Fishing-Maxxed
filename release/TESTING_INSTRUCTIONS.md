# Testing Instructions

Build under test:

- APK: `/Users/maxmac/Desktop/MaxxedMobileApps/codex/same-day-maxxed-apks/FishingMaxxed/release/output/Fishing-MAXXED-release.apk`
- AAB: `/Users/maxmac/Desktop/MaxxedMobileApps/codex/same-day-maxxed-apks/FishingMaxxed/release/output/Fishing-MAXXED-release.aab`

Completed checks on 2026-06-22 UTC:

- `MAXXED_RELEASE_PROPERTIES=... ./gradlew app:signingReport`
- `MAXXED_RELEASE_PROPERTIES=... ./gradlew clean testDebugUnitTest lintDebug assembleRelease bundleRelease`
- `apksigner verify --verbose --print-certs`
- `jarsigner -verify -verbose -certs`
- Samsung device install after uninstalling an older differently signed `com.maxxed.fishingmaxxed`
- Launch smoke test with `adb shell monkey -p com.maxxed.fishingmaxxed -c android.intent.category.LAUNCHER 1`

Observed result:

- Release APK installed successfully.
- Launcher smoke test succeeded with no immediate crash reported by `adb shell monkey`.
- The captured log window did not show an immediate app crash attributed to `com.maxxed.fishingmaxxed`.

Mandatory testing still missing for READY:

- fish measurement validation on known objects
- species confirmation and journal workflow validation
- exact-location privacy and export-redaction validation
- California regulation engine boundary, season, bag, slot, closure, and freshness validation
- light/dark, font-scale, airplane-mode, and process-restart checks against real production features
