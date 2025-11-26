package com.app.mathracer.domain.usecases

import com.app.mathracer.domain.repositories.GameRepository

class GetLastRequestedGameIdUseCase(
    private val gameRepository: GameRepository
) {
    operator fun invoke(): Int? {
        return gameRepository.getLastRequestedJoinGameId()
    }
}
