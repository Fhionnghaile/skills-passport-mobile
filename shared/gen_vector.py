"""Generate and self-verify an Ed25519 credential test vector.

Veritas signs the canonical JSON of a credential's claims with Ed25519. This
produces a shared fixture used by BOTH the iOS (CryptoKit) and Android
(java.security) test suites, and verifies it here so the vector is known-good.
"""
import base64
import json
from cryptography.hazmat.primitives.asymmetric.ed25519 import (
    Ed25519PrivateKey, Ed25519PublicKey,
)
from cryptography.hazmat.primitives import serialization
from cryptography.exceptions import InvalidSignature

SEED = bytes(range(32))  # deterministic private key seed


def canonical(claims: dict) -> bytes:
    return json.dumps(claims, sort_keys=True, separators=(",", ":")).encode()


def main() -> None:
    priv = Ed25519PrivateKey.from_private_bytes(SEED)
    pub = priv.public_key()
    pub_raw = pub.public_bytes(serialization.Encoding.Raw,
                               serialization.PublicFormat.Raw)

    claims = {"name": "Jane Doe", "credential": "BSc Computer Science",
              "issuer": "Derry", "year": 2024}
    msg = canonical(claims)
    sig = priv.sign(msg)

    # self-check: valid verifies, tampered fails
    pub.verify(sig, msg)
    tampered = dict(claims, credential="PhD Nonsense")
    try:
        pub.verify(sig, canonical(tampered))
        raise SystemExit("ERROR: tampered claim verified!")
    except InvalidSignature:
        pass

    vector = {
        "algorithm": "Ed25519",
        "publicKeyBase64": base64.b64encode(pub_raw).decode(),
        "claims": claims,
        "canonicalMessageBase64": base64.b64encode(msg).decode(),
        "signatureBase64": base64.b64encode(sig).decode(),
        "tamperedClaims": tampered,
    }
    with open("testvector.json", "w") as f:
        json.dump(vector, f, indent=2)
    print("OK: vector generated and self-verified (valid passes, tampered fails)")
    print("pubkey:", vector["publicKeyBase64"])
    print("sig:", vector["signatureBase64"][:32], "...")


if __name__ == "__main__":
    main()
