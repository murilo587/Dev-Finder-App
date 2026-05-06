package com.example.devfinder.core.data.repository

import com.example.devfinder.core.data.model.UserResponse
import com.example.devfinder.core.domain.GithubRepository
import com.example.devfinder.core.network.GithubApiService
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor (
    private val apiService: GithubApiService): GithubRepository {

        override suspend fun getUser(username: String): Result<UserResponse> {
            try {
                val response = apiService.getUser(username)
                return if (response.isSuccessful) {
                    val body = response.body()
                    if(body != null) {
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
}