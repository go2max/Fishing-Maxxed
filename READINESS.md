# Readiness

Last updated: 2026-06-29

## Status

**RELEASE CANDIDATE PACKAGE PREPARED / FINAL BUILD EVIDENCE PENDING**

Fishing Maxxed has an offline-first catch journal baseline and now has a launch-readiness package for Play submission prep. It is not production-ready until current-branch verification, signed release evidence, and physical measurement/location/export acceptance are recorded.

## Current evidence

- CameraX fish photo capture, calibrated length measurement, correction handles, uncertainty reporting, manual species confirmation, private catch records, redacted default exports, and device-local rankings are documented.
- Build checks are documented: `./gradlew testDebugUnitTest lintDebug assembleDebug`.
- Signed release path exists through `MAXXED_RELEASE_PROPERTIES`.
- Regulation fixture is explicitly non-authoritative and returns `Unable to verify - check official regulations` rather than authorizing Keeper.
- Release-safety unit tests cover measurement validity, invalid calibration rejection, coordinate-redacted default CSV export, explicit private-coordinate export behavior, non-authoritative regulation lockout, ambiguous-region lockout, and local-only leaderboard ordering.
- Android CI workflow runs debug unit tests, debug lint, and debug assemble for PRs, main, and `codex/**` branches.
- Play listing draft, data-safety draft, release acceptance checklist, and screenshot plan are tracked under `docs/`.

## Blocking launch gates

- Run debug unit tests, debug lint, and debug assemble on the current release candidate.
- Run signed release checks: `./gradlew testDebugUnitTest lintRelease assembleRelease bundleRelease`.
- Record APK/AAB hashes and signer evidence.
- Run physical acceptance on target Android hardware for photo capture, calibration, length adjustment, save/edit/delete, export redaction, and local leaderboard behavior.
- Verify privacy/store copy states exact coordinates are private by default and regulation results are not authoritative.
- Confirm Play Console listing, data-safety declaration, screenshots, support URL, and privacy policy URL.

## Ready when

Mark **READY FOR INTERNAL TESTING** after automated checks pass, a signed AAB is produced, and physical smoke testing finds no launch blockers.

Mark **READY FOR PRODUCTION** only after the internal testing build is accepted, screenshots are attached, Play Store/legal metadata is complete, the signed artifact evidence is recorded, and non-authoritative regulation copy remains intact.
