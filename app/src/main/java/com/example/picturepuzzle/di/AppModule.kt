package com.example.picturepuzzle.di

import android.content.Context
import androidx.room.Room
import com.example.picturepuzzle.data.database.AppDatabase
import com.example.picturepuzzle.data.database.CompletedImageDao
import com.example.picturepuzzle.data.database.ScoreDao
import com.example.picturepuzzle.data.repository.ImageRepository
import com.example.picturepuzzle.data.repository.ScoreRepository
import com.example.picturepuzzle.utils.ImageProcessor
import com.example.picturepuzzle.utils.SoundManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "puzzle_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideScoreDao(database: AppDatabase): ScoreDao {
        return database.scoreDao()
    }

    @Provides
    @Singleton
    fun provideCompletedImageDao(database: AppDatabase): CompletedImageDao {
        return database.completedImageDao()
    }

    @Provides
    @Singleton
    fun provideScoreRepository(scoreDao: ScoreDao): ScoreRepository {
        return ScoreRepository(scoreDao)
    }

    @Provides
    @Singleton
    fun provideImageRepository(completedImageDao: CompletedImageDao): ImageRepository {
        return ImageRepository(completedImageDao)
    }

    @Provides
    @Singleton
    fun provideImageProcessor(): ImageProcessor {
        return ImageProcessor()
    }

    @Provides
    fun provideSoundManager(
        @ApplicationContext context: Context
    ): SoundManager {
        return SoundManager(context)
    }
}