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
import com.app.mathracer.data.network.RetrofitClient
import kotlinx.coroutines.delay

@HiltViewModel
class WaitingOpponentViewModel @Inject constructor(
    private val initializeGameConnectionUseCase: InitializeGameConnectionUseCase,
    private val findMatchUseCase: FindMatchUseCase,
    private val observeGameUpdatesUseCase: ObserveGameUpdatesUseCase,
    private val getLastRequestedGameIdUseCase: com.app.mathracer.domain.usecases.GetLastRequestedGameIdUseCase,
    private val clearLastRequestedGameIdUseCase: com.app.mathracer.domain.usecases.ClearLastRequestedGameIdUseCase
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
        
        val expectedId = getLastRequestedGameIdUseCase()
        if (expectedId != null) {
            android.util.Log.d("WaitingOpponentViewModel", "startConnection: found expectedId=$expectedId, storing in uiState")
            _uiState.value = _uiState.value.copy(
                gameId = expectedId.toString(),
                isSearchingMatch = true,
                message = "Esperando oponente..."
            )
        }
        
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

    fun setExpectedGameId(gameId: String) {
        try {
            _uiState.value = _uiState.value.copy(
                gameId = gameId,
                isSearchingMatch = true,
                message = "Esperando oponente..."
            )
            android.util.Log.d("WaitingOpponentViewModel", "setExpectedGameId: stored expected gameId=$gameId in uiState")
        } catch (e: Exception) {
            android.util.Log.w("WaitingOpponentViewModel", "setExpectedGameId failed: ${e.message}")
        }
    }

    private fun startPollingGameStatus(gameId: Int) {
        android.util.Log.d("WaitingOpponentViewModel", "Starting poll for game status: $gameId")
        viewModelScope.launch {
            try {
                val api = RetrofitClient.api
                var attempts = 0
                val maxAttempts = 20
                while (attempts < maxAttempts) {
                    attempts++
                    try {
                        val resp = api.getGameById(gameId)
                        if (resp.isSuccessful) {
                            val body = resp.body()
                            if (body != null) {
                                android.util.Log.d("WaitingOpponentViewModel", "Polled game status: ${body.status}, players: ${body.currentPlayers}/${body.maxPlayers}")
                                val startedByPlayers = body.currentPlayers >= body.maxPlayers
                                val statusText = body.status ?: ""
                                val looksStarted = startedByPlayers || !(statusText.contains("espera", ignoreCase = true) || statusText.contains("esperando", ignoreCase = true))
                                if (looksStarted) {
                                    android.util.Log.d("WaitingOpponentViewModel", "Poll detected game started for $gameId")
                                    _uiState.value = _uiState.value.copy(
                                        isSearchingMatch = false,
                                        gameFound = true,
                                        message = "¡Oponente encontrado! Iniciando juego...",
                                        opponentName = _uiState.value.opponentName
                                    )
                                    _navigationEvent.value = NavigationEvent.NavigateToGame(gameId.toString())
                                    return@launch
                                }
                            }
                        } else {
                            android.util.Log.w("WaitingOpponentViewModel", "Poll getGameById failed: ${resp.code()}")
                        }
                    } catch (ex: Exception) {
                        android.util.Log.w("WaitingOpponentViewModel", "Error polling game status", ex)
                    }
                    delay(1000)
                }
                android.util.Log.w("WaitingOpponentViewModel", "Polling finished without detecting start for game $gameId")
            } catch (e: Exception) {
                android.util.Log.e("WaitingOpponentViewModel", "Polling loop failed", e)
            }
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
                val assignedId = _uiState.value.assignedPlayerId
                val pname = _uiState.value.playerName

                val isMyGame = when {
                    !assignedId.isNullOrBlank() -> (game.playerOne.id == assignedId || game.playerTwo?.id == assignedId)
                    pname.isNotBlank() -> (game.playerOne.name == pname || game.playerTwo?.name == pname)
                    else -> false
                }

                android.util.Log.d("WaitingOpponentViewModel", "WAITING_FOR_PLAYERS - isMyGame: $isMyGame, assignedId: $assignedId, playerName: $pname, playerOneId: ${game.playerOne.id}, playerTwoId: ${game.playerTwo?.id}")

                if (isMyGame && _uiState.value.assignedPlayerId.isNullOrBlank()) {
                    val matchedId = when {
                        game.playerOne.name == pname -> game.playerOne.id
                        game.playerTwo?.name == pname -> game.playerTwo?.id
                        else -> null
                    }
                    if (!matchedId.isNullOrBlank()) {
                        _uiState.value = _uiState.value.copy(assignedPlayerId = matchedId)
                        android.util.Log.d("WaitingOpponentViewModel", "Stored assignedPlayerId: $matchedId")
                    }
                }

                if (isMyGame) {
                    _uiState.value = _uiState.value.copy(
                        isSearchingMatch = true,
                        message = "Esperando oponente...",
                        gameId = game.id
                    )
                }
            }
            
            GameStatus.IN_PROGRESS -> {
                val assignedId = _uiState.value.assignedPlayerId
                val pname = _uiState.value.playerName

                val isMyGame = when {
                    !assignedId.isNullOrBlank() -> (game.playerOne.id == assignedId || game.playerTwo?.id == assignedId)
                    pname.isNotBlank() -> (game.playerOne.name == pname || game.playerTwo?.name == pname)
                    else -> false
                }

                android.util.Log.d("WaitingOpponentViewModel", "IN_PROGRESS - isMyGame: $isMyGame, hasQuestion: ${game.currentQuestion != null}, assignedId: $assignedId, playerName: $pname, playerOneId: ${game.playerOne.id}, playerTwoId: ${game.playerTwo?.id}")

                 
                if (isMyGame && _uiState.value.assignedPlayerId.isNullOrBlank()) {
                    val matchedId = when {
                        game.playerOne.name == pname -> game.playerOne.id
                        game.playerTwo?.name == pname -> game.playerTwo?.id
                        else -> null
                    }
                    if (!matchedId.isNullOrBlank()) {
                        _uiState.value = _uiState.value.copy(assignedPlayerId = matchedId)
                        android.util.Log.d("WaitingOpponentViewModel", "Stored assignedPlayerId: $matchedId")
                    }
                }

                if (isMyGame) {
                    
                    val amIPlayerOne = if (!assignedId.isNullOrBlank()) {
                        game.playerOne.id == assignedId
                    } else {
                         
                        pname.isNotBlank() && game.playerOne.name == pname
                    }

                    val opponentName = if (amIPlayerOne) {
                        game.playerTwo?.name ?: "Oponente"
                    } else {
                        game.playerOne.name
                    }

                    android.util.Log.d("WaitingOpponentViewModel", "Updating UI for game start - opponent: $opponentName (question present: ${game.currentQuestion != null})")

                    _uiState.value = _uiState.value.copy(
                        isSearchingMatch = false,
                        gameFound = true,
                        message = "¡Oponente encontrado! Iniciando juego...",
                        opponentName = opponentName
                    )

                     
                    android.util.Log.d("WaitingOpponentViewModel", "🚀 NAVIGATING TO GAME - ID: ${game.id}")
                    
                    try {
                        clearLastRequestedGameIdUseCase()
                    } catch (e: Exception) {
                        android.util.Log.w("WaitingOpponentViewModel", "Failed to clear expected game id: ${e.message}")
                    }
                    _navigationEvent.value = NavigationEvent.NavigateToGame(game.id)
                } else {
                    android.util.Log.d("WaitingOpponentViewModel", "NOT navigating - isMyGame: $isMyGame, assignedId: ${_uiState.value.assignedPlayerId}, playerName: ${_uiState.value.playerName}, playerOne: ${game.playerOne.name}, playerTwo: ${game.playerTwo?.name}")
                        if (_uiState.value.isSearchingMatch) {
                            val expectedId = getLastRequestedGameIdUseCase()?.toString()
                            if (expectedId != null) {
                                if (expectedId == game.id) {
                                    android.util.Log.d("WaitingOpponentViewModel", "Fallback: expectedId matches game ${game.id}. Navigating to game.")
                                    try {
                                        clearLastRequestedGameIdUseCase()
                                    } catch (e: Exception) {
                                        android.util.Log.w("WaitingOpponentViewModel", "Failed to clear expected game id: ${e.message}")
                                    }
                                    _uiState.value = _uiState.value.copy(
                                        isSearchingMatch = false,
                                        gameFound = true,
                                        message = "¡Oponente encontrado! Iniciando juego...",
                                        opponentName = game.playerOne.name
                                    )
                                    _navigationEvent.value = NavigationEvent.NavigateToGame(game.id)
                                } else {
                                    android.util.Log.d("WaitingOpponentViewModel", "Fallback: expectedId=$expectedId does not match incoming IN_PROGRESS id=${game.id}; ignoring.")
                                }
                            } else {
                                
                                android.util.Log.d("WaitingOpponentViewModel", "Fallback: no expectedId available; navigating to received IN_PROGRESS game ${game.id}")
                                _uiState.value = _uiState.value.copy(
                                    isSearchingMatch = false,
                                    gameFound = true,
                                    message = "¡Oponente encontrado! Iniciando juego...",
                                    opponentName = game.playerOne.name
                                )
                                _navigationEvent.value = NavigationEvent.NavigateToGame(game.id)
                            }
                        }
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