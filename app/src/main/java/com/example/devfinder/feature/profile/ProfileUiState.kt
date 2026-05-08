package com.example.devfinder.feature.profile

import com.example.devfinder.core.data.model.UserResponse

sealed interface ProfileUiState {
    object Idle: ProfileUiState
    object Loading: ProfileUiState
    data class Success(
        val user: UserResponse
    ) : ProfileUiState
    data class Error(
        val message: String
    ) : ProfileUiState
}