package com.example.devfinder.feature.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devfinder.core.di.IoDispatcher
import com.example.devfinder.core.domain.GithubRepository
import com.example.devfinder.core.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: GithubRepository,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val username: String = checkNotNull(savedStateHandle["username"])
    private val userId: Long = savedStateHandle.get<Any>("userId")?.toString()?.toLongOrNull() ?: error("userId não encontrado")
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState = _uiState.asStateFlow()
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    init {
        loadUserProfile()
    }
    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.LoadUser -> {
                fetchUserProfileFromApi(intent.username)
            }
            is ProfileIntent.ToggleFavorite -> {
                toggleFavorite(intent.user)
            }
        }
    }
    private fun loadUserProfile() {
        viewModelScope.launch(ioDispatcher) {
            _uiState.value = ProfileUiState.Loading
            val isSaved = repository.checkIsFavoriteDirect(userId)
            val localUser = repository.getFavoriteUserByName(username)
            if (isSaved && localUser != null) {
                _uiState.value = ProfileUiState.Success(localUser)
                observeFavorite(localUser.id)
            } else {
                fetchUserProfileFromApi(username)
            }
        }
    }
    private fun saveUserData() {
        viewModelScope.launch {
            val repos = repository.getRepos(username)
            val starredRepos = repository.getStarredRepos(username)
            repos.getOrNull()?.let {
                repository.saveRepositories(it, userId)
            }
            starredRepos.getOrNull()?.let {
                repository.saveStarredRepositories(it, userId)
            }
        }
    }

    private fun removeUserData() {
        viewModelScope.launch {
            val isSaved = repository.checkIsFavoriteDirect(userId)
            if (isSaved) {
                repository.removeRepositories(userId)
                repository.removeStarredRepositories(userId)
            }
        }
    }
    private fun fetchUserProfileFromApi(username: String) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            repository.getUser(username)
                .onSuccess { user ->
                    _uiState.value = ProfileUiState.Success(user)
                    observeFavorite(user.id)
                }
                .onFailure { error ->
                    _uiState.value = ProfileUiState.Error(error.message ?: "Unknown Error")
                }
        }
    }
    private fun observeFavorite(userId: Long) {
        viewModelScope.launch {
            repository.isFavorite(userId)
                .collect { favorite ->
                    _isFavorite.value = favorite
                }
        }
    }
    private fun toggleFavorite(user: User) {
        viewModelScope.launch {
            if (_isFavorite.value) {
                removeUserData()
                repository.removeFavorite(user)
            } else {
                repository.saveFavorite(user)
                saveUserData()
            }
        }
    }
}