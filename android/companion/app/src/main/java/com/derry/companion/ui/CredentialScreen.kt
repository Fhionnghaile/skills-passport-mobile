package com.derry.companion.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.derry.companion.model.VerificationState

@Composable
fun CredentialScreen(viewModel: CompanionViewModel) {
    val state by viewModel.state.collectAsState()
    Column(Modifier.padding(16.dp)) {
        when (val s = state) {
            is VerificationState.Idle ->
                Text("Scan a credential", Modifier.semantics { contentDescription = "idle" })
            is VerificationState.Verifying ->
                Text("Verifying…", Modifier.semantics { contentDescription = "verifying" })
            is VerificationState.Valid ->
                Text("✔ Valid — ${s.claims["credential"]}",
                    Modifier.semantics { contentDescription = "valid" })
            is VerificationState.Invalid ->
                Text("✘ Invalid — ${s.reason}",
                    Modifier.semantics { contentDescription = "invalid" })
        }
    }
}
