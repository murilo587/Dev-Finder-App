package com.example.devfinder.core.data.repository

import com.example.devfinder.core.data.mapper.toDomain
import com.example.devfinder.core.data.mapper.toEntity
import com.example.devfinder.core.data.model.UserListResponse
import com.example.devfinder.core.data.model.UserResponse
import com.example.devfinder.core.database.dao.FavoriteDao
import com.example.devfinder.core.domain.GithubRepository
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
    override suspend fun saveFavorite(user: User) {
        dao.insertFavorite(user.toEntity())
    }

    override suspend fun removeFavorite(user: User) {
        dao.deleteFavorite(user.toEntity())
    }

    override suspend fun isFavorite(userId: Long): Flow<Boolean> {
        return dao.isFavorite(userId)
    }
}