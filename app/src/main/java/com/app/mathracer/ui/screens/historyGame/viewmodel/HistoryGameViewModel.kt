package com.app.mathracer.ui.screens.historyGame.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.data.model.SoloGameUpdateResponse
import com.app.mathracer.domain.usecases.ObserveSoloGameUpdatesUseCase
import com.app.mathracer.domain.usecases.StartSoloGameUseCase
import com.app.mathracer.domain.usecases.SubmitSoloAnswerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryGameViewModel @Inject constructor(
    private val startSoloGameUseCase: StartSoloGameUseCase,
    private val observeSoloGameUpdatesUseCase: ObserveSoloGameUpdatesUseCase,
    private val submitSoloAnswerUseCase: SubmitSoloAnswerUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HistoryGameUiState())
    val uiState: StateFlow<HistoryGameUiState> = _uiState.asStateFlow()
    
    private var pollingJob: Job? = null
    
    fun initializeGame(levelId: Int, playerName: String = "Jugador") {
        _uiState.value = _uiState.value.copy(
            playerName = playerName,
            isLoading = true
        )
        startGame(levelId)
    }
    
    private fun startGame(levelId: Int) {
        viewModelScope.launch {
            val result = startSoloGameUseCase(levelId)
            result.fold(
                onSuccess = { gameStart ->
                    android.util.Log.d("HistoryGameViewModel", "✅ Game started: gameId=${gameStart.gameId}, playerId=${gameStart.playerId}")
                    _uiState.value = _uiState.value.copy(
                        gameId = gameStart.gameId,
                        playerId = gameStart.playerId,
                        playerName = gameStart.playerName,
                        totalQuestions = gameStart.totalQuestions,
                        livesRemaining = gameStart.livesRemaining,
                        isLoading = false,
                        currentQuestion = gameStart.currentQuestion?.equation ?: "",
                        options = gameStart.currentQuestion?.options ?: emptyList(),
                        correctAnswer = null // Se obtendrá de la respuesta del servidor al enviar una respuesta
                    )
                    startPolling(gameStart.gameId)
                },
                onFailure = { exception ->
                    android.util.Log.e("HistoryGameViewModel", "❌ Failed to start game", exception)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error al iniciar partida: ${exception.message}"
                    )
                }
            )
        }
    }
    
    private fun startPolling(gameId: Int) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            observeSoloGameUpdatesUseCase(gameId, intervalMs = 2000).collect { update ->
                update?.let { processGameUpdate(it) }
            }
        }
    }
    
    private fun processGameUpdate(update: SoloGameUpdateResponse) {
        android.util.Log.d("HistoryGameViewModel", "🎮 GameUpdate received - Game ID: ${update.gameId}, Status: ${update.gameStatus}")
        
        val currentState = _uiState.value
        
        // Verificar que el gameId coincide
        if (update.gameId != currentState.gameId) {
            android.util.Log.d("HistoryGameViewModel", "🎮 ❌ Game ID mismatch - skipping update")
            return
        }
        
        val newQuestion = update.currentQuestion?.equation ?: ""
        val hasNewQuestion = newQuestion.isNotEmpty() && newQuestion != currentState.currentQuestion
        
        if (hasNewQuestion) {
            android.util.Log.d("HistoryGameViewModel", "🆕 New question detected: '$newQuestion'")
        }
        
        val playerPosition = minOf(update.playerScore, currentState.totalQuestions)
        val machinePosition = minOf(update.machineScore, currentState.totalQuestions)
        
        val gameFinished = update.gameStatus == "Finished" || playerPosition >= currentState.totalQuestions || machinePosition >= currentState.totalQuestions
        val winner = when {
            gameFinished -> {
                when {
                    update.winner == "Player" || playerPosition >= currentState.totalQuestions -> "¡Ganaste!"
                    update.winner == "Machine" || machinePosition >= currentState.totalQuestions -> "Perdiste"
                    else -> null
                }
            }
            else -> null
        }
        
        android.util.Log.d("HistoryGameViewModel", "🏁 Positions - Player: $playerPosition/${currentState.totalQuestions}, Machine: $machinePosition/${currentState.totalQuestions}, GameFinished: $gameFinished")
        
        _uiState.value = currentState.copy(
            isLoading = false,
            playerScore = update.playerScore,
            machineScore = update.machineScore,
            currentQuestion = update.currentQuestion?.equation ?: "",
            options = update.currentQuestion?.options ?: emptyList(),
            correctAnswer = currentState.correctAnswer, // Mantener la respuesta correcta anterior hasta recibir nueva
            playerProgress = playerPosition,
            machineProgress = machinePosition,
            livesRemaining = update.livesRemaining,
            gameEnded = gameFinished || currentState.gameEnded,
            winner = winner ?: currentState.winner,
            expectedResult = update.expectedResult ?: "",
            showFeedback = if (hasNewQuestion) false else currentState.showFeedback,
            selectedOption = if (hasNewQuestion) null else currentState.selectedOption,
            isLastAnswerCorrect = if (hasNewQuestion) null else currentState.isLastAnswerCorrect,
            isPenalized = currentState.isPenalized
        )
        
        if (hasNewQuestion) {
            android.util.Log.d("HistoryGameViewModel", "✅ New question loaded and UI updated!")
        }
        
        if (gameFinished) {
            pollingJob?.cancel()
        }
    }
    
    fun useFireExtinguisher() {
        val currentState = _uiState.value
        if (currentState.fireExtinguisherActive || currentState.gameEnded || currentState.options.isEmpty() || currentState.fireExtinguisherCount <= 0) return

        val correctAnswer = currentState.correctAnswer
        if (correctAnswer == null) return

        val filteredOptions = currentState.options.filter { it == correctAnswer }.take(1) +
                            currentState.options.filter { it != correctAnswer }.shuffled().take(1)

        val shuffledOptions = filteredOptions.shuffled()

        _uiState.value = currentState.copy(
            options = shuffledOptions,
            fireExtinguisherActive = true,
            fireExtinguisherCount = 0
        )
    }

    fun submitAnswer(selectedOption: Int?) {
        val currentState = _uiState.value
        
        if (currentState.gameId == null || currentState.playerId == null) return
        
        viewModelScope.launch {
            // No podemos verificar localmente sin la respuesta correcta del servidor
            // Enviaremos la respuesta y el servidor nos dirá si es correcta
            android.util.Log.d("HistoryGameViewModel", "🎯 Submitting answer: selected=$selectedOption")
            
            _uiState.value = currentState.copy(
                selectedOption = selectedOption,
                showFeedback = false, // Mostraremos feedback después de recibir respuesta del servidor
                isLastAnswerCorrect = null
            )
            
            if (selectedOption == null) {
                _uiState.value = _uiState.value.copy(
                    error = "La opción seleccionada no es un número válido",
                    showFeedback = false,
                    selectedOption = null
                )
                return@launch
            }
            
            val result = submitSoloAnswerUseCase(currentState.gameId!!, selectedOption)
            
            result.fold(
                onSuccess = { answerResult ->
                    android.util.Log.d("HistoryGameViewModel", "📤 Answer sent successfully to server")
                    
                    // Guardar la respuesta correcta para futuras referencias
                    _uiState.value = _uiState.value.copy(
                        correctAnswer = answerResult.correctAnswer
                    )
                    
                    // Verificar si la respuesta fue correcta usando la respuesta del servidor
                    val actuallyCorrect = answerResult.isCorrect
                    
                    // Actualizar el estado con el feedback correcto
                    val current = _uiState.value
                    _uiState.value = current.copy(
                        isLastAnswerCorrect = actuallyCorrect,
                        showFeedback = true,
                        correctAnswer = answerResult.correctAnswer
                    )
                    
                    if (actuallyCorrect) {
                        android.util.Log.d("HistoryGameViewModel", "✅ Correct answer! Applying server response...")
                        
                        val newScore = answerResult.playerScore
                        val newProgress = minOf(newScore, current.totalQuestions)
                        val reachedEnd = newProgress >= current.totalQuestions

                        _uiState.value = current.copy(
                            playerScore = newScore,
                            machineScore = answerResult.machineScore,
                            playerProgress = newProgress,
                            machineProgress = minOf(answerResult.machineScore, current.totalQuestions),
                            gameEnded = reachedEnd || current.gameEnded,
                            winner = if (reachedEnd) "¡Ganaste!" else current.winner,
                            isLastAnswerCorrect = actuallyCorrect,
                            showFeedback = true,
                            correctAnswer = answerResult.correctAnswer
                        )
                    } else {
                        android.util.Log.d("HistoryGameViewModel", "❌ Wrong answer. Applying penalty, waiting for server to send new question...")
                        _uiState.value = _uiState.value.copy(
                            isPenalized = true,
                            isLastAnswerCorrect = actuallyCorrect,
                            showFeedback = true,
                            correctAnswer = answerResult.correctAnswer
                        )
                        delay(1000)
                        _uiState.value = _uiState.value.copy(
                            isPenalized = false
                        )
                        prepareForNextQuestion()
                    }
                },
                onFailure = { exception ->
                    android.util.Log.e("HistoryGameViewModel", "❌ Failed to send answer: ${exception.message}")
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
        android.util.Log.d("HistoryGameViewModel", "🔄 Preparing for next question...")
        if (_uiState.value.gameEnded) return
        _uiState.value = _uiState.value.copy(
            selectedOption = null,
            showFeedback = false,
            isLastAnswerCorrect = false
        )
        
        android.util.Log.d("HistoryGameViewModel", "🔄 Cleared current question, waiting for server update...")
    }
    
    override fun onCleared() {
        super.onCleared()
        pollingJob?.cancel()
    }
}
