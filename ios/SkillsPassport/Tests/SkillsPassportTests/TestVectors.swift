import Foundation
@testable import SkillsPassport

/// The shared Ed25519 test vector (see /shared/testvector.json), cross-verified
/// in Python and the JDK. CryptoKit uses the same Ed25519 standard.
enum TestVectors {
    static let issuerPublicKeyBase64 = "A6EHv/POEL4dcN0Y50vAmWfk1jCbpQ1fHdyGZBJVMbg="
    static let signatureBase64 = "I6SuCMJIQk47Zl+QdqvwpnaQ+EDfjJbEujI0lfLgbAofueZ3OkqFN1d9wzuMrqTRkwuOZIZRiUlcjFDBg5JNAg=="

    static let claims: [String: ClaimValue] = [
        "name": .string("Jane Doe"),
        "credential": .string("BSc Computer Science"),
        "issuer": .string("Derry"),
        "year": .int(2024),
    ]
}
