package com.derry.companion

import com.derry.companion.data.VeritasCredentialRepository
import com.derry.companion.model.Credential
import com.derry.companion.model.VerificationState
import org.junit.Assert.assertTrue
import org.junit.Test

class CredentialRepositoryTest {
    private val repo = VeritasCredentialRepository("A6EHv/POEL4dcN0Y50vAmWfk1jCbpQ1fHdyGZBJVMbg=")
    private val claims = linkedMapOf<String, Any?>(
        "name" to "Jane Doe", "credential" to "BSc Computer Science",
        "issuer" to "Derry", "year" to 2024,
    )

    @Test fun validCredentialIsValid() {
        val state = repo.verify(Credential("c1", claims, "I6SuCMJIQk47Zl+QdqvwpnaQ+EDfjJbEujI0lfLgbAofueZ3OkqFN1d9wzuMrqTRkwuOZIZRiUlcjFDBg5JNAg=="))
        assertTrue(state is VerificationState.Valid)
    }

    @Test fun tamperedCredentialIsInvalid() {
        val tampered = LinkedHashMap(claims).apply { put("credential", "PhD Nonsense") }
        val state = repo.verify(Credential("c1", tampered, "I6SuCMJIQk47Zl+QdqvwpnaQ+EDfjJbEujI0lfLgbAofueZ3OkqFN1d9wzuMrqTRkwuOZIZRiUlcjFDBg5JNAg=="))
        assertTrue(state is VerificationState.Invalid)
    }
}
