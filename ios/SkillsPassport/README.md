# Skills Passport (iOS)

SwiftUI + MVVM credential wallet. Verifies Veritas Ed25519 signatures with CryptoKit
(`Curve25519.Signing`); device key in the Secure Enclave (P-256, biometric-gated).

```bash
# on macOS with Xcode:
swift test                     # or: xcodebuild test -scheme SkillsPassport
```

- `VerificationService` is a protocol → the view-model is tested with a mock, no crypto.
- The Ed25519 test vector is the shared, cross-verified one (see ../../shared).
- `EnclaveKeyStore` needs a device/Simulator; it's not exercised by `swift test`.
