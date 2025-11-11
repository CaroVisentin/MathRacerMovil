package com.app.mathracer.domain.usecases

import com.app.mathracer.domain.models.Game
import com.app.mathracer.domain.repositories.GameRepository
import kotlinx.coroutines.flow.Flow

class ObserveGameUpdatesUseCase(
    private val gameRepository: GameRepository
) {
    operator fun invoke(): Flow<Game?> {
        return gameRepository.observeGameUpdates()
    }
}