package com.app.mathracer.data.remote

import com.app.mathracer.data.entities.GameUpdateEntity
import com.google.gson.Gson
import com.microsoft.signalr.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignalRRemoteDataSource @Inject constructor() {
    
    private var hubConnection: HubConnection? = null
    private val gson = Gson()
    
    private val _connectionState = MutableStateFlow(HubConnectionState.DISCONNECTED)

    private val _gameEvents = MutableStateFlow<GameUpdateEntity?>(null)
    val gameEvents: StateFlow<GameUpdateEntity?> = _gameEvents
    
    suspend fun initialize(hubUrl: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            android.util.Log.d("SignalR", "Creating hub connection to: $hubUrl/gamehub")
            hubConnection = HubConnectionBuilder.create("$hubUrl/gamehub")
                .withTransport(TransportEnum.WEBSOCKETS)
                .shouldSkipNegotiate(false)
                .build()

            hubConnection?.on("GameUpdate", { gameUpdateObj: Any ->
                try {
                    android.util.Log.d("SignalR", "🎯 GameUpdate received!")
                    android.util.Log.d("SignalR", "Object type: ${gameUpdateObj::class.java.name}")
                    android.util.Log.d("SignalR", "Object content: $gameUpdateObj")

                    val jsonString = gson.toJson(gameUpdateObj)
                    android.util.Log.d("SignalR", "GameUpdate JSON: $jsonString")

                    val gameUpdate = gson.fromJson(jsonString, GameUpdateEntity::class.java)
                    android.util.Log.d("SignalR", "✅ GameUpdate parsed - GameId: ${gameUpdate.gameId}, Status: ${gameUpdate.status}")

                    _gameEvents.value = gameUpdate
                    android.util.Log.d("SignalR", "🚀 GameUpdate event published to flow - SUCCESS!")
                    
                } catch (e: Exception) {
                    android.util.Log.e("SignalR", "❌ Error processing GameUpdate", e)
                    android.util.Log.e("SignalR", "Failed object: $gameUpdateObj")
                }
            }, Object::class.java)

            hubConnection?.onClosed { error ->
                android.util.Log.d("SignalR", "Connection closed: ${error?.message}")
            }
            
            // Connection settings for stability
            hubConnection?.setServerTimeout(30000) // 30 segundos timeout
            hubConnection?.setKeepAliveInterval(15000) // 15 segundos keep alive
            
            android.util.Log.d("SignalR", "All GameUpdate listeners configured with fallback")
            
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("SignalR", "Failed to initialize", e)
            Result.failure(e)
        }
    }
    
    suspend fun connect(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            android.util.Log.d("SignalR", "Attempting to connect...")
            if (hubConnection == null) {
                android.util.Log.e("SignalR", "Hub connection is null")
                return@withContext Result.failure(Exception("Hub connection not initialized"))
            }
            
            hubConnection?.start()

            kotlinx.coroutines.delay(1000)
            
            if (isConnected()) {
                _connectionState.value = HubConnectionState.CONNECTED
                android.util.Log.d("SignalR", "Connected successfully")
                Result.success(Unit)
            } else {
                android.util.Log.e("SignalR", "Connection failed - not connected")
                Result.failure(Exception("Failed to establish connection"))
            }
        } catch (e: Exception) {
            android.util.Log.e("SignalR", "Connection error", e)
            Result.failure(e)
        }
    }
    
    suspend fun findMatch(playerName: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            android.util.Log.d("SignalR", "Finding match for player: $playerName")
            android.util.Log.d("SignalR", "Connection state: ${hubConnection?.connectionState}")
            android.util.Log.d("SignalR", "isConnected(): ${isConnected()}")
            
            if (!isConnected()) {
                return@withContext Result.failure(Exception("Not connected to SignalR hub"))
            }
            
            hubConnection?.invoke("FindMatch", playerName)
            android.util.Log.d("SignalR", "FindMatch invoked successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("SignalR", "Failed to find match", e)
            Result.failure(e)
        }
    }
    
    suspend fun sendAnswer(gameId: String, playerId: String, answer: Int): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            android.util.Log.d("SignalR", "🎯 Preparing to send answer")
            android.util.Log.d("SignalR", "🎯 GameId: '$gameId' (${gameId::class.java.simpleName})")
            android.util.Log.d("SignalR", "🎯 PlayerId: '$playerId' (${playerId::class.java.simpleName})")  
            android.util.Log.d("SignalR", "🎯 Answer: '$answer' (${answer::class.java.simpleName})")

            android.util.Log.d("SignalR", "🎯 Invoking SendAnswer(int, int, int) with correct types")
            hubConnection?.invoke("SendAnswer", gameId, playerId, answer)
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("SignalR", "Failed to send answer", e)
            Result.failure(e)
        }
    }
    
    fun isConnected(): Boolean {
        return hubConnection?.connectionState == HubConnectionState.CONNECTED
    }
    
    suspend fun disconnect() = withContext(Dispatchers.IO) {
        try {
            hubConnection?.stop()
            _connectionState.value = HubConnectionState.DISCONNECTED
        } catch (e: Exception) {
            android.util.Log.e("SignalR", "Failed to disconnect", e)
        }
    }
    
    suspend fun testHealth(): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (isConnected()) {
                Result.success("Connected to SignalR Hub")
            } else {
                Result.failure(Exception("Not connected"))
            }
        } catch (e: Exception) {
            android.util.Log.e("SignalR", "Health test failed", e)
            Result.failure(e)
        }
    }
}