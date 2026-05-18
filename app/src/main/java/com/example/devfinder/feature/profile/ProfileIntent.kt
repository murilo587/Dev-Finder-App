package com.example.devfinder.feature.profile

import com.example.devfinder.core.data.model.UserResponse
import com.example.devfinder.core.domain.model.User
sealed interface ProfileIntent {
    data class LoadUser(
        val username: String
    ) : ProfileIntent
    data class ToggleFavorite(val user: User) : ProfileIntent
}