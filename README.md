# Fishing Maxxed

Fishing Maxxed is an offline-first Android catch journal. It captures a fish photo with CameraX, measures length against a known reference with draggable correction handles, reports uncertainty, requires manual species confirmation, stores private catch records, redacts exact coordinates from default exports, and ranks only records on the current device.

## Website Summary

Use this copy when referencing the app externally:

> Fishing Maxxed is an offline Android catch journal for recording fish photos, calibrated length estimates, species confirmation, private catch details, redacted exports, and local-only leaderboards. It is designed for personal catch tracking and does not claim official regulation verification or global competition support.

## Readiness

Current release status is tracked in [`READINESS.md`](READINESS.md). Do not mark this app production-ready until current-branch checks, signed artifacts, physical capture/measurement/export acceptance, and non-authoritative regulation copy are verified.

## Features

- CameraX fish photo capture
- Known-reference length measurement
- Draggable correction handles
- Uncertainty reporting
- Manual species confirmation
- Private catch records
- Default export redaction for exact coordinates
- Device-local ranking only
- Non-authoritative regulation fixture that cannot authorize Keeper

## Privacy And Safety Posture

- Records are intended to remain local/private unless the user exports them.
- Default exports must redact exact coordinates.
- Regulation output is not authoritative and must direct users to official regulations.
- The app should not claim daily-current worldwide rules, legal keeper authorization, global leaderboards, cloud identity, or public ranking unless those systems are added and separately verified.

## Build

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug
```

Release signing is opt-in. Set `MAXXED_RELEASE_PROPERTIES` to a properties file containing `storeFile`, `storePassword`, `keyAlias`, and `keyPassword`, then run:

```bash
./gradlew testDebugUnitTest lintRelease assembleRelease bundleRelease
```

## Release Gate

Before Play submission:

1. Run debug and signed-release checks on the release candidate.
2. Build signed APK/AAB artifacts and record hashes plus signer evidence.
3. Run physical acceptance for capture, calibration, length adjustment, save/edit/delete, export redaction, and local leaderboard behavior.
4. Confirm in-app and store copy keeps regulation output non-authoritative.
5. Verify support/privacy URLs, screenshots, Play Console data-safety declaration, and release notes.

## Regulation Limitation

The bundled regulation fixture is non-authoritative. It exercises the local evaluator but always returns `Unable to verify - check official regulations` and cannot authorize Keeper. No daily-current worldwide rules or global leaderboard are claimed.
