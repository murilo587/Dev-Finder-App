package com.example.devfinder.core.data.repository

import com.example.devfinder.core.data.mapper.toDomain
import com.example.devfinder.core.data.mapper.toEntity
import com.example.devfinder.core.data.mapper.toUserRepositoryEntity
import com.example.devfinder.core.data.mapper.toUserStarredEntity
import com.example.devfinder.core.data.model.UserListResponse
import com.example.devfinder.core.database.dao.FavoriteDao
import com.example.devfinder.core.domain.GithubRepository
import com.example.devfinder.core.domain.model.Repo
import com.example.devfinder.core.domain.model.User
import com.example.devfinder.core.network.GithubApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor (
    private val apiService: GithubApiService,
    private val dao: FavoriteDao
): GithubRepository {

        override suspend fun getUser(username: String): Result<User> {
            try {
                val response = apiService.getUser(username)
                return if (response.isSuccessful) {
                    val body = response.body()
                    if(body != null) {
                        Result.success(body.toDomain())
                    } else {
                        Result.failure(Exception("Empty Body"))
                    }
                } else {
                    Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                 return Result.failure(e)
            }
        }

    override suspend fun getUsers(query: String): Result<UserListResponse> {
        try {
            val response = apiService.getUsers(query)
            return if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Empty Body"))
                }
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
    override fun getFavorites(): Flow<List<User>> {
        return dao.getFavorites().map { entities ->
            entities.map{ it.toDomain() }
        }
    }
    override fun getFavoriteUserByName(name: String): User? {
        return dao.getFavoriteUserByName(name)?.toDomain()
    }
    override fun getLocalRepositories(userId: Long): Flow<List<Repo>> {
        return dao.getLocalRepositories(userId).map { entities ->
            entities.map{ it.toDomain() }
        }
    }
    override fun getLocalStarredRepositories(userId: Long): Flow<List<Repo>> {
        return dao.getLocalStarredRepositories(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    override suspend fun saveFavorite(user: User) {
        dao.insertFavorite(user.toEntity())
    }
    override suspend fun saveRepositories(repos: List<Repo>, userId: Long) {
        dao.insertRepositories(repos.toUserRepositoryEntity(userId))
    }
    override suspend fun saveStarredRepositories(repos: List<Repo>, userId: Long) {
        return dao.insertStarredRepositories(repos.toUserStarredEntity(userId))
    }
    override suspend fun removeFavorite(user: User) {
        dao.deleteFavorite(user.toEntity())
    }
    override suspend fun removeRepositories(userId: Long) {
        dao.clearRepositories(userId)
    }
    override suspend fun removeStarredRepositories(userId: Long) {
        dao.clearStarredRepositories(userId)
    }
    override suspend fun isFavorite(userId: Long): Flow<Boolean> {
        return dao.isFavorite(userId)
    }
    override suspend fun updateFavorites(user: List<User>) {
        dao.updateFavorites(user.toEntity())
    }
    override suspend fun updateRepositories(repos: List<Repo>, userId: Long) {
        dao.updateLocalRepositories(repos.toUserRepositoryEntity(userId))
    }
    override suspend fun updateStarredRepositories(repos: List<Repo>, userId: Long) {
        dao.updateLocalStarredRepositories(repos.toUserStarredEntity(userId))
    }
    override suspend fun checkIsFavoriteDirect(userId: Long): Boolean {
        return dao.checkIsFavoriteDirect(userId)
    }
    override suspend fun getRepos(username: String): Result<List<Repo>> {
        return try {
            val response = apiService.getRepos(username)
            if (response.isSuccessful) {
                val body = response.body()
                if (body.isNullOrEmpty()) {
                    Result.success(emptyList())
                } else {
                    Result.success(body.toDomain())
                }
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getStarredRepos(username: String): Result<List<Repo>> {
        return try {
            val response = apiService.getStarredRepos(username)
            if (response.isSuccessful) {
                val body = response.body()
                if (body.isNullOrEmpty()) {
                    Result.success(emptyList())
                } else {
                    Result.success(body.toDomain())
                }
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}