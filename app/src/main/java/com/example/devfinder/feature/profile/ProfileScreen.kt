package com.example.devfinder.feature.profile

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.devfinder.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    username: String,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    LaunchedEffect(username) {
        viewModel.handleIntent(ProfileIntent.LoadUser(username))
    }
    Scaffold() { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(16.dp)
                .padding(paddingValues)
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
                            modifier = Modifier.size(230.dp)
                                .clip(shape = CircleShape)
                                .border(
                                    width = 2.dp,
                                    color = Color.Gray,
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.height(26.dp))
                        Text(text = "@${state.user.login}", fontSize = 26.sp)
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