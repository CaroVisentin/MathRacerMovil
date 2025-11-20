package com.app.mathracer.data.remote

import com.app.mathracer.data.entities.GameUpdateEntity
import com.google.gson.Gson
import com.microsoft.signalr.*
import com.app.mathracer.data.repository.UserRemoteRepository
import io.reactivex.rxjava3.core.Single
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
            android.util.Log.d("SignalR", "Creating hub connection to: $hubUrl/gameHub")
            
            // Configure access token provider so backend can verify Firebase token in middleware
            hubConnection = HubConnectionBuilder.create("$hubUrl/gameHub")
                .withTransport(TransportEnum.WEBSOCKETS)
                .withAccessTokenProvider(Single.create { emitter ->
                    try {
                        val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                        val task = firebaseUser?.getIdToken(false)
                        if (task == null) {
                            // RxJava Single does not accept null; emit empty string when no token available
                            emitter.onSuccess("")
                        } else {
                            task.addOnCompleteListener { t ->
                                if (t.isSuccessful) {
                                    emitter.onSuccess(t.result?.token ?: "")
                                } else {
                                    emitter.onError(t.exception ?: Exception("Failed to get idToken"))
                                }
                            }
                        }
                    } catch (e: Exception) {
                        emitter.onError(e)
                    }
                })
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
    
    suspend fun findMatch(playerUid: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            android.util.Log.d("SignalR", "Finding match for playerUid: $playerUid")
            android.util.Log.d("SignalR", "Connection state: ${hubConnection?.connectionState}")
            android.util.Log.d("SignalR", "isConnected(): ${isConnected()}")

            if (!isConnected()) {
                return@withContext Result.failure(Exception("Not connected to SignalR hub"))
            }

            // The server expects the player's Firebase UID so it can resolve the real display name
            val future = hubConnection?.invoke("FindMatch", playerUid)
            try {
                (future as? java.util.concurrent.CompletableFuture<Any?>)?.get()
                android.util.Log.d("SignalR", "FindMatch invoked successfully (uid) - completed")
            } catch (e: Exception) {
                android.util.Log.e("SignalR", "FindMatch invocation failed (get)", e)
                return@withContext Result.failure(e)
            }
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

            android.util.Log.d("SignalR", "🎯 Invoking SendAnswer with types (attempting numeric ids when possible)")
            
            val gameIdNumeric = gameId.toDoubleOrNull()
            val playerIdNumeric = playerId.toDoubleOrNull()

            if (gameIdNumeric != null && playerIdNumeric != null) {
                 
                try {
                    val gameIdInt = gameIdNumeric.toInt()
                    val playerIdInt = playerIdNumeric.toInt()
                    android.util.Log.d("SignalR", "🎯 Invoking SendAnswer with ints: $gameIdInt, $playerIdInt, $answer")
                    hubConnection?.invoke("SendAnswer", gameIdInt, playerIdInt, answer)
                } catch (e: Exception) {
                    android.util.Log.w("SignalR", "Could not convert ids to int, falling back to strings", e)
                    hubConnection?.invoke("SendAnswer", gameId, playerId, answer)
                }
            } else {
                
                hubConnection?.invoke("SendAnswer", gameId, playerId, answer)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("SignalR", "Failed to send answer", e)
            Result.failure(e)
        }
    }

    suspend fun joinGame(gameId: Int, password: String?): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            android.util.Log.d("SignalR", "Joining game via hub: gameId=$gameId, hasPassword=${password != null}")

            if (!isConnected()) {
                return@withContext Result.failure(Exception("Not connected to SignalR hub"))
            }

            // Invoke hub method JoinGame(gameId, password) and wait for completion
            try {
                val future = if (password != null) {
                    hubConnection?.invoke("JoinGame", gameId, password)
                } else {
                    hubConnection?.invoke("JoinGame", gameId)
                }

                (future as? java.util.concurrent.CompletableFuture<Any?>)?.get()
                android.util.Log.d("SignalR", "JoinGame invoke completed for gameId=$gameId")
                Result.success(Unit)
            } catch (e: Exception) {
                android.util.Log.e("SignalR", "JoinGame invocation failed", e)
                Result.failure(e)
            }
        } catch (e: Exception) {
            android.util.Log.e("SignalR", "Failed to join game", e)
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