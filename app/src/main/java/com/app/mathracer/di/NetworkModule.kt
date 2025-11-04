package com.app.mathracer.di

import com.app.mathracer.data.remote.SignalRRemoteDataSource
import com.app.mathracer.data.repositories.GameRepositoryImpl
import com.app.mathracer.data.repositories.SoloGameRepositoryImpl
import com.app.mathracer.domain.repositories.GameRepository
import com.app.mathracer.domain.repositories.SoloGameRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideSignalRRemoteDataSource(): SignalRRemoteDataSource {
        return SignalRRemoteDataSource()
    }
    
    @Provides
    @Singleton
    fun provideGameRepository(
        signalRRemoteDataSource: SignalRRemoteDataSource
    ): GameRepository {
        return GameRepositoryImpl(signalRRemoteDataSource)
    }
    
    @Provides
    @Singleton
    fun provideSoloGameRepository(): SoloGameRepository {
        return SoloGameRepositoryImpl()
    }
}