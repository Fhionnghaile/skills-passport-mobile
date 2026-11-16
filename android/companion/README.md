# Derry Companion (Android)

Jetpack Compose viewer. Verifies Veritas Ed25519 signatures via `java.security`;
device secret in the Android Keystore. A `CredentialRepository` interface keeps the
view-model testable.

```bash
./gradlew test                 # host JUnit: crypto, canonical JSON, repository, view-model
./gradlew connectedAndroidTest # instrumented: Keystore + Compose (needs a device/emulator)
```

- The Ed25519 verification path (`Ed25519Verifier` + `CanonicalJson`) is the one
  cross-verified in the JDK against the shared vector (see ../../shared).
- `KeystoreWrapper` requires an Android runtime (instrumented test).
