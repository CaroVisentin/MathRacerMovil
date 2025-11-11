package com.app.mathracer.domain.repositories

import com.app.mathracer.data.model.SoloAnswerResponse
import com.app.mathracer.data.model.SoloGameStartResponse
import com.app.mathracer.data.model.SoloGameUpdateResponse
import kotlinx.coroutines.flow.Flow

interface SoloGameRepository {
    
    /**
     * Start a solo game for the given level
     */
    suspend fun startSoloGame(levelId: Int): Result<SoloGameStartResponse>
    
    /**
     * Get current game state update
     */
    suspend fun getSoloGameUpdate(gameId: Int): Result<SoloGameUpdateResponse>
    
    /**
     * Submit an answer for the current question
     */
    suspend fun submitSoloAnswer(gameId: Int, answer: Int): Result<SoloAnswerResponse>
    
    /**
     * Observe game updates via polling
     */
    fun observeSoloGameUpdates(gameId: Int, intervalMs: Long = 2000): Flow<SoloGameUpdateResponse?>
}

