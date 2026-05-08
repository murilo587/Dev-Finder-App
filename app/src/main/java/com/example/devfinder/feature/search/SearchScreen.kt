package com.example.devfinder.feature.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onUserClick: (String) -> Unit
) {
    val query by viewModel.query.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(76.dp))
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.onIntent(SearchIntent.OnQueryChanged(it))},
            label = { Text("Pesquisar usuário") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {
                is SearchUiState.Idle -> {
                    Text("Digite para Buscar...")
                }
                is SearchUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is SearchUiState.Success -> {
                    LazyColumn {
                        items(state.users.items) { user ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable{ onUserClick(user.login) }
                            ) {
                                Text(user.login)
                            }
                        }
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