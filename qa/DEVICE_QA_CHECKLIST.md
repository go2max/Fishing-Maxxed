# Fishing Maxxed Device QA Checklist

Use this checklist on the Samsung S22 Ultra before merging Fishing Maxxed into the larger app-suite release flow.

## Build Verification

- Run `./gradlew testDebugUnitTest lintDebug assembleDebug`.
- Run `./gradlew testDebugUnitTest lintRelease assembleRelease bundleRelease` after release signing is configured.
- Confirm `versionCode` and `versionName` match the planned Play upload.

## First Launch

- Fresh install opens without crash.
- Camera permission prompt is understandable.
- Denying camera still allows a manual catch record.
- Granting camera opens a live preview.
- Theme toggle persists after restart.

## Capture And Measurement

- Capture a fish or printed fixture photo.
- Drag cyan fish handles and yellow reference handles.
- Confirm a measured length appears.
- Change the reference length and confirm the measured length updates.
- Confirm impossible/collapsed handles show the unavailable message.
- Retake deletes the pending photo and returns to preview.

## Manual Catch Fallback

- Deny camera or skip photo capture.
- Enter manual length, species, notes, method, and region.
- Save the catch.
- Confirm the record appears in Journal and Leaderboard.
- Confirm manual records show low confidence and remain local-only.

## Species Confirmation

- Search by common name, such as `bass`.
- Search by scientific genus, such as `Oncorhynchus`.
- Search by variant, such as `steelhead`.
- Save a catch without confirmed species and confirm it becomes `UNVERIFIED`.

## Location Privacy

- Deny location and save a catch successfully.
- Grant location and save a catch.
- Confirm UI says exact coordinates are private.
- Export CSV and confirm exact latitude, longitude, and private photo path are omitted.

## Journal

- Edit notes and save changes.
- Change status among Local Catch, Released, and Unverified.
- Use status filter chips for All, Local Catch, Released, and Unverified.
- Delete a record.
- Close and reopen the app; confirm records persist.
- Confirm journal summary counts update correctly.

## Rules And Keeper Safety

- Confirm Keeper is disabled with bundled demo rules.
- Confirm unsupported or ambiguous cases show `Unable to verify - check official regulations`.
- Confirm Rules page does not claim daily-current regulation coverage.
- Confirm no screen implies legal authorization to keep a fish.

## Leaderboard

- Confirm empty state appears before measured records exist.
- Add measured records and confirm ranking is local to the device.
- Confirm best and average length summaries update.
- Confirm equal lengths sort newest first.

## Release Blockers

- Add real support email/contact details to the Play privacy policy.
- Produce signed APK/AAB with the private release key.
- Save device QA notes with date, device, Android version, and fixture results.
