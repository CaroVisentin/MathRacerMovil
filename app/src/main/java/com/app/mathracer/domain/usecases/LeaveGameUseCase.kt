package com.app.mathracer.domain.usecases

import com.app.mathracer.domain.repositories.GameRepository

class LeaveGameUseCase(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(gameId: String, playerId: String): Result<Unit> {
        return gameRepository.leaveGame(gameId, playerId)
    }
}
