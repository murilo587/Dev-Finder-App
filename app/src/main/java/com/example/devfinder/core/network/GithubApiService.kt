package com.example.devfinder.core.network

import retrofit2.Response
import retrofit2.http.GET

interface GithubApiService {
    @GET("users/{username}")
    suspend fun getUser(userName: String): Response<Unit>
}