package com.app.mathracer.domain.usecases

import com.app.mathracer.domain.repositories.GameRepository

class FindMatchWithMatchmakingUseCase(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(playerName: String): Result<Unit> {
        return gameRepository.findMatchWithMatchmaking(playerName)
    }
}
