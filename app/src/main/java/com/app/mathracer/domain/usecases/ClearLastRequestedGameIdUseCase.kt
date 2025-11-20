package com.app.mathracer.domain.usecases

import com.app.mathracer.domain.repositories.GameRepository

class ClearLastRequestedGameIdUseCase(
    private val gameRepository: GameRepository
) {
    operator fun invoke() {
        gameRepository.clearLastRequestedJoinGameId()
    }
}
