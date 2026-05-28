package com.example.devfinder.feature.favorites

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.devfinder.core.ui.components.StatusPlaceholder
import com.example.devfinder.core.ui.components.UserCard

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onUserClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold() { paddingValues ->
        Column(modifier = Modifier.fillMaxSize()
            .padding(16.dp)
            .padding(paddingValues)) {
            when (val state = uiState) {
                is FavoritesUiState.Idle -> Unit
                is FavoritesUiState.Empty -> {
                    StatusPlaceholder(
                        text = "Nenhum usuário favoritado ainda...",
                        icon = Icons.Default.SearchOff,
                        iconContextDescription = "Nenhum usuário encontrado"
                    )
                }
                is FavoritesUiState.Error -> {
                    StatusPlaceholder(
                        text = state.message,
                        icon = Icons.Default.ErrorOutline,
                        iconContextDescription = "Erro"
                    )
                }
                is FavoritesUiState.Success -> {
                        LazyColumn {
                            items(items = state.favorites) { favorite ->
                                UserCard(
                                    userLogin = favorite.login,
                                    userAvatarUrl = favorite.avatarUrl,
                                    onUserClick = { onUserClick(favorite.login) }
                                )
                        }
                    }
                }
            }
        }
    }
}