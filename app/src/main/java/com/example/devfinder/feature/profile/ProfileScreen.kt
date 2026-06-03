package com.example.devfinder.feature.profile

import android.graphics.drawable.Icon
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.devfinder.R
import com.example.devfinder.core.ui.components.UserInfoCard
import com.example.devfinder.feature.publicrepos.PublicReposUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit,
    navigateToPublicRepos: (String, Long) -> Unit,
    navigateToStarredRepos: (String, Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues)
        ) {
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
                            text = "Perfil",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                    }
                }
            }
            when (val state = uiState) {
                is ProfileUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(60.dp),
                            strokeWidth = 6.dp,
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant                            )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Carregando perfil...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                is ProfileUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize(),horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = {
                                viewModel.handleIntent(ProfileIntent.ToggleFavorite(state.user))
                            },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "Favoritar",
                                tint = if (isFavorite) Color(0xFFFFD700) else Color.LightGray,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        AsyncImage(
                            model = state.user.avatarUrl,
                            contentDescription = "user image",
                            placeholder = painterResource(R.drawable.user_icon),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(250.dp)
                                .clip(shape = CircleShape)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.height(26.dp))
                        Text(
                            text = "@${state.user.login}",
                            fontSize = 26.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        val bio = state.user.bio

                        if (!bio.isNullOrBlank()) {
                            val displayBio =
                                if (bio.length > 30) bio.take(30) + "..." else bio
                            if (state.user.bio.isNotBlank()) {
                                Text(
                                    text = displayBio,
                                    fontSize = 16.sp,
                                    fontStyle = FontStyle.Italic,
                                    color = Color.Gray,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(40.dp))
                        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                UserInfoCard(
                                    modifier = Modifier.weight(1f),
                                    userId = state.user.id,
                                    username = state.user.login,
                                    text = "Repos Públicos",
                                    icon = Icons.Default.Public,
                                    onUserClick = navigateToPublicRepos
                                )
                                UserInfoCard(
                                    modifier = Modifier.weight(1f),
                                    userId = state.user.id,
                                    username = state.user.login,
                                    text = "Repos Favoritos",
                                    icon = Icons.Default.Star,
                                    onUserClick = navigateToStarredRepos
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                UserInfoCard(
                                    modifier = Modifier.weight(1f),
                                    userId = state.user.id,
                                    username = state.user.login,
                                    text = "Seguidores",
                                    icon = Icons.Default.Person,
                                    onUserClick = navigateToPublicRepos
                                )
                                UserInfoCard(
                                    modifier = Modifier.weight(1f),
                                    userId = state.user.id,
                                    username = state.user.login,
                                    text = "Seguindo",
                                    icon = Icons.Default.PersonSearch,
                                    onUserClick = navigateToStarredRepos
                                )
                            }
                        }
                    }

                }
                is ProfileUiState.Error -> {
                    Text("Erro: ${state.message}")
                }
                is ProfileUiState.Idle -> {}
            }
        }

    }
}