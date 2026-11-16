package com.derry.companion

import com.derry.companion.crypto.Ed25519Verifier
import com.derry.companion.data.CanonicalJson
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Uses the shared Ed25519 test vector (see /shared/testvector.json). */
class Ed25519VerifierTest {
    private val pub = "A6EHv/POEL4dcN0Y50vAmWfk1jCbpQ1fHdyGZBJVMbg="
    private val sig = "I6SuCMJIQk47Zl+QdqvwpnaQ+EDfjJbEujI0lfLgbAofueZ3OkqFN1d9wzuMrqTRkwuOZIZRiUlcjFDBg5JNAg=="
    private val claims = linkedMapOf<String, Any?>(
        "name" to "Jane Doe", "credential" to "BSc Computer Science",
        "issuer" to "Derry", "year" to 2024,
    )

    @Test fun validSignatureVerifies() {
        val msg = CanonicalJson.encode(claims)
        assertTrue(Ed25519Verifier.verifyBase64(pub, msg, sig))
    }

    @Test fun tamperedClaimFails() {
        val tampered = LinkedHashMap(claims).apply { put("credential", "PhD Nonsense") }
        val msg = CanonicalJson.encode(tampered)
        assertFalse(Ed25519Verifier.verifyBase64(pub, msg, sig))
    }

    @Test fun canonicalJsonIsSortedAndCompact() {
        val out = String(CanonicalJson.encode(claims))
        assertTrue(out.startsWith("{\"credential\":"))   // keys sorted
        assertFalse(out.contains(" "))                    // compact
    }
}
