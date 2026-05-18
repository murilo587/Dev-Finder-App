package com.example.devfinder.core.di

import android.content.Context
import androidx.room.Room
import com.example.devfinder.core.database.AppDatabase
import com.example.devfinder.core.database.dao.FavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ) : AppDatabase {
        return Room.databaseBuilder(
                context = context,
                klass = AppDatabase::class.java,
                name = "dev_finder_app"
            ).fallbackToDestructiveMigration(dropAllTables = true).build()
    }

    @Provides
    fun provideFavoriteDao(db: AppDatabase): FavoriteDao {
        return db.favoriteDao()
    }
}