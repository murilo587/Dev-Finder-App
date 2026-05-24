package com.example.devfinder.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.devfinder.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onUserClick: (String) -> Unit
) {
    val query by viewModel.query.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text(text = "Pesquisar") }, )}) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.handleIntent(SearchIntent.OnQueryChanged(it)) },
                label = { Text("Pesquisar usuário") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Pesquisar")
                },
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = {
                            viewModel.handleIntent(SearchIntent.OnQueryChanged(""))
                        }) {
                            Icon(
                                imageVector = Icons.Default.Cancel,
                                contentDescription = "Limpar pesquisa"
                            )
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize()) {
                when (val state = uiState) {
                    is SearchUiState.Idle -> {
                        SearchFeedback(
                            text = "Digite para Buscar...",
                            icon = Icons.Default.PersonSearch,
                            iconContextDescription = "Pesquisar Usuário"
                        )
                    }
                    is SearchUiState.Loading -> {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .padding(top = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(60.dp),
                                strokeWidth = 6.dp,
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Buscando desenvolvedores...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    is SearchUiState.Success -> {
                        LazyColumn {
                            items(state.users.items) { user ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 5.dp)
                                        .clickable { onUserClick(user.login) }
                                ) {
                                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                                        AsyncImage(model = user.avatarUrl, contentDescription = "UserImage",
                                            contentScale = ContentScale.Fit,
                                            placeholder = painterResource(R.drawable.user_icon),
                                            modifier = Modifier
                                                .size(55.dp)
                                                .clip(CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(16.dp))
                                        Text(text = user.login, fontSize = 20.sp, fontWeight = FontWeight.Medium)
                                    }

                                }
                            }
                        }
                        if (state.users.totalCount == 0) {
                            SearchFeedback(
                                text = "Nenhum usuário encontrado",
                                icon = Icons.Default.SearchOff,
                                iconContextDescription = "Pesquisa não encontrada"
                            )
                        }
                    }
                    is SearchUiState.Error -> {
                        Text(text = state.message,
                            color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchFeedback(text: String, icon: ImageVector, iconContextDescription: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = iconContextDescription,
            modifier = Modifier.size(100.dp),
        )
        Text(text = text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}