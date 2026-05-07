package com.example.devfinder.feature.search

sealed interface SearchIntent {
    data class OnQueryChanged(
        val query: String
    ) : SearchIntent
    data class OnUserClicked(
        val login: String
    ) : SearchIntent
}