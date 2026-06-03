package com.example.devfinder.feature.publicrepos

import com.example.devfinder.core.domain.model.Repo

sealed interface PublicReposUiState {
    object Idle: PublicReposUiState
    object Loading: PublicReposUiState
    data class Empty(
        val message: String
    ): PublicReposUiState
    data class Success(
        val repos: List<Repo>
    ): PublicReposUiState
    data class Error(
        val message: String
    ): PublicReposUiState
}