package com.example.devfinder.core.domain

import com.example.devfinder.core.data.model.UserListResponse
import com.example.devfinder.core.data.model.UserResponse
import com.example.devfinder.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface GithubRepository {
    suspend fun getUser(username: String): Result<User>
    suspend fun getUsers(query: String): Result<UserListResponse>
    fun getFavorites(): Flow<List<User>>
    suspend fun saveFavorite(user: User)
    suspend fun removeFavorite(user: User)
    suspend fun isFavorite(userId: Long): Flow<Boolean>
}