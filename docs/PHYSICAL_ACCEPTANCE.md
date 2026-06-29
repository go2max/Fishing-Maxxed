# Fishing Maxxed Physical Acceptance Checklist

Last updated: 2026-06-29

Run this on the target Android device before Play submission. Record device model, Android version, build artifact, signer evidence, and result.

## Test device

- Device:
- Android version:
- App version:
- Artifact path:
- AAB SHA-256:
- APK SHA-256, if generated:
- Signed by release key: Yes / No
- Tester:
- Date:

## Gate A — first launch and navigation

- App launches from cold start without crash.
- Header shows Fishing Maxxed and private on-device journal copy.
- Bottom navigation opens Capture, Journal, Rules, and Leaderboard.
- Theme toggle works and persists after app restart.

## Gate B — camera capture

- Camera permission prompt appears only when needed.
- Denying camera keeps the app usable.
- Settings button opens app settings after denial.
- Granting camera shows camera preview.
- Take photo saves a photo and opens measurement editor.
- Retake deletes/discards the current photo and returns to camera preview.

## Gate C — measurement

- Measurement editor displays the captured photo.
- Cyan fish line endpoints can be dragged.
- Yellow reference line endpoints can be dragged.
- Reference inches accepts decimal input.
- Invalid reference shows measurement unavailable.
- Valid reference shows length, confidence, and uncertainty.

## Gate D — manual no-photo catch

- With no photo, manual length field is enabled.
- Manual catch can be saved with length only.
- Manual catch can be saved with notes only.
- Manual catch without species is marked Unverified.

## Gate E — species and status

- Search finds common/scientific species entries.
- Selecting species confirms the species and populates the search field.
- Keeper is disabled unless rule engine allows it.
- Rule copy says regulation verification is non-authoritative.
- Unknown/missing species or missing measurement saves as Unverified.

## Gate F — private location

- Location permission is optional.
- Denying location still allows catch save.
- Granting location stores exact coordinates privately when last known location exists.
- UI says exact coordinates are stored privately and excluded from default export.
- Broad public region remains editable.

## Gate G — journal

- Saved catch appears at top of journal.
- Journal summary counts update.
- Filter by species/status works.
- Expanding a record shows details.
- Notes can be edited and saved.
- Status can be changed among allowed local statuses.
- Delete removes the record.

## Gate H — export redaction

- Export/share opens Android chooser.
- Exported CSV does not contain exact latitude.
- Exported CSV does not contain exact longitude.
- Export includes broad region, species, measurement, confidence, notes/status as expected.

## Gate I — leaderboard

- Measured catches appear in My Leaderboard.
- Ranking is sorted by measured length.
- Copy states records are only on this device and not global.
- Empty leaderboard state appears when no measured catches exist.

## Gate J — release install sanity

- Release build installs over previous internal build if same signature is used.
- Release build is not debuggable.
- App survives force stop and relaunch.
- App survives device rotation according to configured orientation behavior.
- App works offline / airplane mode for capture, journal, rules, and leaderboard.

## Result

- Pass / Fail:
- Blockers found:
- Screenshots captured for Play listing:
- Notes:
