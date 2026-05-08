package com.example.devfinder.feature.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    username: String,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(username) {
        viewModel.onIntent(ProfileIntent.LoadUser(username))
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(76.dp))
        Button(onClick = onBackClick) {
            Text("Voltar")
        }
        when (val state = uiState) {
            is ProfileUiState.Loading -> {
                CircularProgressIndicator()
            }
            is ProfileUiState.Success -> {
                Text(state.user.login)
            }
            is ProfileUiState.Error -> {
                Text("Erro: ${state.message}")
            }
            is ProfileUiState.Idle -> {}
        }
    }
}