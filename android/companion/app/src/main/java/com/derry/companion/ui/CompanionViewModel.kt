package com.derry.companion.ui

import androidx.lifecycle.ViewModel
import com.derry.companion.data.CredentialRepository
import com.derry.companion.model.Credential
import com.derry.companion.model.VerificationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Drives the scan → verify → result flow. UI observes [state]. */
class CompanionViewModel(
    private val repository: CredentialRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<VerificationState>(VerificationState.Idle)
    val state: StateFlow<VerificationState> = _state.asStateFlow()

    /** Called when a credential is scanned (QR / deep link already decoded). */
    fun onCredentialScanned(credential: Credential) {
        _state.value = VerificationState.Verifying
        _state.value = repository.verify(credential)
    }

    fun reset() {
        _state.value = VerificationState.Idle
    }
}
