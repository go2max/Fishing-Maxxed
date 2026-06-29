# Play Submission Checklist

Use this after the debug/device QA pass succeeds.

## Build

- `./gradlew testDebugUnitTest lintDebug assembleDebug` passes.
- Release signing properties are configured outside the repo.
- `./gradlew testDebugUnitTest lintRelease assembleRelease bundleRelease` passes.
- APK/AAB paths and hashes are recorded.
- `versionCode` is higher than any uploaded build.

## Store Listing

- App name: Fishing Maxxed.
- Short description is final.
- Full description is final and does not claim official regulation authorization.
- Screenshots show capture, measurement, manual logging, journal, and leaderboard.
- Feature graphic is prepared.
- App icon renders correctly on light and dark launchers.

## Privacy And Data Safety

- Privacy policy has real support email/contact details.
- Data Safety answers match the app: no account, no analytics, no ads, no cloud sync.
- Default CSV export redaction is verified.
- Location permission explanation is accurate.

## Device Acceptance

- Samsung S22 Ultra fresh install passes first-launch smoke test.
- Camera permission deny/allow paths pass.
- Measurement fixtures are tested at multiple distances/lighting conditions.
- Manual no-photo entry passes.
- Journal edit/delete/reopen persistence passes.
- Rules/Keeper fail-closed behavior passes.

## Release Decision

- Internal testing only:
- Production ready:
- Remaining blockers:
