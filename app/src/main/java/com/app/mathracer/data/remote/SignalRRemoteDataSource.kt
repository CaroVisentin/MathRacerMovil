package com.app.mathracer.data.remote

import com.app.mathracer.data.entities.GameUpdateEntity
import com.google.gson.Gson
import com.microsoft.signalr.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
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
    @Volatile
    var lastRequestedJoinGameId: Int? = null
    
    private suspend fun waitForConnection(timeoutMs: Long): Boolean = withContext(Dispatchers.IO) {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeoutMs) {
            if (isConnected()) return@withContext true
            delay(200)
        }
        return@withContext isConnected()
    }
    
    suspend fun initialize(hubUrl: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            android.util.Log.d("SignalR", "Creating hub connection to: $hubUrl/gameHub")
        
            hubConnection = HubConnectionBuilder.create("$hubUrl/gameHub")
                .withTransport(TransportEnum.WEBSOCKETS)
                .withAccessTokenProvider(Single.create { emitter ->
                    try {
                        val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                        val task = firebaseUser?.getIdToken(false)
                        if (task == null) {
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

            hubConnection?.onClosed { error: Exception? ->
                android.util.Log.d("SignalR", "Connection closed: ${error?.message}")

                
                GlobalScope.launch(Dispatchers.IO) {
                    var attempt = 0
                    val maxAttempts = 5
                    while (attempt < maxAttempts && !isConnected()) {
                        attempt++
                        val delayMs = (1000L * attempt * 2)
                        android.util.Log.d("SignalR", "Reconnect attempt $attempt in ${delayMs}ms")
                        delay(delayMs)
                        try {
                            hubConnection?.start()
                            val ok = waitForConnection(8000)
                            if (ok) {
                                android.util.Log.d("SignalR", "Reconnected successfully on attempt $attempt")
                                _connectionState.value = HubConnectionState.CONNECTED
                                break
                            }
                        } catch (re: Exception) {
                            android.util.Log.w("SignalR", "Reconnect attempt $attempt failed", re)
                        }
                    }
                    if (!isConnected()) {
                        android.util.Log.e("SignalR", "Failed to reconnect after $maxAttempts attempts")
                        _connectionState.value = HubConnectionState.DISCONNECTED
                    }
                }
            }

            hubConnection?.setServerTimeout(30000)
            hubConnection?.setKeepAliveInterval(15000)
            
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

            val connected = waitForConnection(8000)
            if (!connected) {
                android.util.Log.e("SignalR", "Connection start failed or timed out (poll)")
                return@withContext Result.failure(Exception("Connection timeout"))
            }

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

            try {
                hubConnection?.invoke("FindMatch", playerUid)
                android.util.Log.d("SignalR", "FindMatch invoked (uid)")
            } catch (e: Exception) {
                android.util.Log.e("SignalR", "FindMatch invocation failed", e)
                return@withContext Result.failure(e)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("SignalR", "Failed to find match", e)
            Result.failure(e)
        }
    }

    suspend fun findMatchWithMatchmaking(playerUid: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            android.util.Log.d("SignalR", "Finding match with matchmaking for UID: $playerUid")

            if (!isConnected()) {
                return@withContext Result.failure(Exception("Not connected to SignalR hub"))
            }

            try {
                hubConnection?.invoke("FindMatchWithMatchmaking", playerUid)
                android.util.Log.d("SignalR", "FindMatchWithMatchmaking invoked (uid)")
            } catch (e: Exception) {
                android.util.Log.e("SignalR", "FindMatchWithMatchmaking invocation failed", e)
                return@withContext Result.failure(e)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("SignalR", "Failed to call FindMatchWithMatchmaking", e)
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
                    val future = hubConnection?.invoke("SendAnswer", gameIdInt, playerIdInt, answer)
                    try {
                        (future as? java.util.concurrent.CompletableFuture<Any?>)?.get()
                        android.util.Log.d("SignalR", "SendAnswer invoke completed (ints)")
                    } catch (e: Exception) {
                        android.util.Log.e("SignalR", "SendAnswer invocation failed (ints)", e)
                        return@withContext Result.failure(e)
                    }
                } catch (e: Exception) {
                    android.util.Log.w("SignalR", "Could not convert ids to int, falling back to strings", e)
                    val future = hubConnection?.invoke("SendAnswer", gameId, playerId, answer)
                    try {
                        (future as? java.util.concurrent.CompletableFuture<Any?>)?.get()
                        android.util.Log.d("SignalR", "SendAnswer invoke completed (strings)")
                    } catch (ex: Exception) {
                        android.util.Log.e("SignalR", "SendAnswer invocation failed (strings)", ex)
                        return@withContext Result.failure(ex)
                    }
                }
            } else {
                val future = hubConnection?.invoke("SendAnswer", gameId, playerId, answer)
                try {
                    (future as? java.util.concurrent.CompletableFuture<Any?>)?.get()
                    android.util.Log.d("SignalR", "SendAnswer invoke completed (strings)")
                } catch (e: Exception) {
                    android.util.Log.e("SignalR", "SendAnswer invocation failed (strings)", e)
                    return@withContext Result.failure(e)
                }
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

           
            val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            if (firebaseUser == null) {
                android.util.Log.e("SignalR", "Cannot join game: no authenticated Firebase user")
                return@withContext Result.failure(Exception("Authentication required to join game"))
            }

            if (!isConnected()) {
                return@withContext Result.failure(Exception("Not connected to SignalR hub"))
            }

            try {
                 
                lastRequestedJoinGameId = gameId

                try {
                    if (password != null) {
                        hubConnection?.invoke("JoinGame", gameId, password)
                    } else {
                        hubConnection?.invoke("JoinGame", gameId)
                    }
                    android.util.Log.d("SignalR", "JoinGame invoked for gameId=$gameId")
                    Result.success(Unit)
                } catch (e: Exception) {
                    android.util.Log.e("SignalR", "JoinGame invocation failed", e)
                    Result.failure(e)
                }
            } catch (e: Exception) {
                android.util.Log.e("SignalR", "JoinGame invocation failed", e)
                Result.failure(e)
            }
        } catch (e: Exception) {
            android.util.Log.e("SignalR", "Failed to join game", e)
            Result.failure(e)
        }
    }

    suspend fun usePowerUp(gameId: String, playerId: String, powerUpType: Int): Result<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                android.util.Log.d("SignalR", "⚡ Using PowerUp")
                android.util.Log.d("SignalR", "⚡ GameId: $gameId")
                android.util.Log.d("SignalR", "⚡ PlayerId: $playerId")
                android.util.Log.d("SignalR", "⚡ PowerUp: $powerUpType")

                if (!isConnected()) {
                    return@withContext Result.failure(Exception("Not connected to SignalR hub"))
                }

                val gameIdNumeric = gameId.toDoubleOrNull()
                val playerIdNumeric = playerId.toDoubleOrNull()

                try {
                    if (gameIdNumeric != null && playerIdNumeric != null) {
                        val future = hubConnection?.invoke(
                            "UsePowerUp",
                            gameIdNumeric.toInt(),
                            playerIdNumeric.toInt(),
                            powerUpType
                        )
                        (future as? java.util.concurrent.CompletableFuture<Any?>)?.get()
                    } else {
                        val future = hubConnection?.invoke(
                            "UsePowerUp",
                            gameId,
                            playerId,
                            powerUpType
                        )
                        (future as? java.util.concurrent.CompletableFuture<Any?>)?.get()
                    }

                    android.util.Log.d("SignalR", "⚡ UsePowerUp invoke completed")
                    Result.success(Unit)

                } catch (e: Exception) {
                    android.util.Log.e("SignalR", "❌ UsePowerUp failed", e)
                    Result.failure(e)
                }

            } catch (e: Exception) {
                android.util.Log.e("SignalR", "❌ Error using power-up", e)
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

    suspend fun leaveGame(gameId: String, playerId: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            android.util.Log.d("SignalR", "Leaving game: gameId=$gameId, playerId=$playerId")

            if (!isConnected()) {
                android.util.Log.d("SignalR", "Hub not connected, nothing to leave")
                return@withContext Result.success(Unit)
            }

            try {
                val gameIdNum = gameId.toDoubleOrNull()?.toInt()
                val playerIdNum = playerId.toDoubleOrNull()?.toInt()
                if (gameIdNum != null && playerIdNum != null) {
                    val future = hubConnection?.invoke("LeaveGame", gameIdNum, playerIdNum)
                    (future as? java.util.concurrent.CompletableFuture<Any?>)?.get()
                } else {
                    val future = hubConnection?.invoke("LeaveGame", gameId, playerId)
                    (future as? java.util.concurrent.CompletableFuture<Any?>)?.get()
                }
                android.util.Log.d("SignalR", "LeaveGame invoked on server (if implemented)")
            } catch (invokeEx: Exception) {
                android.util.Log.w("SignalR", "LeaveGame invoke failed or not implemented on server", invokeEx)
            }

            try {
                hubConnection?.stop()
                _connectionState.value = HubConnectionState.DISCONNECTED
                android.util.Log.d("SignalR", "Hub connection stopped locally")
            } catch (stopEx: Exception) {
                android.util.Log.w("SignalR", "Error stopping hub connection", stopEx)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("SignalR", "Failed to leave game", e)
            Result.failure(e)
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