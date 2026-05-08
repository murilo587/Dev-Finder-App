package com.example.devfinder.feature.profile

sealed interface ProfileIntent {
    data class LoadUser(
        val username: String
    ) : ProfileIntent
}