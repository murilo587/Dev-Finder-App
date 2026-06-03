package com.example.devfinder.feature.favorites

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.devfinder.core.ui.components.StatusPlaceholder
import com.example.devfinder.core.ui.components.UserCard

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onUserClick: (String, Long) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { paddingValues ->
        Column(modifier = Modifier.fillMaxSize()
            .padding(16.dp)
            .padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    FilledTonalIconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {

                        Text(
                            text = "Favoritos",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Perfis salvos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
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
                                    userId = favorite.id,
                                    userLogin = favorite.login,
                                    userAvatarUrl = favorite.avatarUrl,
                                    onUserClick = onUserClick
                                )
                        }
                    }
                }
            }
        }
    }
}