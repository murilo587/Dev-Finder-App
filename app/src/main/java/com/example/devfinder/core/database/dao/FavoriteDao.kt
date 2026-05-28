package com.example.devfinder.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.devfinder.core.database.model.FavoriteUserEntity
import com.example.devfinder.core.domain.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun getFavorites(): Flow<List<FavoriteUserEntity>>
    @Query("SELECT * FROM favorites WHERE login = :name")
    fun getFavoriteUserByName(name: String): User
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(user: FavoriteUserEntity)
    @Delete
    suspend fun deleteFavorite(user: FavoriteUserEntity)
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :userId)")
    fun isFavorite(userId: Long): Flow<Boolean>
    @Update
    suspend fun updateFavorites(user: List<FavoriteUserEntity>)
}