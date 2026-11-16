package com.derry.companion.crypto

import java.security.KeyFactory
import java.security.PublicKey
import java.security.Signature
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

/**
 * Ed25519 signature verification using the platform provider (java.security).
 *
 * Verified cross-platform against the shared test vector (see /shared) with both
 * Python and the JDK. On Android, Ed25519 is available via the AndroidOpenSSL /
 * Conscrypt provider on API 33+; on older devices, bundle Conscrypt or Tink.
 */
object Ed25519Verifier {
    // SubjectPublicKeyInfo (X.509) prefix for a raw 32-byte Ed25519 public key.
    private val SPKI_PREFIX = byteArrayOf(
        0x30, 0x2a, 0x30, 0x05, 0x06, 0x03, 0x2b, 0x65, 0x70, 0x03, 0x21, 0x00
    )

    fun publicKeyFromRaw(raw: ByteArray): PublicKey {
        require(raw.size == 32) { "Ed25519 public key must be 32 bytes" }
        val der = SPKI_PREFIX + raw
        return KeyFactory.getInstance("Ed25519")
            .generatePublic(X509EncodedKeySpec(der))
    }

    fun verify(publicKeyRaw: ByteArray, message: ByteArray, signature: ByteArray): Boolean {
        return try {
            val sig = Signature.getInstance("Ed25519")
            sig.initVerify(publicKeyFromRaw(publicKeyRaw))
            sig.update(message)
            sig.verify(signature)
        } catch (e: Exception) {
            false
        }
    }

    fun verifyBase64(publicKeyB64: String, message: ByteArray, signatureB64: String): Boolean {
        val d = Base64.getDecoder()
        return verify(d.decode(publicKeyB64), message, d.decode(signatureB64))
    }
}
