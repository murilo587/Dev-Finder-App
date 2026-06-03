package com.example.devfinder.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("user_starred_repositories")
data class UserStarredEntity(
    @PrimaryKey val repoId: Long,
    val userid: Long,
    val name: String,
    val description: String?,
    val language: String?,
    val createdAt: String,
    val updatedAt: String,
    val stargazersCount: Int
)
