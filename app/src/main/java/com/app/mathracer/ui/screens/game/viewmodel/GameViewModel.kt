package com.app.mathracer.ui.screens.game.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.domain.models.Game
import com.app.mathracer.domain.models.GameStatus
import com.app.mathracer.domain.usecases.ObserveGameUpdatesUseCase
import com.app.mathracer.domain.usecases.SubmitAnswerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val observeGameUpdatesUseCase: ObserveGameUpdatesUseCase,
    private val submitAnswerUseCase: SubmitAnswerUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    
    fun initializeGame(gameId: String, playerName: String = "Jugador") {
        _uiState.value = _uiState.value.copy(
            gameId = gameId,
            playerName = playerName
        )
        observeGameEvents()
    }
    
    private fun observeGameEvents() {
        viewModelScope.launch {
            observeGameUpdatesUseCase().collect { game ->
                game?.let { processGameUpdate(it) }
            }
        }
    }
    
    private fun processGameUpdate(game: Game) {
        android.util.Log.d("GameViewModel", "🎮 GameUpdate received - Game ID: '${game.id}', Current game ID: '${_uiState.value.gameId}'")
        android.util.Log.d("GameViewModel", "🎮 IDs match: ${game.id == _uiState.value.gameId}")

        val gameId = game.id.toDoubleOrNull()?.toInt()?.toString() ?: game.id
        val currentGameId = _uiState.value.gameId.toDoubleOrNull()?.toInt()?.toString() ?: _uiState.value.gameId
        
        android.util.Log.d("GameViewModel", "🎮 Normalized - Game ID: '$gameId', Current: '$currentGameId'")
        
        if (gameId == currentGameId) {
            android.util.Log.d("GameViewModel", "🎮 Processing game update for game $gameId, status: ${game.status}")
            when (game.status) {
                GameStatus.IN_PROGRESS -> {
                    android.util.Log.d("GameViewModel", "🎮 Processing IN_PROGRESS status")
                    val currentState = _uiState.value
                    val myPlayerName = currentState.playerName
                    
                    android.util.Log.d("GameViewModel", "🎮 Looking for player: '$myPlayerName'")
                    android.util.Log.d("GameViewModel", "🎮 Player 1: '${game.playerOne.name}', Player 2: '${game.playerTwo?.name}'")
                    
                    // Encontrar mi jugador basado en el nombre
                    val myPlayer = if (game.playerOne.name == myPlayerName) {
                        game.playerOne
                    } else {
                        game.playerTwo
                    }
                    
                    // El oponente es el otro jugador
                    val opponent = if (game.playerOne.name == myPlayerName) {
                        game.playerTwo
                    } else {
                        game.playerOne
                    }
                    
                    android.util.Log.d("GameViewModel", "🎮 Found myPlayer: ${myPlayer?.name} (score: ${myPlayer?.score})")
                    android.util.Log.d("GameViewModel", "🎮 Found opponent: ${opponent?.name} (score: ${opponent?.score})")
                    android.util.Log.d("GameViewModel", "🎮 Current question: '${game.currentQuestion?.text}'")
                    
                    // Actualizar UI state
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        playerScore = myPlayer?.score ?: 0,
                        opponentScore = opponent?.score ?: 0,
                        currentQuestion = game.currentQuestion?.text ?: "",
                        options = game.currentQuestion?.options ?: emptyList(),
                        correctAnswer = game.currentQuestion?.correctAnswer,
                        playerProgress = myPlayer?.score ?: 0,
                        opponentProgress = opponent?.score ?: 0,
                        myPlayerId = myPlayer?.id,
                        opponentName = opponent?.name ?: "Oponente"
                    )
                    
                    android.util.Log.d("GameViewModel", "🎮 ✅ UI State updated successfully!")
                }
                GameStatus.FINISHED -> {
                    val currentState = _uiState.value
                    val winner = game.winner
                    val isWinner = winner?.name == currentState.playerName
                    
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        gameEnded = true,
                        winner = if (isWinner) "¡Ganaste!" else "Perdiste",
                        playerScore = game.playerOne.score,
                        opponentScore = game.playerTwo?.score ?: 0
                    )
                }
                GameStatus.WAITING_FOR_PLAYERS -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = true
                    )
                }
            }
        } else {
            android.util.Log.d("GameViewModel", "🎮 ❌ Game ID mismatch - skipping update")
        }
    }
    
    fun submitAnswer(selectedOption: String) {
        val currentState = _uiState.value
        
        if (currentState.gameId.isBlank() || currentState.myPlayerId == null) return
        
        viewModelScope.launch {
            // Marcar opción como seleccionada
            _uiState.value = currentState.copy(
                selectedOption = selectedOption,
                showFeedback = false
            )
            
            android.util.Log.d("GameViewModel", "🎯 Submitting answer - GameId: '${currentState.gameId}' (${currentState.gameId::class.java.simpleName}), PlayerId: '${currentState.myPlayerId}' (${currentState.myPlayerId!!::class.java.simpleName}), Answer: '$selectedOption' (${selectedOption::class.java.simpleName})")
            
            val result = submitAnswerUseCase(
                gameId = currentState.gameId,
                playerId = currentState.myPlayerId!!,
                answer = selectedOption
            )
            
            result.fold(
                onSuccess = { answerResult ->
                    // Mostrar feedback visual
                    _uiState.value = _uiState.value.copy(
                        showFeedback = true,
                        isLastAnswerCorrect = answerResult.isCorrect,
                        correctAnswer = answerResult.correctAnswer
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = "Error al enviar respuesta: ${exception.message}"
                    )
                }
            )
        }
    }

    fun clearFeedback() {
        _uiState.value = _uiState.value.copy(
            showFeedback = false,
            selectedOption = null,
            isLastAnswerCorrect = null
        )
    }
}