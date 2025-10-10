package com.app.mathracer.ui.screens.waitingOpponent.viewmodel

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
class WaitingOpponentViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WaitingOpponentUiState())
    val uiState: StateFlow<WaitingOpponentUiState> = _uiState.asStateFlow()
    
    init {
        // Observar eventos del juego
        observeGameEvents()
        observeConnectionState()
        
        // Inicializar conexión
        initializeConnection()
    }
    
    private fun initializeConnection() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isConnecting = true)
            
            // Verificar si ya está conectado
            if (gameRepository.connectionState.value == HubConnectionState.CONNECTED) {
                _uiState.value = _uiState.value.copy(
                    isConnecting = false,
                    isConnected = true
                )
                
                // Agregar un pequeño delay para asegurar que los event handlers estén configurados
                kotlinx.coroutines.delay(500) // 500ms para asegurar que todo esté listo
                
                // Buscar partida directamente si ya está conectado
                findMatch()
            } else {
                _uiState.value = _uiState.value.copy(
                    error = "No hay conexión disponible. Inicia desde la pantalla principal."
                )
            }
        }
    }
    
    private fun findMatch() {
        viewModelScope.launch {
            val playerName = "Jugador_${System.currentTimeMillis() % 1000}" // Nombre único
            
            _uiState.value = _uiState.value.copy(
                isSearchingMatch = true,
                playerName = playerName
            )
            
            val result = gameRepository.findMatch(playerName)
            result.fold(
                onSuccess = {
                    // El estado se actualizará a través de los eventos de SignalR
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSearchingMatch = false,
                        error = "Error al buscar partida: ${error.message}"
                    )
                }
            )
        }
    }
    
    private fun observeGameEvents() {
        viewModelScope.launch {
            gameRepository.gameEvents.collect { event ->
                when (event) {
                    is GameEvent.GameUpdate -> {
                        val gameUpdate = event.gameUpdate
                        
                        when (gameUpdate.status) {
                            "WaitingForPlayers" -> {
                                _uiState.value = _uiState.value.copy(
                                    isSearchingMatch = true,
                                    gameId = gameUpdate.gameId.toString(),
                                    opponentFound = false
                                )
                            }
                            
                            "InProgress" -> {
                                // Similar al HTML: si hay al menos 2 jugadores, mostrar oponente
                                if (gameUpdate.players.size >= 2) {
                                    val myPlayerName = _uiState.value.playerName ?: "Jugador"
                                    val otherPlayer = gameUpdate.players.firstOrNull { it.name != myPlayerName } 
                                        ?: gameUpdate.players.getOrNull(1)
                                        ?: gameUpdate.players.firstOrNull()
                                    
                                    _uiState.value = _uiState.value.copy(
                                        isSearchingMatch = false,
                                        opponentFound = true,
                                        opponentName = otherPlayer?.name?.let { "vs $it" } ?: "vs Oponente",
                                        gameId = gameUpdate.gameId.toString(),
                                        gameStarted = true // La partida ya empezó
                                    )
                                } else {
                                    _uiState.value = _uiState.value.copy(
                                        isSearchingMatch = true,
                                        gameId = gameUpdate.gameId.toString()
                                    )
                                }
                            }
                            
                            "Finished" -> {
                                // El juego terminó, pero esto se manejará en GameScreen
                                _uiState.value = _uiState.value.copy(
                                    gameId = gameUpdate.gameId.toString()
                                )
                            }
                        }
                    }
                    
                    is GameEvent.WaitingForPlayers -> {
                        _uiState.value = _uiState.value.copy(
                            isSearchingMatch = true,
                            gameId = event.gameId,
                            opponentFound = false
                        )
                    }
                    
                    is GameEvent.MatchFound -> {
                        _uiState.value = _uiState.value.copy(
                            isSearchingMatch = false,
                            opponentFound = true,
                            opponentName = "vs ${event.player2}",
                            gameId = event.gameId
                        )
                    }
                    
                    is GameEvent.GameStarted -> {
                        _uiState.value = _uiState.value.copy(
                            gameStarted = true,
                            gameId = event.gameId
                        )
                    }
                    
                    is GameEvent.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isSearchingMatch = false,
                            error = event.message
                        )
                    }
                    
                    else -> { /* Otros eventos se manejarán en GameViewModel */ }
                }
            }
        }
    }
    
    private fun observeConnectionState() {
        viewModelScope.launch {
            gameRepository.connectionState.collect { state ->
                _uiState.value = _uiState.value.copy(
                    isConnected = state == HubConnectionState.CONNECTED,
                    isConnecting = state == HubConnectionState.CONNECTING
                )
                
                if (state == HubConnectionState.DISCONNECTED && !_uiState.value.isConnecting) {
                    _uiState.value = _uiState.value.copy(
                        error = "Conexión perdida con el servidor"
                    )
                }
            }
        }
    }
    
    fun cancelSearch() {
        viewModelScope.launch {
            // Implementar cancelación de búsqueda si tu backend lo soporta
            gameRepository.disconnect()
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            gameRepository.disconnect()
        }
    }
}

data class WaitingOpponentUiState(
    val isConnecting: Boolean = false,
    val isConnected: Boolean = false,
    val isSearchingMatch: Boolean = false,
    val opponentFound: Boolean = false,
    val opponentName: String? = null,
    val gameStarted: Boolean = false,
    val gameId: String? = null,
    val playerName: String? = null,
    val error: String? = null
)