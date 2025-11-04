package com.app.mathracer.di

import com.app.mathracer.domain.repositories.GameRepository
import com.app.mathracer.domain.repositories.SoloGameRepository
import com.app.mathracer.domain.usecases.FindMatchUseCase
import com.app.mathracer.domain.usecases.InitializeGameConnectionUseCase
import com.app.mathracer.domain.usecases.ObserveGameUpdatesUseCase
import com.app.mathracer.domain.usecases.ObserveSoloGameUpdatesUseCase
import com.app.mathracer.domain.usecases.StartSoloGameUseCase
import com.app.mathracer.domain.usecases.SubmitAnswerUseCase
import com.app.mathracer.domain.usecases.SubmitSoloAnswerUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    
    @Provides
    fun provideSubmitAnswerUseCase(
        gameRepository: GameRepository
    ): SubmitAnswerUseCase {
        return SubmitAnswerUseCase(gameRepository)
    }
    
    @Provides
    fun provideFindMatchUseCase(
        gameRepository: GameRepository
    ): FindMatchUseCase {
        return FindMatchUseCase(gameRepository)
    }
    
    @Provides
    fun provideObserveGameUpdatesUseCase(
        gameRepository: GameRepository
    ): ObserveGameUpdatesUseCase {
        return ObserveGameUpdatesUseCase(gameRepository)
    }
    
    @Provides
    fun provideInitializeGameConnectionUseCase(
        gameRepository: GameRepository
    ): InitializeGameConnectionUseCase {
        return InitializeGameConnectionUseCase(gameRepository)
    }
    
    // Solo Game (History Mode) use cases
    @Provides
    fun provideStartSoloGameUseCase(
        soloGameRepository: SoloGameRepository
    ): StartSoloGameUseCase {
        return StartSoloGameUseCase(soloGameRepository)
    }
    
    @Provides
    fun provideObserveSoloGameUpdatesUseCase(
        soloGameRepository: SoloGameRepository
    ): ObserveSoloGameUpdatesUseCase {
        return ObserveSoloGameUpdatesUseCase(soloGameRepository)
    }
    
    @Provides
    fun provideSubmitSoloAnswerUseCase(
        soloGameRepository: SoloGameRepository
    ): SubmitSoloAnswerUseCase {
        return SubmitSoloAnswerUseCase(soloGameRepository)
    }
}