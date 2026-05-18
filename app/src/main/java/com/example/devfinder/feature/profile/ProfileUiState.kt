package com.example.devfinder.feature.profile

import com.example.devfinder.core.data.model.UserResponse
import com.example.devfinder.core.domain.model.User

sealed interface ProfileUiState {
    object Idle: ProfileUiState
    object Loading: ProfileUiState
    data class Success(
        val user: User
    ) : ProfileUiState
    data class Error(
        val message: String
    ) : ProfileUiState
}