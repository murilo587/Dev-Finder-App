package com.example.devfinder.core.network

import com.example.devfinder.core.data.model.UserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubApiService {
    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): Response<UserResponse>
}