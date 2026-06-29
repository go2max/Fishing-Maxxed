# Fishing Maxxed Release Acceptance

Last updated: 2026-06-29

## Candidate

- Repository: `go2max/Fishing-Maxxed`
- Branch: `codex/fishing-launch-readiness`
- Package: `com.maxxed.fishingmaxxed`
- Version: `1.0.0` / versionCode `2`
- Submission target: Play Console internal testing first, then production after physical acceptance.

## Automated checks

Run from the repo root:

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug
```

Signed release path:

```bash
export MAXXED_RELEASE_PROPERTIES=/absolute/path/to/release-signing.properties
./gradlew testDebugUnitTest lintRelease assembleRelease bundleRelease
```

Expected signed artifacts:

- `app/build/outputs/apk/release/app-release.apk`
- `app/build/outputs/bundle/release/app-release.aab`

Record hashes after build:

```bash
shasum -a 256 app/build/outputs/apk/release/app-release.apk app/build/outputs/bundle/release/app-release.aab
```

Record signer evidence:

```bash
$ANDROID_HOME/build-tools/35.0.0/apksigner verify --verbose --print-certs app/build/outputs/apk/release/app-release.apk
```

Use the installed build-tools version if `35.0.0` is not present.

## Physical device acceptance

Run on target Android hardware before Play submission.

### Fresh install

- [ ] App installs cleanly.
- [ ] App launches without crash.
- [ ] App name and icon appear correctly.
- [ ] Bottom navigation works for Capture, Journal, Rules, and Leaderboard.
- [ ] Theme toggle works and persists after relaunch.

### Camera and measurement

- [ ] Camera permission panel appears when permission is missing.
- [ ] Permission request opens correctly.
- [ ] Live camera preview appears after permission.
- [ ] Photo capture succeeds.
- [ ] Retake deletes current capture and returns to preview.
- [ ] Fish endpoints can be dragged.
- [ ] Reference endpoints can be dragged.
- [ ] Reference length field updates measurement.
- [ ] Invalid/zero reference length blocks measurement.
- [ ] Measurement shows length, confidence, and uncertainty.

### Manual entry

- [ ] Manual no-photo length field is available before taking photo.
- [ ] Manual catch can be saved without photo.
- [ ] Unknown/unconfirmed species saves as Unverified when appropriate.

### Species and status

- [ ] Species search works by common name.
- [ ] Species search works by scientific name.
- [ ] Species confirmation message updates.
- [ ] Keeper cannot be selected when regulation output is unable to verify.
- [ ] Released/local/unverified statuses can be selected where allowed.

### Location and privacy

- [ ] Location permission prompt opens from Capture screen.
- [ ] App remains usable when location permission is denied.
- [ ] Broad public region can be edited manually.
- [ ] Exact coordinates copy says private when available.
- [ ] Journal default export does not include latitude/longitude headers.
- [ ] Journal default export does not include exact coordinate values.

### Journal

- [ ] Saved catch appears at the top of Journal.
- [ ] Summary cards update.
- [ ] Text/status filtering works.
- [ ] Expanded record shows photo when available.
- [ ] Notes can be edited and saved.
- [ ] Record can be deleted.

### Rules

- [ ] Rules screen clearly says `Unable to verify - check official regulations`.
- [ ] Screen does not claim official authorization.
- [ ] Source metadata is visible.
- [ ] Warning to check official agency is visible.

### Leaderboard

- [ ] Empty state appears with no measured catches.
- [ ] Measured catches appear in descending length order.
- [ ] Copy says device-only / no global ranking.

## Submission blockers

Do not submit if any of these are true:

- App crashes on fresh launch.
- Camera capture cannot complete on target hardware.
- Saved records disappear after relaunch.
- Default export includes exact latitude or longitude.
- Keeper can be selected from the bundled non-authoritative regulation fixture.
- Play listing claims official regulation verification, AI species recognition, global leaderboard, cloud sync, or guaranteed measurement accuracy.
- Signed AAB is missing or unsigned.
- Privacy/data-safety declaration no longer matches the final build.

## Launch disposition

- `READY FOR INTERNAL TESTING`: automated checks pass, signed AAB exists, and no physical acceptance blocker is found.
- `READY FOR PRODUCTION`: internal testing build is accepted, store listing is complete, screenshots are attached, and data-safety declaration matches final signed build.
