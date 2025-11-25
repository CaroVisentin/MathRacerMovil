package com.app.mathracer.domain.usecases

import com.app.mathracer.domain.repositories.GameRepository

class UsePowerUpUseCase (
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(
        gameId: String,
        playerId: String,
        powerUpType: Int
    ): Result<Unit> {
        return gameRepository.usePowerUp(gameId, playerId, powerUpType)
    }
}