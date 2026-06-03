package com.example.devfinder.feature.publicrepos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devfinder.core.domain.GithubRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublicReposViewModel @Inject constructor(
    private val repository: GithubRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val username: String = checkNotNull(savedStateHandle["username"])
    private val userId: Long = savedStateHandle.get<Any>("userId")?.toString()?.toLongOrNull()
        ?: error("userId não encontrado")
    private val isStarred: Boolean = savedStateHandle.get<Any>("isStarred").toString().toBoolean()
    private val _uiState = MutableStateFlow<PublicReposUiState>(PublicReposUiState.Idle)
    val uiState = _uiState.asStateFlow()

    init {
        if (isStarred) {
            observeStarredRepos()
        } else {
            observeRepos()
        }

        syncRepos()
    }

    private fun observeRepos() {
        viewModelScope.launch {
            _uiState.value = PublicReposUiState.Loading
            val isSaved = repository.checkIsFavoriteDirect(userId)
            if (isSaved) {
                repository.getLocalRepositories(userId)
                    .collect { repos ->
                        _uiState.value =
                            if (repos.isEmpty()) {
                                PublicReposUiState.Empty(
                                    "O usuário não tem repositórios públicos"
                                )
                            } else {
                                PublicReposUiState.Success(repos)
                            }
                    }
            } else {
                repository.getRepos(username).onSuccess { repos ->
                    if (repos.isEmpty()) {
                        _uiState.value = PublicReposUiState.Empty("O usuário não tem repositórios públicos")
                    } else {
                        _uiState.value = PublicReposUiState.Success(repos)
                    }
                }.onFailure { error ->
                    _uiState.value = PublicReposUiState.Error(error.message ?: "Unknown Error")
                }
            }
        }
    }

    private fun observeStarredRepos() {
        viewModelScope.launch {
            _uiState.value = PublicReposUiState.Loading
            val isSaved = repository.checkIsFavoriteDirect(userId)
            if (isSaved) {
                repository.getLocalStarredRepositories(userId)
                    .collect { repos ->
                        _uiState.value =
                            if (repos.isEmpty()) {
                                PublicReposUiState.Empty(
                                    "O usuário não tem repositórios favoritados"
                                )
                            } else {
                                PublicReposUiState.Success(repos)
                            }
                    }
            } else {
                repository.getStarredRepos(username).onSuccess { starredRepos ->
                    if (starredRepos.isEmpty()) {
                        _uiState.value = PublicReposUiState.Empty("O usuário não tem repositórios favoritados")
                    } else {
                        _uiState.value = PublicReposUiState.Success(starredRepos)
                    }
                }.onFailure { error ->
                    _uiState.value = PublicReposUiState.Error(error.message ?: "Unknown Error")
                }
            }
        }
    }

    private fun syncRepos() {
        viewModelScope.launch(Dispatchers.IO) {
            val isSaved = repository.checkIsFavoriteDirect(userId)
            if (isSaved) {
                val publicResult = repository.getRepos(username)
                val starredResult = repository.getStarredRepos(username)

                publicResult.getOrNull()?.let { public ->
                    repository.updateRepositories(public, userId)
                }

                starredResult.getOrNull()?.let { starred ->
                    repository.updateStarredRepositories(starred, userId)
                }
            }
        }
    }
}
