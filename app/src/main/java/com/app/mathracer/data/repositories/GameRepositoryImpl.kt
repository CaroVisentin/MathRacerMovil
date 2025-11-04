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

    override suspend fun sendAnswer(gameId: String, playerId: String, answer: Int): Result<AnswerResult> {
        return if (signalRRemoteDataSource.isConnected()) {
            try {
                android.util.Log.d("GameRepository", "🔁 Sending answer to remote: gameId=$gameId, playerId=$playerId, answer=$answer")
                val result = signalRRemoteDataSource.sendAnswer(gameId, playerId, answer)
                android.util.Log.d("GameRepository", "🔁 Remote sendAnswer result: isSuccess=${result.isSuccess}")
                if (result.isSuccess) {
                    Result.success(
                        AnswerResult(
                            isCorrect = true,
                            correctAnswer = answer,
                            playerId = playerId
                        )
                    )
                } else {
                    Result.failure(result.exceptionOrNull() ?: Exception("Failed to send answer"))
                }
            } catch (e: Exception) {
                android.util.Log.e("GameRepository", "❌ Exception sending answer", e)
                Result.failure(e)
            }
        } else {
            android.util.Log.e("GameRepository", "❌ Not connected to game server when sending answer")
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