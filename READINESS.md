# Readiness

Last updated: 2026-06-25

## Status

**IMPLEMENTED BASELINE / RELEASE EVIDENCE PENDING**

Fishing Maxxed has an offline-first catch journal baseline, but it is not production-ready until current-branch verification, physical measurement/location acceptance, and signed release evidence are recorded.

## Current evidence

- CameraX fish photo capture, calibrated length measurement, correction handles, uncertainty reporting, manual species confirmation, private catch records, redacted default exports, and device-local rankings are documented.
- Build checks are documented: `./gradlew testDebugUnitTest lintDebug assembleDebug`.
- Signed release path exists through `MAXXED_RELEASE_PROPERTIES`.
- Regulation fixture is explicitly non-authoritative and returns `Unable to verify - check official regulations` rather than authorizing Keeper.

## Blocking launch gates

- Run debug unit tests, debug lint, and debug assemble on the current release candidate.
- Run signed release checks: `./gradlew testDebugUnitTest lintRelease assembleRelease bundleRelease`.
- Record APK/AAB hashes and signer evidence.
- Run physical acceptance on target Android hardware for photo capture, calibration, length adjustment, save/edit/delete, export redaction, and local leaderboard behavior.
- Verify privacy/store copy states exact coordinates are private by default and regulation results are not authoritative.
- Confirm Play Console listing, data-safety declaration, screenshots, support URL, and privacy policy URL.

## Ready when

Mark **READY** only after automated checks pass, signed artifacts are verified, physical capture/measurement/export testing is documented, non-authoritative regulation copy remains intact, and Play Store/legal metadata is complete.