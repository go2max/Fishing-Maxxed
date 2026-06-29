# Local Validation Commands

Run these from the Fishing Maxxed repository root on the machine that has Android SDK and network access.

## Debug Gate

```bash
./gradlew testDebugUnitTest lintDebug assembleDebug
```

## Release Gate

Set `MAXXED_RELEASE_PROPERTIES` to a release signing properties file first.

```bash
./gradlew testDebugUnitTest lintRelease assembleRelease bundleRelease
```

## Expected Evidence To Save

- Command used
- Date and machine
- Java version
- Android SDK path
- Final Gradle result
- APK/AAB output paths
- Any warnings that remain
