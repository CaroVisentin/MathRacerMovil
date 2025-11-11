package com.app.mathracer.domain.usecases

import com.app.mathracer.data.model.SoloGameStartResponse
import com.app.mathracer.domain.repositories.SoloGameRepository

class StartSoloGameUseCase(
    private val soloGameRepository: SoloGameRepository
) {
    suspend operator fun invoke(levelId: Int): Result<SoloGameStartResponse> {
        return soloGameRepository.startSoloGame(levelId)
    }
}

