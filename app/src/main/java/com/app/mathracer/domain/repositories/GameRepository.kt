package com.app.mathracer.domain.repositories

import com.app.mathracer.domain.models.AnswerResult
import com.app.mathracer.domain.models.Game
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    
    /**
     * Initialize connection to the game server
     */
    suspend fun initializeConnection(): Result<Unit>
    
    /**
     * Find a match for the given player
     */
    suspend fun findMatch(playerName: String): Result<Unit>
    suspend fun findMatchWithMatchmaking(playerName: String): Result<Unit>
    
    /**
     * Send answer for a specific game and player
     */
    suspend fun sendAnswer(gameId: String, playerId: String, answer: Int): Result<AnswerResult>

    /**
     * Join an existing created game (by id). Password optional for private games.
     */
    suspend fun joinGame(gameId: Int, password: String?): Result<Unit>

    suspend fun usePowerUp(gameId: String, playerId: String, powerUpType: Int): Result<Unit>

    fun getLastRequestedJoinGameId(): Int?

    fun clearLastRequestedJoinGameId()
    
    /**
     * Observe game state changes
     */
    fun observeGameUpdates(): Flow<Game?>
    
    /**
     * Check if connected to game server
     */
    fun isConnected(): Boolean
    
    /**
     * Disconnect from game server
     */
    suspend fun disconnect()

    /**
     * Gracefully leave a game from the client side (notify server and disconnect).
     */
    suspend fun leaveGame(gameId: String, playerId: String): Result<Unit>
    
    /**
     * Test server health
     */
    suspend fun testHealth(): Result<String>
}