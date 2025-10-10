package com.app.mathracer.ui.screens.signalrtest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.data.repository.GameRepository
import com.app.mathracer.data.services.GameEvent
import com.microsoft.signalr.HubConnectionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignalRTestViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val hubUrl: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SignalRTestUiState())
    val uiState: StateFlow<SignalRTestUiState> = _uiState.asStateFlow()
    
    init {
        observeConnectionState()
        observeGameEvents()
    }
    
    private fun observeConnectionState() {
        viewModelScope.launch {
            gameRepository.connectionState.collect { state ->
                _uiState.value = _uiState.value.copy(
                    connectionState = when (state) {
                        HubConnectionState.DISCONNECTED -> "Desconectado"
                        HubConnectionState.CONNECTING -> "Conectando..."
                        HubConnectionState.CONNECTED -> "✅ Conectado"
                        else -> "Estado desconocido"
                    },
                    isConnected = state == HubConnectionState.CONNECTED,
                    isConnecting = state == HubConnectionState.CONNECTING
                )
            }
        }
    }
    
    private fun observeGameEvents() {
        viewModelScope.launch {
            gameRepository.gameEvents.collect { event ->
                when (event) {
                    is GameEvent.WaitingForPlayers -> {
                        addLog("⏳ Esperando más jugadores para la partida: ${event.gameId}")
                    }
                    is GameEvent.MatchFound -> {
                        addLog("🎮 Match encontrado: ${event.gameId} - ${event.player1} vs ${event.player2}")
                    }
                    is GameEvent.GameStarted -> {
                        addLog("🎮 Juego iniciado: ${event.gameId}")
                    }
                    is GameEvent.NewQuestion -> {
                        addLog("🎮 Nueva pregunta: ${event.questionData}")
                    }
                    is GameEvent.PlayerAnswered -> {
                        addLog("🎮 Jugador respondió: ${event.playerId} - Correcto: ${event.isCorrect}")
                    }
                    is GameEvent.GameFinished -> {
                        addLog("🎮 Juego terminado: Ganador ${event.winnerId}")
                    }
                    is GameEvent.Error -> {
                        addLog("❌ Error: ${event.message}")
                    }
                    else -> {
                        addLog("📡 Evento recibido: ${event?.javaClass?.simpleName}")
                    }
                }
            }
        }
    }
    
    fun testConnection() {
        viewModelScope.launch {
            addLog("🔗 Probando conexión a: $hubUrl")
            _uiState.value = _uiState.value.copy(isConnecting = true)
            
            val result = gameRepository.initializeConnection(hubUrl)
            result.fold(
                onSuccess = {
                    addLog("✅ Conexión exitosa!")
                },
                onFailure = { error ->
                    addLog("❌ Error de conexión: ${error.message}")
                }
            )
        }
    }
    
    fun findMatch() {
        if (!_uiState.value.isConnected) {
            addLog("❌ No hay conexión activa")
            return
        }
        
        viewModelScope.launch {
            val playerName = _uiState.value.playerName.ifEmpty { "TestPlayer_${System.currentTimeMillis()}" }
            addLog("🔍 Buscando partida como: $playerName")
            
            val result = gameRepository.findMatch(playerName)
            result.fold(
                onSuccess = {
                    addLog("✅ Solicitud de partida enviada")
                },
                onFailure = { error ->
                    addLog("❌ Error buscando partida: ${error.message}")
                }
            )
        }
    }
    
    fun disconnect() {
        viewModelScope.launch {
            addLog("🔌 Desconectando...")
            gameRepository.disconnect()
            addLog("✅ Desconectado")
        }
    }
    
    fun updatePlayerName(name: String) {
        _uiState.value = _uiState.value.copy(playerName = name)
    }
    
    fun clearLogs() {
        _uiState.value = _uiState.value.copy(logs = emptyList())
    }
    
    private fun addLog(message: String) {
        val timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())
        val logEntry = "[$timestamp] $message"
        
        _uiState.value = _uiState.value.copy(
            logs = (_uiState.value.logs + logEntry).takeLast(50) // Mantener últimos 50 logs
        )
    }
}

data class SignalRTestUiState(
    val connectionState: String = "Desconectado",
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val playerName: String = "",
    val logs: List<String> = emptyList()
)