# Fishing Maxxed Release Acceptance

Last updated: 2026-06-29

## Automated gates

Run before internal testing:

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug
```

Run before signed upload:

```bash
./gradlew testDebugUnitTest lintRelease assembleRelease bundleRelease
```

## Required physical acceptance

- Camera opens and captures a fish/reference photo.
- Calibration handles can be adjusted.
- Manual no-photo catch entry works.
- Save, edit, delete, and reload persistence work.
- Default CSV export redacts exact coordinates.
- Explicit private-coordinate export is treated as private user data.
- Regulation output remains non-authoritative and does not authorize Keeper.
- Local leaderboard uses only records stored on the device.

## Evidence block

```text
Device:
Android version:
Debug checks result:
Release checks result:
Signed AAB path:
AAB SHA-256:
Signer evidence:
Physical smoke result:
Known blockers:
```
