# Functionality Report

## Implemented

- CameraX preview, permission recovery, app-private capture, and retake
- Known-reference calibration, four draggable correction handles, confidence, and uncertainty
- Manual catch fallback for no-photo or no-camera field entries
- Searchable bundled species catalog with explicit user confirmation and variant-name aliases
- Catch records with photo, notes, method, optional weight, status, exact private coordinates, and broad region
- Local journal filtering, detail, edit, delete, restart persistence, and privacy-safe CSV sharing
- Local catch analytics for journal counts, best length, average length, and leaderboard ranking
- Equal Masculine and Feminine control sets
- Local Catch, Keeper, Released, and Unverified model; Keeper fails closed behind rule authorization
- Coordinate-region resolver and rule evaluator for boundary ambiguity, season, slot, bag, gear, closure, freshness, and support
- SHA-256 and RSA-signed regulation-package verification interface with previous-package rollback
- Location-scoped native/introduced/invasive/unknown classification
- My Leaderboard limited to records on the device

## Truth Boundaries

The bundled regulation fixture is non-authoritative and never authorizes Keeper. The verifier and rollback path exist, but package import is not exposed because no production public key or independently maintained regulation feed exists. The app does not claim daily-current worldwide rules, legal advice, cloud synchronization, or global rankings.

## Verification

The Gradle command `testDebugUnitTest lintDebug assembleDebug bundleRelease` was attempted in the container on 2026-06-21. It stopped before configuration because the environment returned HTTP 403 while downloading Gradle 8.9. A follow-up `testDebugUnitTest lintDebug assembleDebug` attempt on 2026-06-28 used a writable Gradle cache but stopped before configuration because the environment could not reach `services.gradle.org`. No compatible cached Gradle distribution was available. Consequently unit tests, lint, APK/AAB creation, signing verification, and physical-device tests remain unverified in this environment.

Fixtures were added for raw-coordinate measurement scaling, normalized UI-handle measurement scaling, missing scale, coordinate redaction, legal size, undersize, overslot, closed season, bag limit, gear restriction, closure, ambiguous/unknown location, stale/unsupported data, and bundled Keeper enforcement.

## 2026-06-29 Polish Pass

- Fixed measurement calculation for the production Compose editor path. The editor stores handles as normalized 0-1 coordinates, and those handles now pass validation instead of being rejected by raw-pixel thresholds.
- Kept compatibility with existing raw-coordinate measurement tests and fixture-style calculations.
- Added regression coverage for the default normalized handle positions used after capture.
- Added a manual length/catch fallback so users can save a private record without camera access or a usable field photo.
- Expanded the bundled species catalog and added variant-name search coverage.
- Added journal and local leaderboard summaries plus clearer empty states.
- Added status chips for quick journal review of local, released, and unverified records.
- Added domain-level analytics coverage for summary cards and local leaderboard ordering.
- Added `qa/DEVICE_QA_CHECKLIST.md` for the Samsung S22 Ultra validation pass.
- Rechecked release posture: the bundled regulation fixture remains non-authoritative, Keeper remains disabled unless a trusted authoritative rule package approves it, and default export remains privacy-safe.
