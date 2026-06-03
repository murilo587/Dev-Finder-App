package com.example.devfinder.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.devfinder.core.database.model.FavoriteUserEntity
import com.example.devfinder.core.database.model.UserRepositoryEntity
import com.example.devfinder.core.database.model.UserStarredEntity
import com.example.devfinder.core.domain.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites")
    fun getFavorites(): Flow<List<FavoriteUserEntity>>
    @Query("SELECT * FROM favorites WHERE login = :name")
    fun getFavoriteUserByName(name: String): User
    @Query("SELECT * FROM user_repositories WHERE userid = :userId")
    fun getLocalRepositories(userId: Long): Flow<List<UserRepositoryEntity>>
    @Query("SELECT * FROM user_starred_repositories WHERE userid = :userId")
    fun getLocalStarredRepositories(userId: Long): Flow<List<UserStarredEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(user: FavoriteUserEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepositories(repo: List<UserRepositoryEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStarredRepositories(repos: List<UserStarredEntity>)
    @Delete
    suspend fun deleteFavorite(user: FavoriteUserEntity)
    @Query("DELETE FROM user_repositories WHERE userId = :userId")
    suspend fun clearRepositories(userId: Long)
    @Query("DELETE FROM user_starred_repositories WHERE userId = :userId")
    suspend fun clearStarredRepositories(userId: Long)
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :userId)")
    fun isFavorite(userId: Long): Flow<Boolean>
    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :userId)")
    suspend fun checkIsFavoriteDirect(userId: Long): Boolean
    @Update
    suspend fun updateFavorites(user: List<FavoriteUserEntity>)
    @Update
    suspend fun updateLocalRepositories(repos: List<UserRepositoryEntity>)
    @Update
    suspend fun updateLocalStarredRepositories(repos: List<UserStarredEntity>)

}