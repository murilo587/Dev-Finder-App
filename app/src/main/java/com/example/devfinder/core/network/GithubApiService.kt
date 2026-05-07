package com.example.devfinder.core.network

import com.example.devfinder.core.data.model.UserListResponse
import com.example.devfinder.core.data.model.UserResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApiService {
    @GET("users/{username}")
    suspend fun getUser(@Path("username") username: String): Response<UserResponse>
    @GET("search/users")
    suspend fun getUsers(@Query("q") query: String): Response<UserListResponse>
}