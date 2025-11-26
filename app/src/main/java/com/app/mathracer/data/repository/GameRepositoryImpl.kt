package com.app.mathracer.data.repository

import android.util.Log
import com.app.mathracer.BuildConfig
import com.app.mathracer.data.mappers.GameMapper
import com.app.mathracer.data.network.RetrofitClient
import com.app.mathracer.data.remote.SignalRRemoteDataSource
import com.app.mathracer.domain.models.AnswerResult
import com.app.mathracer.domain.models.Game
import com.app.mathracer.domain.repositories.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GameRepositoryImpl(
    private val signalRRemoteDataSource: SignalRRemoteDataSource
) : GameRepository {

    private val hubUrl = BuildConfig.HUB_URL

    override suspend fun initializeConnection(): Result<Unit> {
        return try {
            val api = RetrofitClient.api
            val hubToUse = try {
                val resp = api.getConnectionInfo()
                if (resp.isSuccessful) {
                    resp.body()?.hubUrl ?: hubUrl
                } else {
                    hubUrl
                }
            } catch (e: Exception) {
                android.util.Log.w("GameRepository", "Failed to fetch connection-info, using default hubUrl: $hubUrl", e)
                hubUrl
            }

            val initResult = signalRRemoteDataSource.initialize(hubToUse)
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

    override suspend fun findMatchWithMatchmaking(playerName: String): Result<Unit> {
        return if (signalRRemoteDataSource.isConnected()) {
            signalRRemoteDataSource.findMatchWithMatchmaking(playerName)
        } else {
            Result.failure(Exception("Not connected to game server"))
        }
    }

    override suspend fun joinGame(gameId: Int, password: String?): Result<Unit> {
        return try {
            android.util.Log.d("GameRepository", "joinGame requested for id=$gameId, hasPassword=${password != null}")

            if (!signalRRemoteDataSource.isConnected()) {
                android.util.Log.d("GameRepository", "Not connected, initializing connection before joinGame")
                val init = initializeConnection()
                if (init.isFailure) {
                    android.util.Log.e("GameRepository", "Failed to initialize connection before joinGame: ${init.exceptionOrNull()?.message}")
                    return init
                }
            }

            val result = signalRRemoteDataSource.joinGame(gameId, password)
            android.util.Log.d("GameRepository", "joinGame result: isSuccess=${result.isSuccess}, err=${result.exceptionOrNull()?.message}")
            result
        } catch (e: Exception) {
            android.util.Log.e("GameRepository", "Exception in joinGame", e)
            Result.failure(e)
        }
    }

    override fun getLastRequestedJoinGameId(): Int? {
        return signalRRemoteDataSource.lastRequestedJoinGameId
    }

    override fun clearLastRequestedJoinGameId() {
        signalRRemoteDataSource.lastRequestedJoinGameId = null
    }

    override suspend fun sendAnswer(gameId: String, playerId: String, answer: Int): Result<AnswerResult> {
        return if (signalRRemoteDataSource.isConnected()) {
            try {
                Log.d("GameRepository", "🔁 Sending answer to remote: gameId=$gameId, playerId=$playerId, answer=$answer")
                val result = signalRRemoteDataSource.sendAnswer(gameId, playerId, answer)
                Log.d("GameRepository", "🔁 Remote sendAnswer result: isSuccess=${result.isSuccess}")
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
                Log.e("GameRepository", "❌ Exception sending answer", e)
                Result.failure(e)
            }
        } else {
            Log.e("GameRepository", "❌ Not connected to game server when sending answer")
            Result.failure(Exception("Not connected to game server"))
        }
    }

    override suspend fun usePowerUp(
        gameId: String,
        playerId: String,
        powerUpType: Int
    ): Result<Unit> {
        return if (signalRRemoteDataSource.isConnected()) {
            try {
                android.util.Log.d(
                    "GameRepository",
                    "⚡ Using PowerUp: gameId=$gameId, playerId=$playerId, type=$powerUpType"
                )

                val result = signalRRemoteDataSource.usePowerUp(gameId, playerId, powerUpType)

                if (result.isSuccess) {
                    android.util.Log.d("GameRepository", "⚡ PowerUp sent successfully")
                    Result.success(Unit)
                } else {
                    android.util.Log.e(
                        "GameRepository",
                        "❌ Failed to send PowerUp: ${result.exceptionOrNull()?.message}"
                    )
                    Result.failure(result.exceptionOrNull() ?: Exception("Failed to use power-up"))
                }

            } catch (e: Exception) {
                android.util.Log.e("GameRepository", "❌ Exception using PowerUp", e)
                Result.failure(e)
            }
        } else {
            android.util.Log.e(
                "GameRepository",
                "❌ Not connected to game server when trying to use power-up"
            )
            Result.failure(Exception("Not connected to game server"))
        }
    }

    override suspend fun leaveGame(gameId: String, playerId: String): Result<Unit> {
        return signalRRemoteDataSource.leaveGame(gameId, playerId)
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