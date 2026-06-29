# Readiness

Last updated: 2026-06-29

## Status

**RELEASE CANDIDATE PACKAGE PREPARED / FINAL BUILD EVIDENCE PENDING**

Fishing Maxxed has an offline-first catch journal baseline, a Play submission package, and release-safety validation coverage staged for internal testing prep. It is not production-ready until current-branch verification, physical measurement/location acceptance, signed release evidence, hosted privacy policy URL, and Play Console metadata are recorded.

## Current evidence

- CameraX fish photo capture, calibrated length measurement, correction handles, uncertainty reporting, manual species confirmation, private catch records, redacted default exports, and device-local rankings are documented.
- Build checks are documented: `./gradlew testDebugUnitTest lintDebug assembleDebug`.
- Signed release path exists through `MAXXED_RELEASE_PROPERTIES`.
- Regulation fixture is explicitly non-authoritative and returns `Unable to verify - check official regulations` rather than authorizing Keeper.
- Play listing, data-safety notes, release notes, privacy policy draft, physical acceptance checklist, and release build runbook are staged under `docs/`.
- Release-safety unit tests cover measurement validity, invalid calibration rejection, coordinate-redacted default CSV export, explicit private-coordinate export behavior, non-authoritative regulation lockout, ambiguous-region lockout, and local-only leaderboard ordering.
- Android CI runs debug unit tests, debug lint, and debug assemble for pull requests and review branches.

## Blocking launch gates

- Run debug unit tests, debug lint, and debug assemble on the current release candidate.
- Run signed release checks: `./gradlew testDebugUnitTest lintRelease assembleRelease bundleRelease`.
- Record APK/AAB hashes and signer evidence.
- Run physical acceptance on target Android hardware for photo capture, calibration, length adjustment, save/edit/delete, export redaction, and local leaderboard behavior.
- Verify privacy/store copy states exact coordinates are private by default and regulation results are not authoritative.
- Host the privacy policy and confirm the production privacy policy URL.
- Confirm Play Console listing, data-safety declaration, screenshots, support URL, and privacy policy URL.

## Ready when

Mark **READY FOR INTERNAL TESTING** after automated checks pass, a signed AAB is produced, and physical smoke testing finds no launch blockers.

Mark **READY FOR PRODUCTION** only after the internal testing build is accepted, screenshots are attached, Play Store/legal metadata is complete, the signed artifact evidence is recorded, and non-authoritative regulation copy remains intact.

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
