# Fishing Maxxed Data Safety Draft

Last updated: 2026-06-29

Use this as the working Play Console data-safety source of truth. Confirm against the final signed build before submission.

## App behavior summary

Fishing Maxxed is offline-first. The app stores catch records locally on the user's device. It does not include account creation, cloud sync, third-party analytics, advertising SDKs, public posting, or global leaderboards in this release candidate.

## Data types handled

| Data type | Collected by app? | Shared with third parties? | Purpose | Notes |
|---|---:|---:|---|---|
| Photos and videos | Yes, user-initiated photos | No | App functionality | Catch photos are saved locally for the journal. |
| Approximate location | Optional | No | App functionality | Used to infer broad public region when permission is granted. |
| Precise location | Optional | No | App functionality | Stored locally/private when permission is granted; excluded from default CSV export. |
| App activity | Yes, local records only | No | App functionality | Catch entries, notes, measurement values, species confirmation, status, and local leaderboard are stored on device. |
| Device or other IDs | No intentional collection | No | Not used | No account, analytics, ads, or cloud identity in this RC. |
| Personal info | No intentional collection | No | Not used | User may type personal notes voluntarily; app does not require them. |

## Permissions

- `CAMERA`: lets the user photograph a catch for local measurement and journal records.
- `ACCESS_COARSE_LOCATION`: optional broad location support.
- `ACCESS_FINE_LOCATION`: optional private exact coordinate storage and broad region inference.

## Sharing and export behavior

Default CSV export is privacy-safe by design:

- Includes broad public region.
- Does not include exact latitude or longitude.
- Uses local share sheet only when the user taps export.

There is an internal export option in code that can include exact coordinates when explicitly requested by a future caller, but the user-facing default export path does not expose exact coordinates in this release candidate.

## Security posture

- `android:allowBackup="false"` is configured in the manifest.
- No cloud sync is claimed.
- No public/global leaderboard is claimed.
- No official regulation verification is claimed.

## Play Console answers draft

- Does the app collect or share user data? **Yes, collects locally on device. No third-party sharing.**
- Is all user data encrypted in transit? **Not applicable for core app behavior; no network transmission in this release candidate.**
- Can users request data deletion? **Data can be deleted on-device by deleting catch records or uninstalling the app.**
- Is data collection optional? **Camera/location are permission-gated; manual no-photo catch logging is available.**
- Does the app use advertising ID? **No.**

## Submission guardrails

Before submission, re-check the final merged branch and signed build for any added SDKs, network calls, analytics, ads, crash reporting, or cloud storage. Any addition changes this declaration.
