package com.example.devfinder.core.data.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    val id: Long,
    val login: String,
    val name: String?,
    @SerializedName("html_url")
    val htmlUrl: String,
    val bio: String,
    @SerializedName("public_repos")
    val publicRepos: Int,
    val followers: Int,
    val following: Int
)
