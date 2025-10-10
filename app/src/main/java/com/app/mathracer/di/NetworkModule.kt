package com.app.mathracer.di

import com.app.mathracer.data.repository.GameRepository
import com.app.mathracer.data.services.SignalRService
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
    fun provideSignalRService(): SignalRService {
        return SignalRService()
    }
    
    @Provides
    @Singleton
    fun provideGameRepository(signalRService: SignalRService): GameRepository {
        return GameRepository(signalRService)
    }
    
    @Provides
    fun provideHubUrl(): String {
        // Basado en tu test-liveserver.html
        return "http://10.0.2.2:5153/gameHub" // Para emulador Android (tu localhost:5153)
        // return "http://192.168.1.X:5153/gameHub" // Para dispositivo físico (cambiar IP)
    }
}