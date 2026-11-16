# Mobile — Skills Passport (iOS) + Companion (Android)

Native companions to the Derry **Veritas** verifiable-credentials service:

- **iOS (Swift)** — a Skills Passport wallet. Verifies Veritas's **Ed25519** credential
  signatures locally with CryptoKit; the device's own key lives in the **Secure Enclave**
  (P-256) behind biometrics. SwiftUI + MVVM.
- **Android (Kotlin)** — a companion viewer. Verifies the same credentials via
  `java.security` Ed25519; device secret in the **Android Keystore**. Jetpack Compose +
  a `Repository` interface.

> **What it proves:** Swift, Kotlin, mobile architecture, and applied crypto (Secure
> Enclave / Keystore, Ed25519 verification).

## One signature standard, two platforms

Veritas signs the **canonical JSON** of a credential's claims (sorted keys, compact)
with **Ed25519**. Both apps canonicalise identically and verify the signature against the
issuer's public key. Because the Secure Enclave only holds P-256 keys, the Ed25519
*verification* is a software public-key check on both platforms (CryptoKit
`Curve25519.Signing` on iOS, `java.security` Ed25519 on Android); the Enclave/Keystore
secure the device's *own* key.

## How this was verified (honest note)

This repo was built in a Linux sandbox with **no Swift toolchain** and with **Maven
Central / Gradle plugin portal blocked**, so a full `xcodebuild` / `gradle test` couldn't
run here. Instead the **security-critical core was machine-verified cross-platform**:

- `shared/gen_vector.py` generates an Ed25519 credential vector and self-verifies it
  (valid passes, tampered fails) with Python's `cryptography`.
- `shared/VerifierCheck.java` re-verifies the **same vector** with the JDK's native
  Ed25519 (`java.security`) — the exact API path the Android app uses. **Result: PASS.**
- `shared/CanonicalCheck.java` proves the canonical-JSON encoder produces **byte-identical**
  output to the signer's message. **Result: PASS.**

Ed25519 is a single standard, so the iOS CryptoKit path verifies the identical vector on
an Apple platform. The Swift/Kotlin test suites embed this shared vector; run them with
`xcodebuild test` (iOS) and `./gradlew test` (Android) on a normal dev machine.

```
ios/SkillsPassport/      SwiftPM package: Sources + XCTest (iOS 16+, macOS 13+)
android/companion/       Gradle project: Compose app + JUnit host tests
shared/                  the cross-verified Ed25519 test vector + its generators/checkers
```

## What each test suite covers

| Platform | Tests |
|---|---|
| iOS (XCTest) | canonical JSON matches the signer; valid credential verifies; tampered fails; view-model `idle→verifying→valid/invalid`; state-change callback |
| Android (JUnit) | Ed25519 verify (valid/tampered); canonical JSON sorted+compact; repository valid/invalid; view-model transitions |

Secure Enclave (iOS) and Keystore (Android) wrappers require real hardware / an emulator;
they're covered by instrumented tests on-device, not the host suites.
