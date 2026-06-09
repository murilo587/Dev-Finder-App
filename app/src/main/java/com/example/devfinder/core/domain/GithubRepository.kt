package com.example.devfinder.core.domain

import com.example.devfinder.core.data.model.UserListResponse
import com.example.devfinder.core.domain.model.Repo
import com.example.devfinder.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface GithubRepository {
    suspend fun getUser(username: String): Result<User>
    suspend fun getUsers(query: String): Result<UserListResponse>
    fun getFavorites(): Flow<List<User>>
    fun getFavoriteUserByName(name: String): User?
    fun getLocalRepositories(userId: Long): Flow<List<Repo>>
    fun getLocalStarredRepositories(userId: Long): Flow<List<Repo>>
    suspend fun saveFavorite(user: User)
    suspend fun saveRepositories(repos: List<Repo>, userId: Long)
    suspend fun saveStarredRepositories(repos: List<Repo>, userId: Long)
    suspend fun removeFavorite(user: User)
    suspend fun removeRepositories(userId: Long)
    suspend fun removeStarredRepositories(userId: Long)
    suspend fun isFavorite(userId: Long): Flow<Boolean>
    suspend fun checkIsFavoriteDirect(userId: Long): Boolean
    suspend fun updateFavorites(user: List<User>)
    suspend fun updateRepositories(repos: List<Repo>, userId: Long)
    suspend fun updateStarredRepositories(repos: List<Repo>, userId: Long)
    suspend fun getRepos(username: String): Result<List<Repo>>
    suspend fun getStarredRepos(username: String): Result<List<Repo>>
}