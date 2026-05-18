package com.example.devfinder.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.devfinder.core.database.model.FavoriteUserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun getFavorites(): Flow<List<FavoriteUserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(user: FavoriteUserEntity)

    @Delete
    suspend fun deleteFavorite(user: FavoriteUserEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :userId)")
    fun isFavorite(userId: Long): Flow<Boolean>
}