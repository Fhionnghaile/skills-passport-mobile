import XCTest
@testable import SkillsPassport

final class VerificationTests: XCTestCase {
    private let service = VeritasVerificationService(
        issuerPublicKeyBase64: TestVectors.issuerPublicKeyBase64)

    func testCanonicalJSONMatchesSigner() {
        let data = CanonicalJSON.encode(TestVectors.claims)
        let str = String(decoding: data, as: UTF8.self)
        XCTAssertTrue(str.hasPrefix("{\"credential\":"))   // keys sorted
        XCTAssertFalse(str.contains(" "))                   // compact
    }

    func testValidCredentialVerifies() {
        let cred = Credential(id: "c1", claims: TestVectors.claims,
                              signatureBase64: TestVectors.signatureBase64)
        guard case .valid = service.verify(cred) else {
            return XCTFail("expected valid")
        }
    }

    func testTamperedCredentialFails() {
        var tampered = TestVectors.claims
        tampered["credential"] = .string("PhD Nonsense")
        let cred = Credential(id: "c1", claims: tampered,
                              signatureBase64: TestVectors.signatureBase64)
        guard case .invalid = service.verify(cred) else {
            return XCTFail("expected invalid for tampered claim")
        }
    }
}
