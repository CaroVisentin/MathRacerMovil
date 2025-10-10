package com.app.mathracer.domain.usecases

import com.app.mathracer.domain.models.AnswerResult
import com.app.mathracer.domain.repositories.GameRepository

class SubmitAnswerUseCase(
    private val gameRepository: GameRepository
) {
    suspend operator fun invoke(
        gameId: String, 
        playerId: String, 
        answer: String
    ): Result<AnswerResult> {
        return gameRepository.sendAnswer(gameId, playerId, answer)
    }
}