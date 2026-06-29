# Fishing Maxxed Release Notes

## Internal Testing Draft

Fishing Maxxed is ready for a focused internal validation pass.

- Added private on-device catch journal workflows.
- Added camera capture with known-reference measurement handles.
- Added manual no-photo catch logging.
- Added species confirmation and local-only leaderboard summaries.
- Added privacy-safe CSV export that excludes exact coordinates and photo paths by default.
- Keeps regulation output conservative: unsupported cases show `Unable to verify - check official regulations`.

## Tester Focus

- Capture a photo and verify measurement handles produce a reasonable length.
- Log a catch manually without a photo.
- Confirm species search works by common name, scientific name, and alias.
- Confirm default CSV export does not include exact coordinates or private photo paths.
- Confirm Keeper cannot be selected with bundled demo rules.

## Known Internal-Test Limits

- Regulation data is a non-authoritative fixture.
- Signed release artifacts are not recorded yet.
- Physical measurement accuracy still requires fixture validation.
- Support contact details must be finalized before public Play submission.
