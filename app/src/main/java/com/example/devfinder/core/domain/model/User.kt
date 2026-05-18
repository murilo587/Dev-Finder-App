package com.example.devfinder.core.domain.model

data class User(
    val id: Long,
    val login: String,
    val avatarUrl: String,
    val htmlUrl: String,
    val bio: String?
)