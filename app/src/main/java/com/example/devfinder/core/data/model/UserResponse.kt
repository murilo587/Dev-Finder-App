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
    @SerializedName("avatar_url")
    val avatarUrl: String,
    val followers: Int,
    val following: Int
)

data class SearchUserItem(
    val id: Long,
    val login: String,
    @SerializedName("html_url")
    val htmlUrl: String,
    @SerializedName("avatar_url")
    val avatarUrl: String
)

data class UserListResponse(
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("incomplete_results")
    val incompleteResults: Boolean,
    val items: List<SearchUserItem>
)
