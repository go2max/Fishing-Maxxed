# Functionality Report

## Implemented

- CameraX preview, permission recovery, app-private capture, and retake
- Known-reference calibration, four draggable correction handles, confidence, and uncertainty
- Searchable bundled species catalog with explicit user confirmation
- Catch records with photo, notes, method, optional weight, status, exact private coordinates, and broad region
- Local journal filtering, detail, edit, delete, restart persistence, and privacy-safe CSV sharing
- Equal Masculine and Feminine control sets
- Local Catch, Keeper, Released, and Unverified model; Keeper fails closed behind rule authorization
- Coordinate-region resolver and rule evaluator for boundary ambiguity, season, slot, bag, gear, closure, freshness, and support
- SHA-256 and RSA-signed regulation-package verification interface with previous-package rollback
- Location-scoped native/introduced/invasive/unknown classification
- My Leaderboard limited to records on the device

## Truth Boundaries

The bundled regulation fixture is non-authoritative and never authorizes Keeper. The verifier and rollback path exist, but package import is not exposed because no production public key or independently maintained regulation feed exists. The app does not claim daily-current worldwide rules, legal advice, cloud synchronization, or global rankings.

## Verification

The Gradle command `testDebugUnitTest lintDebug assembleDebug bundleRelease` was attempted in the container on 2026-06-21. It stopped before configuration because the environment returned HTTP 403 while downloading Gradle 8.9. No compatible cached Gradle distribution was available. Consequently unit tests, lint, APK/AAB creation, signing verification, and physical-device tests remain unverified in this environment.

Fixtures were added for measurement scaling/missing scale, coordinate redaction, legal size, undersize, overslot, closed season, bag limit, gear restriction, closure, ambiguous/unknown location, stale/unsupported data, and bundled Keeper enforcement.
