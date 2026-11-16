import Foundation
#if canImport(Combine)
import Combine
#endif

/// MVVM view-model: scan → verifying → valid/invalid. Depends on the
/// VerificationService protocol, so tests inject a deterministic mock.
public final class WalletViewModel {
    public private(set) var state: VerificationState = .idle {
        didSet { onStateChange?(state) }
    }
    public var onStateChange: ((VerificationState) -> Void)?

    private let service: VerificationService

    public init(service: VerificationService) {
        self.service = service
    }

    /// Called when a credential has been scanned (QR / deep link already decoded).
    public func didScan(_ credential: Credential) {
        state = .verifying
        state = service.verify(credential)
    }

    public func reset() { state = .idle }
}
