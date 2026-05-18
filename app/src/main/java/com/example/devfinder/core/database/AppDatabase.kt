package com.example.devfinder.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.devfinder.core.database.dao.FavoriteDao
import com.example.devfinder.core.database.model.FavoriteUserEntity

@Database(entities = [FavoriteUserEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
}