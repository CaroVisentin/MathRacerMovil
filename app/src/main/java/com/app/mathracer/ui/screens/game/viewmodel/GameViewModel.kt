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
                    
                    
                    val myPlayer = when {
                        game.playerOne.name.equals(myPlayerName, ignoreCase = true) -> game.playerOne
                        game.playerTwo?.name?.equals(myPlayerName, ignoreCase = true) == true -> game.playerTwo
                         
                        currentState.myPlayerId != null && game.playerOne.id == currentState.myPlayerId -> game.playerOne
                        currentState.myPlayerId != null && game.playerTwo?.id == currentState.myPlayerId -> game.playerTwo
                        else -> null
                    }

                    
                    val opponent = when (myPlayer) {
                        null -> if (game.playerOne.name.equals(myPlayerName, ignoreCase = true)) game.playerTwo else game.playerOne
                        game.playerOne -> game.playerTwo
                        else -> game.playerOne
                    }
                    
                    android.util.Log.d("GameViewModel", "🎮 Found myPlayer: ${myPlayer?.name} (score: ${myPlayer?.score})")
                    android.util.Log.d("GameViewModel", "🎮 Found opponent: ${opponent?.name} (score: ${opponent?.score})")
                    android.util.Log.d("GameViewModel", "🎮 Current question: '${game.currentQuestion?.text}'")
                   
                    val newQuestion = game.currentQuestion?.text ?: ""
                    val hasNewQuestion = newQuestion.isNotEmpty() && newQuestion != currentState.currentQuestion
                    
                    if (hasNewQuestion) {
                        android.util.Log.d("GameViewModel", "🆕 New question detected: '$newQuestion'")
                    }
                    
                    // Por ahora lo puse asi nomas, dps se puede mejorar
                    val myPlayerScore = myPlayer?.score ?: 0
                    val opponentScore = opponent?.score ?: 0
                    
                   
                    val myPlayerPosition = minOf(myPlayerScore, 10)
                    val opponentPosition = minOf(opponentScore, 10)
                    
                    
                    val gameFinished = myPlayerPosition >= 10 || opponentPosition >= 10
                    val winner = when {
                        myPlayerPosition >= 10 -> "¡Ganaste!"
                        opponentPosition >= 10 -> "Perdiste"
                        else -> null
                    }
                    
                    android.util.Log.d("GameViewModel", "🏁 Positions - Me: $myPlayerPosition/10, Opponent: $opponentPosition/10, GameFinished: $gameFinished")
                     
                    _uiState.value = currentState.copy(
                        isLoading = false,
                        playerScore = myPlayerScore,
                        opponentScore = opponentScore,
                        currentQuestion = game.currentQuestion?.text ?: "",
                        options = game.currentQuestion?.options ?: emptyList(),
                        correctAnswer = game.currentQuestion?.correctAnswer,
                        playerProgress = myPlayerPosition,
                        opponentProgress = opponentPosition,
                        myPlayerId = myPlayer?.id ?: currentState.myPlayerId,
                        opponentName = opponent?.name ?: "Oponente",
                        gameEnded = gameFinished || currentState.gameEnded,
                        winner = winner ?: currentState.winner,
                        
                        showFeedback = if (hasNewQuestion) false else currentState.showFeedback,
                        selectedOption = if (hasNewQuestion) null else currentState.selectedOption,
                        isLastAnswerCorrect = if (hasNewQuestion) null else currentState.isLastAnswerCorrect,
                        
                        isPenalized = currentState.isPenalized,
                        expectedResult = game.expectedResult ?: "" // <-- NUEVO
                    )
                    
                    if (hasNewQuestion) {
                        android.util.Log.d("GameViewModel", "✅ New question loaded and UI updated!")
                    }
                    
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
                        opponentScore = game.playerTwo?.score ?: 0,
                        expectedResult = currentState.expectedResult ?: "" // <-- NUEVO
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
    
    fun submitAnswer(selectedOption: Int?) {
        val currentState = _uiState.value
        
        if (currentState.gameId.isBlank() || currentState.myPlayerId == null) return
        
        viewModelScope.launch {
             
            val isCorrect = selectedOption == currentState.correctAnswer
            
            android.util.Log.d("GameViewModel", "🎯 Checking answer: selected=$selectedOption, correct=${currentState.correctAnswer}, isCorrect=$isCorrect")
            
            
            _uiState.value = currentState.copy(
                selectedOption = selectedOption,
                showFeedback = true,
                isLastAnswerCorrect = isCorrect
            )
            
           
            android.util.Log.d("GameViewModel",
                "🎯 Submitting answer - GameId: '${currentState.gameId}', PlayerId: '${currentState.myPlayerId}', Answer: '${selectedOption ?: "null"}'")
            
            val answerInt = selectedOption
            if (answerInt == null) {
                _uiState.value = _uiState.value.copy(
                    error = "La opción seleccionada no es un número válido",
                    showFeedback = false,
                    selectedOption = null
                )
                return@launch
            }
            
             
            val result = submitAnswerUseCase(
                gameId = currentState.gameId,
                playerId = currentState.myPlayerId!!,
                answer = answerInt
            )
            
            result.fold(
                onSuccess = { answerResult ->
                    android.util.Log.d("GameViewModel", "📤 Answer sent successfully to server")
                    
                    if (isCorrect) {
                        android.util.Log.d("GameViewModel", "✅ Correct answer! Applying optimistic local progress and waiting for server update...")
                        
                        val current = _uiState.value
                        val newScore = current.playerScore + 1
                        val newProgress = minOf(newScore, 10)
                        val reachedEnd = newProgress >= 10

                        _uiState.value = current.copy(
                            playerScore = newScore,
                            playerProgress = newProgress,
                            gameEnded = reachedEnd || current.gameEnded,
                            winner = if (reachedEnd) "¡Ganaste!" else current.winner
                        )
                    } else {
                        android.util.Log.d("GameViewModel", "❌ Wrong answer. Applying penalty, waiting for server to send new question...")
                        _uiState.value = _uiState.value.copy(
                            isPenalized = true
                        )
                        kotlinx.coroutines.delay(1000)
                        _uiState.value = _uiState.value.copy(
                            isPenalized = false
                        )
                     
                        prepareForNextQuestion()
                    }
                },
                onFailure = { exception ->
                    android.util.Log.e("GameViewModel", "❌ Failed to send answer: ${exception.message}")
                    _uiState.value = _uiState.value.copy(
                        selectedOption = null,
                        showFeedback = false,
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
    
    fun prepareForNextQuestion() {
        val currentState = _uiState.value
        android.util.Log.d("GameViewModel", "🔄 Preparing for next question...")
        if (_uiState.value.gameEnded) return
        _uiState.value = _uiState.value.copy(
            selectedOption = null,
            showFeedback = false,
            isLastAnswerCorrect = false
        )
        
        android.util.Log.d("GameViewModel", "🔄 Cleared current question, waiting for server update...")
    }
}