import Foundation
#if canImport(CryptoKit)
import CryptoKit
#endif

/// The verification port — mockable, so the ViewModel is tested without crypto.
public protocol VerificationService {
    func verify(_ credential: Credential) -> VerificationState
}

/// Verifies Veritas's Ed25519 signatures locally with CryptoKit.
///
/// The Secure Enclave only holds P-256 keys, so it cannot hold the Ed25519 issuer
/// key; verifying Veritas's signature is a software public-key check
/// (`Curve25519.Signing`). The Enclave is used separately for the device's own
/// key (see EnclaveKeyStore).
public struct VeritasVerificationService: VerificationService {
    private let issuerPublicKeyBase64: String

    public init(issuerPublicKeyBase64: String) {
        self.issuerPublicKeyBase64 = issuerPublicKeyBase64
    }

    public func verify(_ credential: Credential) -> VerificationState {
        #if canImport(CryptoKit)
        guard
            let keyData = Data(base64Encoded: issuerPublicKeyBase64),
            let sigData = Data(base64Encoded: credential.signatureBase64),
            let publicKey = try? Curve25519.Signing.PublicKey(rawRepresentation: keyData)
        else {
            return .invalid(reason: "malformed key or signature")
        }
        let message = CanonicalJSON.encode(credential.claims)
        if publicKey.isValidSignature(sigData, for: message) {
            return .valid(credential.claims)
        }
        return .invalid(reason: "signature mismatch")
        #else
        return .invalid(reason: "CryptoKit unavailable on this platform")
        #endif
    }
}
