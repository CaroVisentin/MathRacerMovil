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
    
    fun startConnection(playerUid: String, displayName: String) {
        if (_uiState.value.isConnecting) return
        
        android.util.Log.d("WaitingOpponentViewModel", "Starting connection for uid: $playerUid, displayName: $displayName")
        
        _uiState.value = _uiState.value.copy(
            isConnecting = true,
            playerUid = playerUid,
            playerName = displayName,
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
                    
                    // Auto buscar partida después de conectar (send UID to server)
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
        val playerUid = _uiState.value.playerUid
        if (playerUid.isBlank()) {
            android.util.Log.e("WaitingOpponentViewModel", "Player UID is blank")
            return
        }

        android.util.Log.d("WaitingOpponentViewModel", "Finding match for uid: $playerUid")

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSearchingMatch = true)

            findMatchUseCase(playerUid).fold(
                onSuccess = {
                    android.util.Log.d("WaitingOpponentViewModel", "FindMatch request sent successfully (uid)")
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
                // Verificar si soy parte de este juego usando UID (más fiable que nombre)
                val currentPlayerUid = _uiState.value.playerUid
                val isMyGame = game.playerOne.id == currentPlayerUid ||
                               game.playerTwo?.id == currentPlayerUid

                android.util.Log.d("WaitingOpponentViewModel", "WAITING_FOR_PLAYERS - isMyGame: $isMyGame, currentUid: $currentPlayerUid, playerOneId: ${game.playerOne.id}, playerTwoId: ${game.playerTwo?.id}")

                if (isMyGame) {
                    _uiState.value = _uiState.value.copy(
                        isSearchingMatch = true,
                        message = "Esperando oponente...",
                        gameId = game.id
                    )
                }
            }
            
            GameStatus.IN_PROGRESS -> {
                // Verificar si soy parte de este juego usando UID
                val currentPlayerUid = _uiState.value.playerUid
                val isMyGame = game.playerOne.id == currentPlayerUid ||
                               game.playerTwo?.id == currentPlayerUid

                android.util.Log.d("WaitingOpponentViewModel", "IN_PROGRESS - isMyGame: $isMyGame, hasQuestion: ${game.currentQuestion != null}, currentUid: $currentPlayerUid, playerOneId: ${game.playerOne.id}, playerTwoId: ${game.playerTwo?.id}")

                if (isMyGame && game.currentQuestion != null) {
                    val opponentName = if (game.playerOne.id == currentPlayerUid) {
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
                    android.util.Log.d("WaitingOpponentViewModel", "NOT navigating - isMyGame: $isMyGame, currentUid: $currentPlayerUid, playerOne: ${game.playerOne.name}, playerTwo: ${game.playerTwo?.name}")
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