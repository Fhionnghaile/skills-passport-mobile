# Mobile — Skills Passport (iOS) + Companion (Android) — manual

*One page, eight blocks. Every figure here comes from this repository's own tests and README.*

## 1. Claim
One Ed25519 credential vector verifies identically in Python and in the JDK path the Android app uses, and canonical JSON is byte-identical to the signer's message.

## 2. What it is
Native mobile companions to Veritas. The iOS wallet verifies credentials locally and protects its own key in the Secure Enclave. The Android companion verifies the same credentials and uses the Keystore.

## 3. Demo
Capture to record: a 60-second simulator run (needs a Mac for iOS, or an Android emulator).

## 4. Run it yourself
**Prerequisites:** Xcode for iOS; Android Studio/Gradle for Android; Java and Python for the shared checks.

```bash
python shared/gen_vector.py
java shared/VerifierCheck.java
java shared/CanonicalCheck.java
# on a dev machine:
xcodebuild test   # iOS
./gradlew test    # Android
```

## 5. What you will see
Both shared checks report PASS. The XCTest and JUnit suites cover valid/tampered verification, canonical JSON and view-model state changes.

## 6. 30-second talking point
> One signature standard on two platforms. The security-critical core, signature verification and canonicalisation, was proved on a shared test vector before any UI was built.

## 7. Scope and limits
- Built in a sandbox with no Swift toolchain and no Gradle access, so the full xcodebuild/gradle suites were not run there.
- Secure Enclave and Keystore wrappers need real hardware or an emulator; they are covered by on-device tests.

## 8. Links
[Repository](https://github.com/fhionnghaile/skills-passport-mobile) · [Write-up](https://padraigfhionnghaile.com/work/skills-passport-mobile/) · [README](README.md)
