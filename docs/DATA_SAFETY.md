# Fishing Maxxed Data Safety Notes

Last updated: 2026-06-29

Use this as the working source for the Play Console Data safety form. Verify against the actual Play Console prompts at submission time.

## Collection / sharing posture

Fishing Maxxed is designed as a local-first app. It does not require an account and does not include cloud sync, public leaderboards, advertising SDKs, analytics SDKs, or third-party sharing in the current baseline.

## Data types handled by app

### Photos and videos

- Purpose: User takes a catch photo for private journal and length estimate.
- Storage: App-private device storage.
- Sharing: Not shared by the app unless the user intentionally exports or shares outside the app.
- Required: Optional. Manual no-photo entries are supported.

### Location

- Purpose: Optional private catch location and broad region resolution.
- Storage: App-private catch record when user grants permission.
- Sharing: Default CSV export excludes exact latitude/longitude.
- Required: Optional. User can enter broad region manually.

### App activity / user content

- Purpose: Catch notes, species selection, method/gear, weight, length estimate, status, and local leaderboard.
- Storage: App-private local journal.
- Sharing: Default export is user-initiated and redacts exact coordinates.
- Required: Optional fields vary by catch entry.

## Security and privacy statements

- No account is required.
- No cloud sync is claimed.
- No global/public leaderboard is claimed.
- Exact coordinates are private by default.
- Default CSV export uses broad public region instead of exact coordinates.
- Regulation guidance is non-authoritative and directs users to official regulations.

## Play Console answer draft

- Does the app collect or share user data? **Yes**, because photos, location, and user-entered catch details may be stored by the app locally and can be exported by the user.
- Is all user data encrypted in transit? **Not applicable for baseline local-only operation**, unless a future network feature is added.
- Can users request data deletion? **Yes, locally in app** through record deletion and app uninstall/clear storage. There is no account-held cloud data in this baseline.
- Is data shared with third parties? **No**, not by the baseline app. User-initiated Android share/export is user-controlled.

## Submission verification checklist

- Confirm the production build has no analytics, ads, crash reporting, or network SDKs added.
- Confirm no INTERNET permission is present unless a future feature requires it.
- Confirm CSV export remains redacted by default.
- Confirm location permission copy explains exact coordinates are private.
- Confirm privacy policy URL is live before Play submission.
