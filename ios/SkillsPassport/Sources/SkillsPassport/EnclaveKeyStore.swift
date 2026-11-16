import Foundation
#if canImport(CryptoKit)
import CryptoKit
#endif
#if canImport(LocalAuthentication)
import LocalAuthentication
#endif

/// Wraps the Secure Enclave for the device's own P-256 key, used to sign the
/// wallet's presentation requests and gated behind biometrics.
///
/// The Enclave holds only P-256 keys — it cannot store the Ed25519 issuer key, so
/// credential verification is done in software (see VeritasVerificationService).
public final class EnclaveKeyStore {
    public enum KeyError: Error { case unavailable, notFound, generationFailed }

    private let tag: String

    public init(tag: String = "com.derry.skillspassport.devicekey") {
        self.tag = tag
    }

    #if canImport(CryptoKit)
    /// Create (or fetch) a Secure-Enclave P-256 signing key, biometric-gated.
    public func getOrCreateKey() throws -> SecureEnclave.P256.Signing.PrivateKey {
        guard SecureEnclave.isAvailable else { throw KeyError.unavailable }
        if let data = try loadKeyData() {
            return try SecureEnclave.P256.Signing.PrivateKey(dataRepresentation: data)
        }
        let access = SecAccessControlCreateWithFlags(
            nil, kSecAttrAccessibleWhenUnlockedThisDeviceOnly,
            [.privateKeyUsage, .biometryCurrentSet], nil
        )
        let key = try SecureEnclave.P256.Signing.PrivateKey(accessControl: access!)
        try saveKeyData(key.dataRepresentation)
        return key
    }

    public func hasKey() -> Bool { (try? loadKeyData()) != nil }

    private func loadKeyData() throws -> Data? {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: tag,
            kSecReturnData as String: true,
        ]
        var item: CFTypeRef?
        let status = SecItemCopyMatching(query as CFDictionary, &item)
        if status == errSecSuccess { return item as? Data }
        if status == errSecItemNotFound { return nil }
        throw KeyError.notFound
    }

    private func saveKeyData(_ data: Data) throws {
        let attrs: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrAccount as String: tag,
            kSecValueData as String: data,
        ]
        SecItemDelete(attrs as CFDictionary)
        guard SecItemAdd(attrs as CFDictionary, nil) == errSecSuccess else {
            throw KeyError.generationFailed
        }
    }
    #endif
}
