package com.app.mathracer.domain.usecases

import com.app.mathracer.data.model.SoloAnswerResponse
import com.app.mathracer.domain.repositories.SoloGameRepository

class SubmitSoloAnswerUseCase(
    private val soloGameRepository: SoloGameRepository
) {
    suspend operator fun invoke(gameId: Int, answer: Int): Result<SoloAnswerResponse> {
        return soloGameRepository.submitSoloAnswer(gameId, answer)
    }
}

