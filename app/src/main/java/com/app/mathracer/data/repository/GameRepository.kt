package com.app.mathracer.data.repository

import com.app.mathracer.data.services.GameEvent
import com.app.mathracer.data.services.SignalRService
import com.microsoft.signalr.HubConnectionState
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val signalRService: SignalRService
) {
    
    val connectionState: StateFlow<HubConnectionState> = signalRService.connectionState
    val gameEvents: StateFlow<GameEvent?> = signalRService.gameEvents
    
    suspend fun initializeConnection(hubUrl: String): Result<Unit> {
        return try {
            signalRService.initialize(hubUrl)
            signalRService.connect()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun findMatch(playerName: String): Result<Unit> {
        return if (signalRService.isConnected()) {
            signalRService.findMatch(playerName)
        } else {
            Result.failure(Exception("Not connected to game server"))
        }
    }
    
    suspend fun sendAnswer(gameId: String, playerId: String, answer: String): Result<Unit> {
        return if (signalRService.isConnected()) {
            signalRService.sendAnswer(gameId, playerId, answer)
        } else {
            Result.failure(Exception("Not connected to game server"))
        }
    }
    
    suspend fun testHealth(): Result<String> {
        return signalRService.testHealth()
    }
    
    suspend fun disconnect() {
        signalRService.disconnect()
    }
    
    fun isConnected(): Boolean {
        return signalRService.isConnected()
    }
}