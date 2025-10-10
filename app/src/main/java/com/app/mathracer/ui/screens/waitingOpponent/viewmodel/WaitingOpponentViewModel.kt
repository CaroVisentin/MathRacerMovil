package com.app.mathracer.ui.screens.waitingOpponent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.domain.models.Game
import com.app.mathracer.domain.models.GameStatus
import com.app.mathracer.domain.usecases.FindMatchUseCase
import com.app.mathracer.domain.usecases.InitializeGameConnectionUseCase
import com.app.mathracer.domain.usecases.ObserveGameUpdatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaitingOpponentViewModel @Inject constructor(
    private val initializeGameConnectionUseCase: InitializeGameConnectionUseCase,
    private val findMatchUseCase: FindMatchUseCase,
    private val observeGameUpdatesUseCase: ObserveGameUpdatesUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(WaitingOpponentUiState())
    val uiState: StateFlow<WaitingOpponentUiState> = _uiState.asStateFlow()
    
    private val _navigationEvent = MutableStateFlow<NavigationEvent?>(null)
    val navigationEvent: StateFlow<NavigationEvent?> = _navigationEvent.asStateFlow()
    
    fun startConnection(playerName: String) {
        if (_uiState.value.isConnecting) return
        
        android.util.Log.d("WaitingOpponentViewModel", "Starting connection for player: $playerName")
        
        _uiState.value = _uiState.value.copy(
            isConnecting = true,
            playerName = playerName,
            error = null,
            message = "Conectando al servidor..."
        )
        
        // Siempre observar eventos desde el inicio
        observeGameEvents()
        
        viewModelScope.launch {
            // Flujo completo: Conexión → Búsqueda de partida
            android.util.Log.d("WaitingOpponentViewModel", "Initializing connection...")
            initializeGameConnectionUseCase().fold(
                onSuccess = {
                    android.util.Log.d("WaitingOpponentViewModel", "Connection successful, finding match...")
                    _uiState.value = _uiState.value.copy(
                        isConnecting = false,
                        isConnected = true,
                        message = "Conectado! Buscando oponente..."
                    )
                    
                    // Auto buscar partida después de conectar
                    findMatch()
                },
                onFailure = { exception ->
                    android.util.Log.e("WaitingOpponentViewModel", "Connection failed: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isConnecting = false,
                        error = "Error de conexión: ${exception.message}"
                    )
                }
            )
        }
    }
    
    private fun findMatch() {
        val playerName = _uiState.value.playerName
        if (playerName.isBlank()) {
            android.util.Log.e("WaitingOpponentViewModel", "Player name is blank")
            return
        }
        
        android.util.Log.d("WaitingOpponentViewModel", "Finding match for player: $playerName")
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearchingMatch = true)
            
            findMatchUseCase(playerName).fold(
                onSuccess = {
                    android.util.Log.d("WaitingOpponentViewModel", "FindMatch request sent successfully")
                    _uiState.value = _uiState.value.copy(
                        message = "Buscando oponente..."
                    )
                },
                onFailure = { exception ->
                    android.util.Log.e("WaitingOpponentViewModel", "FindMatch failed: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        isSearchingMatch = false,
                        error = "Error al buscar partida: ${exception.message}"
                    )
                }
            )
        }
    }
    
    private fun observeGameEvents() {
        android.util.Log.d("WaitingOpponentViewModel", "Starting to observe game events...")
        viewModelScope.launch {
            observeGameUpdatesUseCase().collect { game ->
                if (game != null) {
                    android.util.Log.d("WaitingOpponentViewModel", "Game event received - ID: ${game.id}, Status: ${game.status}")
                    processGameUpdate(game)
                } else {
                    android.util.Log.d("WaitingOpponentViewModel", "Null game event received")
                }
            }
        }
    }
    
    private fun processGameUpdate(game: Game) {
        android.util.Log.d("WaitingOpponentViewModel", "Processing game update - ID: ${game.id}, Status: ${game.status}")
        when (game.status) {
            GameStatus.WAITING_FOR_PLAYERS -> {
                // Verificar si soy parte de este juego
                val currentPlayerName = _uiState.value.playerName
                val isMyGame = game.playerOne.name == currentPlayerName || 
                               game.playerTwo?.name == currentPlayerName
                
                if (isMyGame) {
                    _uiState.value = _uiState.value.copy(
                        isSearchingMatch = true,
                        message = "Esperando oponente...",
                        gameId = game.id
                    )
                }
            }
            
            GameStatus.IN_PROGRESS -> {
                // Verificar si soy parte de este juego
                val currentPlayerName = _uiState.value.playerName
                val isMyGame = game.playerOne.name == currentPlayerName || 
                               game.playerTwo?.name == currentPlayerName
                
                android.util.Log.d("WaitingOpponentViewModel", "IN_PROGRESS - isMyGame: $isMyGame, hasQuestion: ${game.currentQuestion != null}")
                
                if (isMyGame && game.currentQuestion != null) {
                    val opponentName = if (game.playerOne.name == currentPlayerName) {
                        game.playerTwo?.name ?: "Oponente"
                    } else {
                        game.playerOne.name
                    }
                    
                    android.util.Log.d("WaitingOpponentViewModel", "Updating UI for game start - opponent: $opponentName")
                    
                    _uiState.value = _uiState.value.copy(
                        isSearchingMatch = false,
                        gameFound = true,
                        message = "¡Oponente encontrado! Iniciando juego...",
                        opponentName = opponentName
                    )
                    
                    // Navegar inmediatamente al juego
                    android.util.Log.d("WaitingOpponentViewModel", "🚀 NAVIGATING TO GAME - ID: ${game.id}")
                    _navigationEvent.value = NavigationEvent.NavigateToGame(game.id)
                } else {
                    android.util.Log.d("WaitingOpponentViewModel", "NOT navigating - isMyGame: $isMyGame, currentPlayer: $currentPlayerName, playerOne: ${game.playerOne.name}, playerTwo: ${game.playerTwo?.name}")
                }
            }
            
            GameStatus.FINISHED -> {
                // El juego terminó antes de que pudiéramos unirmos
                _uiState.value = _uiState.value.copy(
                    isSearchingMatch = false,
                    error = "El juego terminó inesperadamente"
                )
            }
        }
    }

    fun clearNavigationEvent() {
        _navigationEvent.value = null
    }

}