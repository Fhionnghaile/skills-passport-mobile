#if canImport(SwiftUI)
import SwiftUI

/// Minimal SwiftUI surface for the wallet state. (UI shell; logic lives in the
/// view-model and is unit-tested.)
public struct WalletView: View {
    private let viewModel: WalletViewModel
    @State private var state: VerificationState = .idle

    public init(viewModel: WalletViewModel) {
        self.viewModel = viewModel
    }

    public var body: some View {
        VStack(spacing: 16) {
            switch state {
            case .idle:
                Text("Scan a credential").accessibilityIdentifier("idle")
            case .verifying:
                ProgressView("Verifying…").accessibilityIdentifier("verifying")
            case .valid(let claims):
                Label("Valid", systemImage: "checkmark.seal.fill")
                    .accessibilityIdentifier("valid")
                if case .string(let c)? = claims["credential"] { Text(c) }
            case .invalid(let reason):
                Label("Invalid — \(reason)", systemImage: "xmark.seal.fill")
                    .accessibilityIdentifier("invalid")
            }
        }
        .padding()
        .onAppear { viewModel.onStateChange = { state = $0 } }
    }
}
#endif
