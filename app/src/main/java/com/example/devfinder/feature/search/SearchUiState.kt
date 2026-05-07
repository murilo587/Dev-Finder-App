package com.example.devfinder.feature.search

import com.example.devfinder.core.data.model.UserListResponse

sealed interface SearchUiState {
    object Idle: SearchUiState
    object Loading: SearchUiState
    data class Success(
        val users: UserListResponse
    ) : SearchUiState
    data class Error(
        val message: String
    ) : SearchUiState
}