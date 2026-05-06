package com.example.devfinder.core.domain

import com.example.devfinder.core.data.model.UserResponse

interface GithubRepository {
    suspend fun getUser(username: String): Result<UserResponse>
}