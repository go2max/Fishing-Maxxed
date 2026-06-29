# Readiness

Last updated: 2026-06-29

## Status

**LAUNCH PACKAGE STAGED / RELEASE EVIDENCE PENDING**

Fishing Maxxed has an offline-first catch journal baseline and a Play submission package staged in `docs/`, but it is not production-ready until current-branch verification, physical measurement/location acceptance, signed release evidence, hosted privacy policy URL, and Play Console metadata are recorded.

## Current evidence

- CameraX fish photo capture, calibrated length measurement, correction handles, uncertainty reporting, manual species confirmation, private catch records, redacted default exports, and device-local rankings are documented.
- Build checks are documented: `./gradlew testDebugUnitTest lintDebug assembleDebug`.
- Signed release path exists through `MAXXED_RELEASE_PROPERTIES`.
- Regulation fixture is explicitly non-authoritative and returns `Unable to verify - check official regulations` rather than authorizing Keeper.
- Play listing, data-safety notes, release notes, privacy policy draft, physical acceptance checklist, and release build runbook are staged under `docs/`.
- CI workflow is staged to run `testDebugUnitTest`, `lintDebug`, and `assembleDebug` on pull requests and pushes to `main`.

## Blocking launch gates

- Run debug unit tests, debug lint, and debug assemble on the current release candidate.
- Run signed release checks: `./gradlew testDebugUnitTest lintRelease assembleRelease bundleRelease`.
- Record APK/AAB hashes and signer evidence.
- Run physical acceptance on target Android hardware for photo capture, calibration, length adjustment, save/edit/delete, export redaction, and local leaderboard behavior.
- Verify privacy/store copy states exact coordinates are private by default and regulation results are not authoritative.
- Host the privacy policy and confirm the production privacy policy URL.
- Confirm Play Console listing, data-safety declaration, screenshots, support URL, and privacy policy URL.

## Ready when

Mark **READY** only after automated checks pass, signed artifacts are verified, physical capture/measurement/export testing is documented, non-authoritative regulation copy remains intact, and Play Store/legal metadata is complete.

## Evidence to paste before final submit

```text
Branch/commit:
Debug checks:
Release checks:
AAB path:
AAB SHA-256:
APK SHA-256, if generated:
Signer evidence:
Physical acceptance result:
Hosted privacy policy URL:
Support URL:
Play Console data-safety completed:
Screenshots uploaded:
Submitted by:
Submitted at:
```
