# Fishing Maxxed Release Build Runbook

Last updated: 2026-06-29

## 1. Prepare local environment

```bash
cd /path/to/Fishing-Maxxed

export JAVA_HOME=$(/usr/libexec/java_home -v 17 2>/dev/null || echo "/Applications/Android Studio.app/Contents/jbr/Contents/Home")
export ANDROID_HOME="$HOME/Library/Android/sdk"
export PATH="$JAVA_HOME/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"

java -version
./gradlew --version
```

## 2. Create release signing properties

Do not commit this file.

```properties
storeFile=/absolute/path/to/maxxed-release.keystore
storePassword=REPLACE_WITH_STORE_PASSWORD
keyAlias=REPLACE_WITH_KEY_ALIAS
keyPassword=REPLACE_WITH_KEY_PASSWORD
```

Then export it:

```bash
export MAXXED_RELEASE_PROPERTIES=/absolute/path/to/fishing-release.properties
```

## 3. Run verification

```bash
./gradlew clean testDebugUnitTest lintDebug assembleDebug
./gradlew testDebugUnitTest lintRelease assembleRelease bundleRelease
```

## 4. Confirm artifact locations

Expected release artifacts:

```bash
ls -lah app/build/outputs/apk/release/
ls -lah app/build/outputs/bundle/release/
```

Expected AAB:

```bash
app/build/outputs/bundle/release/app-release.aab
```

## 5. Record hashes

```bash
shasum -a 256 app/build/outputs/bundle/release/app-release.aab
shasum -a 256 app/build/outputs/apk/release/app-release.apk 2>/dev/null || true
```

Paste the result into `READINESS.md` or the PR before merge.

## 6. Confirm signer evidence

```bash
jarsigner -verify -verbose -certs app/build/outputs/bundle/release/app-release.aab | head -80
```

Or use Android build tools when available:

```bash
$ANDROID_HOME/build-tools/35.0.0/apksigner verify --print-certs app/build/outputs/apk/release/app-release.apk
```

## 7. Install and physically test

For APK physical testing:

```bash
adb install -r app/build/outputs/apk/release/app-release.apk
```

Then complete `docs/PHYSICAL_ACCEPTANCE.md`.

## 8. Play Console submission checklist

- Upload signed AAB.
- Use copy from `docs/PLAY_STORE_LISTING.md`.
- Use release notes from `docs/RELEASE_NOTES.md`.
- Publish hosted privacy policy from `docs/PRIVACY_POLICY.md`.
- Complete Data safety using `docs/DATA_SAFETY.md`.
- Upload screenshots that show privacy and non-authoritative regulation copy.

## Stop condition

Stop and do not submit if any of these fail:

- Release build is unsigned.
- App crashes during first launch.
- Export includes exact coordinates by default.
- Store copy claims official regulation verification.
- App contains network/ads/analytics SDKs not reflected in Data safety.
