package com.app.mathracer.data.repositories

import com.app.mathracer.data.mappers.GameMapper
import com.app.mathracer.data.remote.SignalRRemoteDataSource
import com.app.mathracer.domain.models.AnswerResult
import com.app.mathracer.domain.models.Game
import com.app.mathracer.domain.repositories.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepositoryImpl(
    private val signalRRemoteDataSource: SignalRRemoteDataSource
) : GameRepository {

    private val hubUrl = "http://10.0.2.2:5153" // Android emulator localhost

    override suspend fun initializeConnection(): Result<Unit> {
        return try {
            val initResult = signalRRemoteDataSource.initialize(hubUrl)
            if (initResult.isSuccess) {
                signalRRemoteDataSource.connect()
            } else {
                initResult
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun findMatch(playerName: String): Result<Unit> {
        return if (signalRRemoteDataSource.isConnected()) {
            signalRRemoteDataSource.findMatch(playerName)
        } else {
            Result.failure(Exception("Not connected to game server"))
        }
    }

    override suspend fun sendAnswer(gameId: String, playerId: String, answer: String): Result<AnswerResult> {
        return if (signalRRemoteDataSource.isConnected()) {
            val result = signalRRemoteDataSource.sendAnswer(gameId, playerId, answer)
            if (result.isSuccess) {
                Result.success(
                    AnswerResult(
                        isCorrect = true,
                        correctAnswer = "",
                        playerId = playerId
                    )
                )
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Failed to send answer"))
            }
        } else {
            Result.failure(Exception("Not connected to game server"))
        }
    }

    override fun observeGameUpdates(): Flow<Game?> {
        return signalRRemoteDataSource.gameEvents.map { entity ->
            entity?.let { GameMapper.entityToDomain(it) }
        }
    }

    override fun isConnected(): Boolean {
        return signalRRemoteDataSource.isConnected()
    }

    override suspend fun disconnect() {
        signalRRemoteDataSource.disconnect()
    }

    override suspend fun testHealth(): Result<String> {
        return signalRRemoteDataSource.testHealth()
    }
}