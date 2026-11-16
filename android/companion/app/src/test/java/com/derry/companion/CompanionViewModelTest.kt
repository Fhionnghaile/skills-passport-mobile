package com.derry.companion

import com.derry.companion.data.CredentialRepository
import com.derry.companion.model.Credential
import com.derry.companion.model.VerificationState
import com.derry.companion.ui.CompanionViewModel
import org.junit.Assert.assertTrue
import org.junit.Test

class CompanionViewModelTest {
    private fun vmWith(result: VerificationState): CompanionViewModel {
        val repo = object : CredentialRepository {
            override fun verify(credential: Credential) = result
        }
        return CompanionViewModel(repo)
    }

    @Test fun scanValidTransitionsToValid() {
        val vm = vmWith(VerificationState.Valid(mapOf("credential" to "BSc")))
        vm.onCredentialScanned(Credential("c1", emptyMap(), ""))
        assertTrue(vm.state.value is VerificationState.Valid)
    }

    @Test fun scanInvalidTransitionsToInvalid() {
        val vm = vmWith(VerificationState.Invalid("bad"))
        vm.onCredentialScanned(Credential("c1", emptyMap(), ""))
        assertTrue(vm.state.value is VerificationState.Invalid)
    }

    @Test fun resetReturnsToIdle() {
        val vm = vmWith(VerificationState.Valid(emptyMap()))
        vm.onCredentialScanned(Credential("c1", emptyMap(), ""))
        vm.reset()
        assertTrue(vm.state.value is VerificationState.Idle)
    }
}
