# FishingMaxxed

## Current Status

This is a standalone Android Studio project that currently builds a debug APK and opens to a basic Compose screen. It is not yet feature-complete relative to the same-day task brief.

## Build

```bash
./gradlew testDebugUnitTest
./gradlew lintDebug
./gradlew assembleDebug
```

## Build APK For Individual Testing

```bash
./gradlew assembleDebug
open app/build/outputs/apk/debug/
```

Primary test artifact:

- `app/build/outputs/apk/debug/app-debug.apk`

## Install

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Current Permissions

- None requested yet

## Verified in This Session

- `testDebugUnitTest` passed
- `lintDebug` passed
- `assembleDebug` passed
- Individual debug APK was built for direct install/testing
- Debug APK copied to `../Deliverables/FishingMaxxed-debug.apk`

## Current Workflow

- Launches into a Compose-based placeholder home screen

## Major Limitations

- No photo capture yet
- No manual fish measurement yet
- No catalog or suggested-match flow yet
- No catch journal yet
- No local rules engine or privacy/export handling yet

## Next Release Step

Implement the offline catch-record flow and local regulation prototype before claiming acceptance against the task brief.
