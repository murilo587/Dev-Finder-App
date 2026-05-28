package com.example.devfinder.feature.favorites

import com.example.devfinder.core.domain.model.User

sealed interface FavoritesUiState {
    object Idle: FavoritesUiState
    object Empty: FavoritesUiState
    data class Success(
        val favorites: List<User>
    ) : FavoritesUiState
    data class Error(
        val message: String
    ) : FavoritesUiState
}