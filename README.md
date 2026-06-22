# Fishing Maxxed

Fishing Maxxed is an offline-first Android catch journal. It captures a fish photo with CameraX, measures length against a known reference with draggable correction handles, reports uncertainty, requires manual species confirmation, stores private catch records, redacts exact coordinates from default exports, and ranks only records on the current device.

## Build

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug
```

Release signing is opt-in. Set `MAXXED_RELEASE_PROPERTIES` to a properties file containing `storeFile`, `storePassword`, `keyAlias`, and `keyPassword`, then run:

```bash
./gradlew testDebugUnitTest lintRelease assembleRelease bundleRelease
```

The bundled regulation fixture is non-authoritative. It exercises the local evaluator but always returns `Unable to verify - check official regulations` and cannot authorize Keeper. No daily-current worldwide rules or global leaderboard are claimed.
