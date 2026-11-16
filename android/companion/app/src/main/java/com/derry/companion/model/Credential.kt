package com.derry.companion.model

/** A verifiable credential as received from Veritas (decoded from QR / deep link). */
data class Credential(
    val id: String,
    val claims: Map<String, Any?>,
    val signatureBase64: String,
)

/** The verification state machine surfaced to the UI. */
sealed interface VerificationState {
    data object Idle : VerificationState
    data object Verifying : VerificationState
    data class Valid(val claims: Map<String, Any?>) : VerificationState
    data class Invalid(val reason: String) : VerificationState
}
