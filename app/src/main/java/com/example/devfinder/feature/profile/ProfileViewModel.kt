package com.example.devfinder.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devfinder.core.domain.GithubRepository
import com.example.devfinder.core.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: GithubRepository
): ViewModel() {
    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Idle)
    val uiState = _uiState.asStateFlow()
    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()
    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.LoadUser -> {
                fetchUserProfile(intent.username)
            }
            is ProfileIntent.ToggleFavorite -> {
                toggleFavorite(intent.user)
            }
        }
    }
    fun fetchUserProfile(username: String) {
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
    fun toggleFavorite(user: User) {
        viewModelScope.launch {
            if (_isFavorite.value) {
                repository.removeFavorite(user)
            } else {
                repository.saveFavorite(user)
            }
        }
    }
}