package com.example.devfinder.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.devfinder.core.database.dao.FavoriteDao
import com.example.devfinder.core.database.model.FavoriteUserEntity
import com.example.devfinder.core.database.model.UserRepositoryEntity
import com.example.devfinder.core.database.model.UserStarredEntity

@Database(
    entities = [
        FavoriteUserEntity::class,
        UserRepositoryEntity::class, UserStarredEntity::class], version = 3, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}