import Foundation

/// A verifiable credential received from Veritas (decoded from a QR code or deep link).
public struct Credential: Equatable {
    public let id: String
    public let claims: [String: ClaimValue]
    public let signatureBase64: String

    public init(id: String, claims: [String: ClaimValue], signatureBase64: String) {
        self.id = id
        self.claims = claims
        self.signatureBase64 = signatureBase64
    }
}

/// A JSON-ish claim value, limited to the types Veritas issues.
public enum ClaimValue: Equatable {
    case string(String)
    case int(Int)
    case bool(Bool)
}

/// The verification state surfaced to the UI (MVVM).
public enum VerificationState: Equatable {
    case idle
    case verifying
    case valid([String: ClaimValue])
    case invalid(reason: String)
}
