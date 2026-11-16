package com.derry.companion.data

import com.derry.companion.crypto.Ed25519Verifier
import com.derry.companion.model.Credential
import com.derry.companion.model.VerificationState

/** Verifies credentials. Injected into the ViewModel so tests use a fake/real impl. */
interface CredentialRepository {
    fun verify(credential: Credential): VerificationState
}

/**
 * Real repository: canonicalises the claims (sorted keys, compact JSON matching
 * Veritas's signer) and checks the Ed25519 signature against the issuer key.
 */
class VeritasCredentialRepository(
    private val issuerPublicKeyB64: String,
) : CredentialRepository {

    override fun verify(credential: Credential): VerificationState {
        val message = CanonicalJson.encode(credential.claims)
        val ok = Ed25519Verifier.verifyBase64(
            issuerPublicKeyB64, message, credential.signatureBase64,
        )
        return if (ok) VerificationState.Valid(credential.claims)
        else VerificationState.Invalid("signature mismatch")
    }
}

/**
 * Deterministic, dependency-free canonical JSON: keys sorted, no whitespace —
 * byte-identical to Python's `json.dumps(sort_keys=True, separators=(",",":"))`,
 * which is how Veritas signs. (Verified byte-for-byte against the shared vector.)
 */
object CanonicalJson {
    fun encode(value: Any?): ByteArray = build(value, StringBuilder()).toString()
        .toByteArray(Charsets.UTF_8)

    private fun build(value: Any?, sb: StringBuilder): StringBuilder {
        when (value) {
            null -> sb.append("null")
            is String -> encodeString(value, sb)
            is Boolean -> sb.append(if (value) "true" else "false")
            is Int, is Long -> sb.append(value.toString())
            is Double -> sb.append(if (value % 1.0 == 0.0) value.toLong().toString()
                                   else value.toString())
            is Map<*, *> -> {
                sb.append('{')
                val keys = value.keys.map { it.toString() }.sorted()
                keys.forEachIndexed { i, k ->
                    if (i > 0) sb.append(',')
                    encodeString(k, sb); sb.append(':')
                    build(value[k], sb)
                }
                sb.append('}')
            }
            is List<*> -> {
                sb.append('[')
                value.forEachIndexed { i, v -> if (i > 0) sb.append(','); build(v, sb) }
                sb.append(']')
            }
            else -> encodeString(value.toString(), sb)
        }
        return sb
    }

    private fun encodeString(s: String, sb: StringBuilder) {
        sb.append('"')
        for (c in s) when (c) {
            '"' -> sb.append("\\\"")
            '\\' -> sb.append("\\\\")
            '\n' -> sb.append("\\n")
            '\r' -> sb.append("\\r")
            '\t' -> sb.append("\\t")
            else -> sb.append(c)
        }
        sb.append('"')
    }
}
