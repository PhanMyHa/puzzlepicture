package com.example.picturepuzzle.di

import com.example.picturepuzzle.data.firebase.AuthManager
import com.example.picturepuzzle.data.firebase.AuthRepository
import com.example.picturepuzzle.data.firebase.FriendRepository
import com.example.picturepuzzle.data.firebase.ProfileRepository
import com.example.picturepuzzle.data.firebase.RankRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideAuthManager(): AuthManager = AuthManager()

    @Provides
    @Singleton
    fun provideRankRepository(authManager: AuthManager): RankRepository =
        RankRepository(authManager)

    @Provides
    @Singleton
    fun provideFriendRepository(authManager: AuthManager): FriendRepository =
        FriendRepository(authManager)

    @Provides
    @Singleton
    fun provideAuthRepo(): AuthRepository = AuthRepository()

    @Provides
    @Singleton
    fun provideProfileRepo(): ProfileRepository = ProfileRepository()
}