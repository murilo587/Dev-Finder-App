package com.example.devfinder.core.data.mapper

import com.example.devfinder.core.data.model.SearchUserItem
import com.example.devfinder.core.data.model.UserResponse
import com.example.devfinder.core.database.model.FavoriteUserEntity
import com.example.devfinder.core.domain.model.User

fun UserResponse.toDomain(): User {
    return User(
        id = this.id,
        login = this.login,
        avatarUrl = this.avatarUrl,
        htmlUrl = this.htmlUrl,
        bio = this.bio
    )
}
fun SearchUserItem.toDomain(): User {
    return User(
        id = this.id,
        login = this.login,
        avatarUrl = this.avatarUrl,
        htmlUrl = this.htmlUrl,
        bio = this.bio
    )
}
fun User.toEntity(): FavoriteUserEntity {
    return FavoriteUserEntity(
        id = this.id,
        login = this.login,
        avatarUrl = this.avatarUrl,
        htmlUrl = this.htmlUrl,
        bio = this.bio
    )
}
fun List<User>.toEntity(): List<FavoriteUserEntity> {
    return map { it.toEntity() }
}
fun FavoriteUserEntity.toDomain(): User {
    return User(
        id = this.id,
        login = this.login,
        avatarUrl = this.avatarUrl,
        htmlUrl = this.htmlUrl,
        bio = this.bio
    )
}