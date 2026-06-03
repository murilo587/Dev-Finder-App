package com.example.devfinder.core.domain.model

import com.google.gson.annotations.SerializedName

data class Repo (
    val id: Long,
    val name: String,
    val description: String?,
    val language: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("stargazers_count")
    val stargazersCount: Int
)