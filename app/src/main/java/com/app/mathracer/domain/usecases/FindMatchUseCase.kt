package com.app.mathracer.domain.usecases

import com.app.mathracer.domain.repositories.GameRepository

class FindMatchUseCase(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(playerName: String): Result<Unit> {
        return gameRepository.findMatch(playerName)
    }
}