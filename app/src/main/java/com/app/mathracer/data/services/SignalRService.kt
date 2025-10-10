package com.app.mathracer.data.services

import android.util.Log
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import com.microsoft.signalr.HubConnectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.gson.annotations.SerializedName
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignalRService @Inject constructor() {
    
    private var hubConnection: HubConnection? = null
    private val gson = Gson()
    
    private val _connectionState = MutableStateFlow(HubConnectionState.DISCONNECTED)
    val connectionState: StateFlow<HubConnectionState> = _connectionState.asStateFlow()
    
    private val _gameEvents = MutableStateFlow<GameEvent?>(null)
    val gameEvents: StateFlow<GameEvent?> = _gameEvents.asStateFlow()
    
    fun initialize(hubUrl: String) {
        Log.d("SignalRService", "🚀 Inicializando conexión a: $hubUrl")
        if (hubConnection == null) {
            hubConnection = HubConnectionBuilder.create(hubUrl)
                .shouldSkipNegotiate(false)
                .withHandshakeResponseTimeout(30000)
                .build()
                
            setupEventHandlers()
            Log.d("SignalRService", "✅ Conexión inicializada correctamente")
        } else {
            Log.d("SignalRService", "⚠️ Conexión ya existe, reinicializando handlers...")
            // Re-configurar handlers por si acaso
            setupEventHandlers()
        }
    }
    
    private fun setupEventHandlers() {
        hubConnection?.let { connection ->
            Log.d("SignalRService", "Configurando event handlers...")
            
            // Probar recibir el GameUpdate como objeto directamente (como en HTML)
            try {
                connection.on("GameUpdate", 
                    { gameUpdateObject: com.google.gson.JsonObject ->
                        try {
                            val gameUpdateJson = gameUpdateObject.toString()
                            Log.d("SignalRService", "🎯 GameUpdate recibido (como objeto): $gameUpdateJson")
                            
                            // Parsear el JSON del GameUpdate
                            val gameUpdate = parseGameUpdate(gameUpdateJson)
                            
                            // Emitir un solo evento con toda la información del GameUpdate
                            _gameEvents.value = GameEvent.GameUpdate(gameUpdate)
                            
                            Log.d("SignalRService", "✅ GameUpdate procesado: gameId=${gameUpdate.gameId}, " +
                                    "status=${gameUpdate.status}, players=${gameUpdate.players.size}, " +
                                    "question=${gameUpdate.currentQuestion?.equation}")
                            
                        } catch (e: Exception) {
                            Log.e("SignalRService", "❌ Error procesando GameUpdate (objeto): ${e.message}", e)
                            _gameEvents.value = GameEvent.Error("Error procesando GameUpdate: ${e.message}")
                        }
                    }, 
                    com.google.gson.JsonObject::class.java
                )
                Log.d("SignalRService", "✅ Handler GameUpdate (objeto) registrado")
            } catch (e: Exception) {
                Log.e("SignalRService", "❌ Error registrando handler objeto, probando con String", e)
                
                // Fallback: probar como String
                connection.on("GameUpdate", 
                    { gameUpdateJson: String ->
                        try {
                            Log.d("SignalRService", "🎯 GameUpdate recibido (como string): $gameUpdateJson")
                            
                            // Parsear el JSON del GameUpdate
                            val gameUpdate = parseGameUpdate(gameUpdateJson)
                            
                            // Emitir un solo evento con toda la información del GameUpdate
                            _gameEvents.value = GameEvent.GameUpdate(gameUpdate)
                            
                            Log.d("SignalRService", "✅ GameUpdate procesado: gameId=${gameUpdate.gameId}, " +
                                    "status=${gameUpdate.status}, players=${gameUpdate.players.size}, " +
                                    "question=${gameUpdate.currentQuestion?.equation}")
                            
                        } catch (e: Exception) {
                            Log.e("SignalRService", "❌ Error procesando GameUpdate (string): ${e.message}", e)
                            _gameEvents.value = GameEvent.Error("Error procesando GameUpdate: ${e.message}")
                        }
                    }, 
                    String::class.java
                )
                Log.d("SignalRService", "✅ Handler GameUpdate (string) registrado")
            }
            
            // Evento de error del servidor
            connection.on("Error", 
                { error: String ->
                    Log.e("SignalRService", "🚨 Server error: $error")
                    _gameEvents.value = GameEvent.Error(error)
                }, 
                String::class.java
            )
            
            // Estados de conexión - solo un handler
            connection.onClosed { error ->
                Log.d("SignalRService", "🔌 Conexión cerrada: ${error?.message ?: "Sin error"}")
                _connectionState.value = HubConnectionState.DISCONNECTED
                error?.let {
                    _gameEvents.value = GameEvent.Error("Connection closed: ${it.message}")
                }
            }
            
            Log.d("SignalRService", "✅ Event handlers configurados correctamente")
        }
    }
    
    suspend fun connect(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d("SignalRService", "Intentando conectar...")
            hubConnection?.let { connection ->
                Log.d("SignalRService", "Estado actual: ${connection.connectionState}")
                if (connection.connectionState == HubConnectionState.DISCONNECTED) {
                    _connectionState.value = HubConnectionState.CONNECTING
                    Log.d("SignalRService", "Iniciando conexión...")
                    
                    // Usar blockingAwait en un hilo de I/O para evitar bloquear el hilo principal
                    connection.start().blockingAwait()
                    
                    _connectionState.value = connection.connectionState
                    Log.d("SignalRService", "¡Conexión exitosa! Estado: ${connection.connectionState}")
                } else {
                    Log.d("SignalRService", "Ya estaba conectado")
                }
            } ?: throw Exception("Hub connection not initialized")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SignalRService", "Error de conexión: ${e.message}", e)
            _connectionState.value = HubConnectionState.DISCONNECTED
            Result.failure(e)
        }
    }
    
    suspend fun disconnect() = withContext(Dispatchers.IO) {
        try {
            hubConnection?.let { connection ->
                if (connection.connectionState == HubConnectionState.CONNECTED) {
                    connection.stop().blockingAwait()
                }
            }
            _connectionState.value = HubConnectionState.DISCONNECTED
        } catch (e: Exception) {
            Log.e("SignalRService", "Error en disconnect: ${e.message}")
            _connectionState.value = HubConnectionState.DISCONNECTED
        }
    }
    
    // Métodos basados en tu API del test-liveserver.html
    suspend fun findMatch(playerName: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d("SignalRService", "🎮 Enviando FindMatch para jugador: $playerName")
            hubConnection?.let { connection ->
                connection.invoke("FindMatch", playerName).blockingAwait()
                Log.d("SignalRService", "✅ FindMatch enviado correctamente")
            } ?: throw Exception("Hub connection not initialized")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SignalRService", "❌ Error en FindMatch: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun sendAnswer(gameId: String, playerId: String, answer: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            Log.d("SignalRService", "🎯 Enviando SendAnswer: gameId=$gameId, playerId=$playerId, answer=$answer")
            
            hubConnection?.let { connection ->
                // El backend espera gameId y playerId como Int32, no String
                val gameIdInt = try {
                    gameId.toInt()
                } catch (e: NumberFormatException) {
                    Log.e("SignalRService", "❌ Error: gameId no es un número válido: $gameId", e)
                    throw Exception("Invalid gameId format: $gameId")
                }
                
                val playerIdInt = try {
                    playerId.toInt()
                } catch (e: NumberFormatException) {
                    Log.e("SignalRService", "❌ Error: playerId no es un número válido: $playerId", e)
                    throw Exception("Invalid playerId format: $playerId")
                }
                
                Log.d("SignalRService", "📤 Invocando SendAnswer con: gameId=$gameIdInt (Int), playerId=$playerIdInt (Int), answer=$answer")
                connection.invoke("SendAnswer", gameIdInt, playerIdInt, answer).blockingAwait()
                Log.d("SignalRService", "✅ SendAnswer enviado correctamente")
            } ?: throw Exception("Hub connection not initialized")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SignalRService", "❌ Error en SendAnswer: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun testHealth(): Result<String> {
        return try {
            // Implementar health check si tu backend lo soporta
            Result.success("Connected")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    fun isConnected(): Boolean {
        return hubConnection?.connectionState == HubConnectionState.CONNECTED
    }

    private fun parseGameUpdate(gameUpdateJson: String): GameUpdate {
        try {
            Log.d("SignalRService", "Parseando GameUpdate JSON: $gameUpdateJson")
            
            // Intentar parsear el JSON completo
            val gameUpdate = gson.fromJson(gameUpdateJson, GameUpdate::class.java)
            
            Log.d("SignalRService", "GameUpdate parseado: gameId=${gameUpdate.gameId}, status=${gameUpdate.status}, players=${gameUpdate.players.size}")
            
            return gameUpdate
        } catch (e: JsonSyntaxException) {
            Log.e("SignalRService", "Error de sintaxis JSON: $gameUpdateJson", e)
            
            // Fallback: parseo manual básico si el JSON no coincide exactamente
            return try {
                GameUpdate(
                    gameId = 1001, // Por defecto
                    status = when {
                        gameUpdateJson.contains("WaitingForPlayers") -> "WaitingForPlayers"
                        gameUpdateJson.contains("InProgress") -> "InProgress" 
                        gameUpdateJson.contains("Finished") -> "Finished"
                        else -> "Unknown"
                    },
                    players = emptyList(),
                    currentQuestion = if (gameUpdateJson.contains("InProgress") && gameUpdateJson.contains("equation")) {
                        // Tratar de extraer la ecuación básicamente
                        val equation = extractEquationFromJson(gameUpdateJson)
                        Question(equation, listOf("A", "B", "C", "D")) // Opciones por defecto
                    } else null
                )
            } catch (fallbackError: Exception) {
                Log.e("SignalRService", "Error en fallback parsing: $gameUpdateJson", fallbackError)
                GameUpdate(gameId = 0, status = "Error")
            }
        } catch (e: Exception) {
            Log.e("SignalRService", "Error general parseando GameUpdate: $gameUpdateJson", e)
            return GameUpdate(gameId = 0, status = "Error")
        }
    }
    
    private fun extractEquationFromJson(json: String): String {
        return try {
            // Buscar patrones de ecuación en el JSON
            val equationRegex = """"equation"\s*:\s*"([^"]+)"""".toRegex()
            val match = equationRegex.find(json)
            match?.groupValues?.get(1) ?: "y = x"
        } catch (e: Exception) {
            "y = x" // Ecuación por defecto
        }
    }
}

// Eventos del juego basados en tu backend
sealed class GameEvent {
    data class GameUpdate(val gameUpdate: com.app.mathracer.data.services.GameUpdate) : GameEvent()
    data class WaitingForPlayers(val gameId: String) : GameEvent()
    data class MatchFound(val gameId: String, val player1: String, val player2: String) : GameEvent()
    data class GameStarted(val gameId: String, val questionData: String) : GameEvent()
    data class NewQuestion(val gameId: String, val questionData: String) : GameEvent()
    data class PlayerAnswered(val gameId: String, val playerId: String, val isCorrect: Boolean, val progress: Int) : GameEvent()
    data class GameFinished(val gameId: String, val winnerId: String, val summary: String) : GameEvent()
    data class Error(val message: String) : GameEvent()
}

// Modelos basados en tu backend
data class GameUpdate(
    @SerializedName("gameId") val gameId: Int = 0, // El backend envía como Int
    @SerializedName("status") val status: String = "", // "WaitingForPlayers", "InProgress", "Finished"
    @SerializedName("players") val players: List<Player> = emptyList(),
    @SerializedName("currentQuestion") val currentQuestion: Question? = null,
    @SerializedName("winnerId") val winnerId: Int? = null, // El backend envía como Int
    @SerializedName("conditionToWin") val conditionToWin: Int = 10
)

data class Player(
    @SerializedName("id") val id: Int = 0, // El backend envía como Int
    @SerializedName("name") val name: String = "",
    @SerializedName("correctAnswers") val correctAnswers: Int = 0,
    @SerializedName("position") val position: Int? = null,
    @SerializedName("penaltyUntil") val penaltyUntil: String? = null, // timestamp
    @SerializedName("finishedAt") val finishedAt: String? = null // timestamp
)

data class Question(
    @SerializedName("equation") val equation: String = "",
    @SerializedName("options") val options: List<String> = emptyList()
)