package com.example.devfinder.core.data.mapper

import com.example.devfinder.core.data.model.RepoResponse
import com.example.devfinder.core.database.model.UserRepositoryEntity
import com.example.devfinder.core.database.model.UserStarredEntity
import com.example.devfinder.core.domain.model.Repo
fun List<RepoResponse>.toDomain(): List<Repo> {
    return map {
        Repo(
            id = it.id,
            name = it.name,
            language = it.language,
            createdAt = it.createdAt,
            updatedAt = it.updatedAt,
            description = it.description,
            stargazersCount = it.stargazersCount
        )
    }
}

fun UserRepositoryEntity.toDomain(): Repo {
    return Repo(
        id = repoId,
        name = name,
        description = description,
        language = language,
        createdAt = createdAt,
        updatedAt = updatedAt,
        stargazersCount = stargazersCount
    )
}

fun UserStarredEntity.toDomain(): Repo {
    return Repo(
        id = repoId,
        name = name,
        description = description,
        language = language,
        createdAt = createdAt,
        updatedAt = updatedAt,
        stargazersCount = stargazersCount
    )
}

fun List<Repo>.toUserStarredEntity(userId: Long): List<UserStarredEntity> {
    return map {
        UserStarredEntity(
            repoId = it.id,
            userid = userId,
            name = it.name,
            description = it.description,
            language = it.language,
            createdAt = it.createdAt,
            updatedAt = it.updatedAt,
            stargazersCount = it.stargazersCount
        )
    }
}

fun List<Repo>.toUserRepositoryEntity(userId: Long): List<UserRepositoryEntity> {
    return map {
        UserRepositoryEntity(
            repoId = it.id,
            userid = userId,
            name = it.name,
            description = it.description,
            language = it.language,
            createdAt = it.createdAt,
            updatedAt = it.updatedAt,
            stargazersCount = it.stargazersCount
        )
    }
}