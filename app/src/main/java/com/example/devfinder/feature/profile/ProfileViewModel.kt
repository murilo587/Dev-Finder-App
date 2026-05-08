package com.example.devfinder.feature.profile

import androidx.compose.material3.PrimaryTabRow
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.devfinder.core.data.model.UserResponse
import com.example.devfinder.core.domain.GithubRepository
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
    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.LoadUser -> {
                fetchUserProfile(intent.username)
            }
        }
    }
    fun fetchUserProfile(username: String) {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            repository.getUser(username)
                .onSuccess { user ->
                    _uiState.value = ProfileUiState.Success(user)
                }
                .onFailure { error ->
                    _uiState.value = ProfileUiState.Error(error.message ?: "Unknown Error")
                }
        }
    }
}