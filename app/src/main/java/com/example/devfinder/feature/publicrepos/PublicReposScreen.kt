package com.example.devfinder.feature.publicrepos

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.devfinder.core.ui.components.StatusPlaceholder

@Composable
fun PublicReposScreen(
    viewModel: PublicReposViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
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
                            text = "Repositórios",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = when (uiState) {
                                is PublicReposUiState.Success ->
                                    "${(uiState as PublicReposUiState.Success).repos.size} projetos públicos"
                                else ->
                                    "Projetos públicos no GitHub"
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            when (val state = uiState) {
                is PublicReposUiState.Idle -> Unit
                is PublicReposUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                            .padding(top = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(60.dp),
                            strokeWidth = 6.dp,
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Carregando repositórios...",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                is PublicReposUiState.Empty -> {
                    StatusPlaceholder(
                        icon = Icons.Default.CloudOff,
                        text = state.message,
                        iconContextDescription = "Sem repositórios públicos"
                    )
                }

                is PublicReposUiState.Error -> {
                    StatusPlaceholder(
                        icon = Icons.Default.Error,
                        text = state.message,
                        iconContextDescription = "Erro"
                    )
                }

                is PublicReposUiState.Success -> {
                    LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
                        items(state.repos) { repo ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp)
                                    .heightIn(min = 110.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {

                                    Text(
                                        text = repo.name,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 25.sp
                                    )

                                    repo.description?.let {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = it,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (repo.language?.isEmpty() == false) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                repo.language.let { language ->
                                                    Box(
                                                        modifier = Modifier
                                                            .size(12.dp)
                                                            .background(
                                                                color = getLanguageColors(language),
                                                                shape = CircleShape
                                                            )
                                                    )

                                                    Spacer(modifier = Modifier.width(6.dp))

                                                    Text(text = language, fontSize = 14.sp)
                                                }
                                            }
                                        }

                                        if (repo.stargazersCount > 0) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = Icons.Default.StarOutline,
                                                        contentDescription = "Stars"
                                                    )

                                                    Spacer(modifier = Modifier.width(4.dp))

                                                    Text(text = repo.stargazersCount.toString())

                                            }
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = "Atualizado ${formatDate(repo.updatedAt)}", fontSize = 14.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}