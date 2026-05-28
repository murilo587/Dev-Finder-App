package com.example.devfinder.feature.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devfinder.core.domain.GithubRepository
import com.example.devfinder.core.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: GithubRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val username: String = checkNotNull(savedStateHandle["username"])
    private val isSaved: Boolean = savedStateHandle.get<Any>("isSaved")?.toString()?.toBoolean() ?: false
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
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ProfileUiState.Loading
            if (isSaved) {
                val localUser = repository.getFavoriteUserByName(username)
                _uiState.value = ProfileUiState.Success(localUser)
                observeFavorite(localUser.id)
            } else {
                fetchUserProfileFromApi(username)
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
                repository.removeFavorite(user)
            } else {
                repository.saveFavorite(user)
            }
        }
    }
}