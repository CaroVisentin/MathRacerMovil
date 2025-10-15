package com.app.mathracer.domain.usecases

import com.app.mathracer.domain.repositories.GameRepository

class InitializeGameConnectionUseCase(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return gameRepository.initializeConnection()
    }
}