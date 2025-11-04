package com.app.mathracer.domain.usecases

import com.app.mathracer.data.model.SoloGameUpdateResponse
import com.app.mathracer.domain.repositories.SoloGameRepository
import kotlinx.coroutines.flow.Flow

class ObserveSoloGameUpdatesUseCase(
    private val soloGameRepository: SoloGameRepository
) {
    operator fun invoke(gameId: Int, intervalMs: Long = 2000): Flow<SoloGameUpdateResponse?> {
        return soloGameRepository.observeSoloGameUpdates(gameId, intervalMs)
    }
}

