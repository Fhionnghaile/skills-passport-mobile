import XCTest
@testable import SkillsPassport

/// Deterministic mock service so the state machine is tested without crypto.
private struct MockService: VerificationService {
    let result: VerificationState
    func verify(_ credential: Credential) -> VerificationState { result }
}

final class ViewModelTests: XCTestCase {
    private func credential() -> Credential {
        Credential(id: "c1", claims: [:], signatureBase64: "")
    }

    func testScanValidTransitionsToValid() {
        let vm = WalletViewModel(service: MockService(result: .valid([:])))
        vm.didScan(credential())
        guard case .valid = vm.state else { return XCTFail() }
    }

    func testScanInvalidTransitionsToInvalid() {
        let vm = WalletViewModel(service: MockService(result: .invalid(reason: "bad")))
        vm.didScan(credential())
        guard case .invalid = vm.state else { return XCTFail() }
    }

    func testResetReturnsToIdle() {
        let vm = WalletViewModel(service: MockService(result: .valid([:])))
        vm.didScan(credential())
        vm.reset()
        guard case .idle = vm.state else { return XCTFail() }
    }

    func testStateChangeCallbackFires() {
        let vm = WalletViewModel(service: MockService(result: .valid([:])))
        var seen: [VerificationState] = []
        vm.onStateChange = { seen.append($0) }
        vm.didScan(credential())
        XCTAssertEqual(seen.count, 2)   // verifying, then valid
    }
}
