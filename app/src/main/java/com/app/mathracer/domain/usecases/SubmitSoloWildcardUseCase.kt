package com.app.mathracer.domain.usecases

import com.app.mathracer.data.model.WildCard
import com.app.mathracer.domain.repositories.SoloGameRepository

class SubmitSoloWildcardUseCase(
    private val soloGameRepository: SoloGameRepository
) {
    suspend operator fun invoke(gameId: Int, wildcardId: Int): Result<WildCard> {
        return soloGameRepository.useWildcard(gameId, wildcardId)
    }
}