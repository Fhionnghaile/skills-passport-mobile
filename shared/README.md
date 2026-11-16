# Shared Ed25519 test vector + cross-checks

`testvector.json` — an Ed25519-signed credential used by both the iOS and Android test
suites, so both verify the **same** known-good data.

- `gen_vector.py` — generates it and self-verifies (Python `cryptography`).
- `VerifierCheck.java` — re-verifies with the JDK's native Ed25519 (`java.security`), the
  Android path. `java VerifierCheck <pub> <msg> <sig> <tamperedMsg>` → `PASS`.
- `CanonicalCheck.java` — proves the canonical-JSON encoder is byte-identical to the
  signer. → `PASS`.

Regenerate: `python3 gen_vector.py`.
