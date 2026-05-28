package com.example.devfinder.core.domain

import com.example.devfinder.core.data.model.UserListResponse
import com.example.devfinder.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface GithubRepository {
    suspend fun getUser(username: String): Result<User>
    suspend fun getUsers(query: String): Result<UserListResponse>
    fun getFavorites(): Flow<List<User>>
    fun getFavoriteUserByName(name: String): User
    suspend fun saveFavorite(user: User)
    suspend fun removeFavorite(user: User)
    suspend fun isFavorite(userId: Long): Flow<Boolean>
    suspend fun updateFavorites(user: List<User>)
}