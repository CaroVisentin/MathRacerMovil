// This is a noop patch
package com.app.mathracer.ui.screens.historyGame.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.data.model.SoloGameUpdateResponse
import com.app.mathracer.domain.usecases.ObserveSoloGameUpdatesUseCase
import com.app.mathracer.domain.usecases.StartSoloGameUseCase
import com.app.mathracer.domain.usecases.SubmitSoloAnswerUseCase
import com.app.mathracer.domain.usecases.SubmitSoloWildcardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TIMEOUT_SENTINEL_ANSWER = -1

@HiltViewModel
class HistoryGameViewModel @Inject constructor(
    private val startSoloGameUseCase: StartSoloGameUseCase,
    private val observeSoloGameUpdatesUseCase: ObserveSoloGameUpdatesUseCase,
    private val submitSoloAnswerUseCase: SubmitSoloAnswerUseCase
    ,
    private val submitSoloWildcardUseCase: SubmitSoloWildcardUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HistoryGameUiState())
    val uiState: StateFlow<HistoryGameUiState> = _uiState.asStateFlow()

    private var questionTimerJob: Job? = null
    private var feedbackJob: Job? = null

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
                    val w1 = gameStart.availableWildcards.firstOrNull { it.wildcardId == 1 }?.quantity ?: 0
                    val w2 = gameStart.availableWildcards.firstOrNull { it.wildcardId == 2 }?.quantity ?: 0
                    val w3 = gameStart.availableWildcards.firstOrNull { it.wildcardId == 3 }?.quantity ?: 0

                    val carProductId = gameStart.playerProducts
                        ?.firstOrNull { it.productTypeId == 1 } // 1 = Auto
                        ?.productId

                    val trackProductId = gameStart.playerProducts
                        ?.firstOrNull { it.productTypeId == 3 } // 3 = Pista
                        ?.productId

                    _uiState.value = _uiState.value.copy(
                        gameId = gameStart.gameId,
                        playerId = gameStart.playerId,
                        playerName = gameStart.playerName,
                        totalQuestions = gameStart.totalQuestions,
                        livesRemaining = gameStart.livesRemaining,
                        timePerEquation = gameStart.timePerEquation,
                        isLoading = false,
                        currentQuestion = gameStart.currentQuestion?.equation ?: "",
                        options = gameStart.currentQuestion?.options ?: emptyList(),
                        correctAnswer = null,
                        timeLeft = gameStart.timePerEquation,
                        wildcard1Available = w1 > 0,
                        wildcard2Available = w2 > 0,
                        wildcard3Available = w3 > 0,
                        wildcard1Quantity = w1,
                        wildcard2Quantity = w2,
                        wildcard3Quantity = w3,
                        playerCarRes = carProductId ?: 1,
                        playerTrackRes = trackProductId ?: 1
                    )
                    startPolling(gameStart.gameId, gameStart.timePerEquation)
                    startQuestionTimer()
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
    
    private fun startPolling(gameId: Int, timePerEquation: Int) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            observeSoloGameUpdatesUseCase(
                gameId,
                intervalMs = timePerEquation * 1000L.toLong()
            ).collect { update ->
                update?.let { processGameUpdate(it) }
            }
        }
    }

    fun useWildcard(wildcardId: Int) {
        val gid = _uiState.value.gameId ?: return
        val current = _uiState.value

        val isAvailable = when (wildcardId) {
            1 -> current.wildcard1Available
            2 -> current.wildcard2Available
            3 -> current.wildcard3Available
            else -> false
        }
        if (!isAvailable || current.wildcardsLocked) {
            _uiState.value = current.copy(error = "Comodín no disponible")
            return
        }

        viewModelScope.launch {
            try {
                val result = submitSoloWildcardUseCase(gid, wildcardId)
                result.fold(
                    onSuccess = { wc ->
                        val cur = _uiState.value
                        val newOptions = when {
                            !wc.modifiedOptions.isNullOrEmpty() -> wc.modifiedOptions
                            wc.newQuestion != null -> wc.newQuestion.options
                            else -> cur.options
                        }

                        val newQuestionText = wc.newQuestion?.equation ?: cur.currentQuestion

                        val updated = cur.copy(
                            options = newOptions,
                            currentQuestion = newQuestionText,
                            doubleProgressActive = wc.doubleProgressActive,
                            wildcardsLocked = true,
                            wildcard1Quantity = wc.remainingQuantity.takeIf { wildcardId == 1 } ?: cur.wildcard1Quantity,
                            wildcard2Quantity = wc.remainingQuantity.takeIf { wildcardId == 2 } ?: cur.wildcard2Quantity,
                            wildcard3Quantity = wc.remainingQuantity.takeIf { wildcardId == 3 } ?: cur.wildcard3Quantity,
                            wildcard1Available = if (wildcardId == 1) (wc.remainingQuantity > 0) else cur.wildcard1Available,
                            wildcard2Available = if (wildcardId == 2) (wc.remainingQuantity > 0) else cur.wildcard2Available,
                            wildcard3Available = if (wildcardId == 3) (wc.remainingQuantity > 0) else cur.wildcard3Available
                        )

                        _uiState.value = updated
                    },
                    onFailure = { ex ->
                        android.util.Log.e("HistoryGameViewModel", "Error usando wildcard: ${ex.message}")
                        _uiState.value = _uiState.value.copy(error = "Error al usar comodín: ${ex.message}")
                    }
                )
            } catch (e: Exception) {
                android.util.Log.e("HistoryGameViewModel", "Exception usando wildcard: ${e.message}")
                _uiState.value = _uiState.value.copy(error = "Error al usar comodín: ${e.message}")
            }
        }
    }
    
    private fun processGameUpdate(update: SoloGameUpdateResponse) {
        android.util.Log.d("HistoryGameViewModel", "🎮 GameUpdate received - Game ID: ${update.gameId}, Status: ${update.status}")
        
        val currentState = _uiState.value

        if (update.gameId != currentState.gameId) {
            android.util.Log.d("HistoryGameViewModel", "🎮 ❌ Game ID mismatch - skipping update")
            return
        }
        
        val newQuestion = update.currentQuestion?.equation ?: ""
        val hasNewQuestion = newQuestion.isNotEmpty() && newQuestion != currentState.currentQuestion

        if (hasNewQuestion) {
            android.util.Log.d("HistoryGameViewModel", "🆕 New question detected: '$newQuestion'")
        }
        
        val playerPosition = minOf(update.playerPosition, currentState.totalQuestions)
        val machinePosition = minOf(update.machinePosition, currentState.totalQuestions)
        
        val gameFinished = update.status != "InProgress" || playerPosition >= currentState.totalQuestions || machinePosition >= currentState.totalQuestions

        val winner = when {
            gameFinished -> {
                when {
                    playerPosition >= currentState.totalQuestions || update.status  == "PlayerWon" -> "¡Ganaste!"
                    machinePosition >= currentState.totalQuestions || update.status  == "MachineWon" || update.status  == "PlayerLost" -> "Perdiste"
                    else -> null
                }
            }
            else -> null
        }
        
        android.util.Log.d("HistoryGameViewModel", "🏁 Positions - Player: $playerPosition/${currentState.totalQuestions}, Machine: $machinePosition/${currentState.totalQuestions}, GameFinished: $gameFinished")

        val finalPlayerProgress = maxOf(currentState.playerProgress, playerPosition)
        val finalMachineProgress = maxOf(currentState.machineProgress, machinePosition)
        
        _uiState.value = currentState.copy(
            isLoading = false,
            playerScore = update.playerPosition,
            machineScore = update.machinePosition,
            currentQuestion = update.currentQuestion?.equation ?: "",
            options = update.currentQuestion?.options ?: emptyList(),
            correctAnswer = currentState.correctAnswer,
            playerProgress = finalPlayerProgress,
            machineProgress = finalMachineProgress,
            livesRemaining = update.livesRemaining,
            timePerEquation = update.timePerEquation,
            gameEnded = gameFinished || currentState.gameEnded,
            winner = winner ?: currentState.winner,
            isLastAnswerCorrect = if (hasNewQuestion) null else currentState.isLastAnswerCorrect,
            isPenalized = currentState.isPenalized,
            showFeedback = if (hasNewQuestion) false else currentState.showFeedback,
            selectedOption = if (hasNewQuestion) null else currentState.selectedOption,
            canAnswer = if (hasNewQuestion) true else currentState.canAnswer

        )

        if (hasNewQuestion && !_uiState.value.gameEnded) {
            _uiState.value = _uiState.value.copy(wildcardsLocked = false)
        }

        if (hasNewQuestion && !_uiState.value.gameEnded) {
            startQuestionTimer()
        }

        if (gameFinished) {
            pollingJob?.cancel()
            stopQuestionTimer()
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
        if (currentState.gameId == null || currentState.playerId == null || currentState.gameEnded) return

        viewModelScope.launch {
            android.util.Log.d("HistoryGameViewModel", "🎯 Submitting answer: selected=$selectedOption")

            _uiState.value = currentState.copy(
                selectedOption = selectedOption,
                showFeedback = false,
                isLastAnswerCorrect = null,
                canAnswer = false
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

                    _uiState.value = _uiState.value.copy(
                        correctAnswer = answerResult.correctAnswer
                    )

                    val actuallyCorrect = answerResult.isCorrect
                    val before = _uiState.value
                    val newPlayerScore = answerResult.playerScore
                    val newMachineScore = answerResult.machineScore
                    val reachedEnd = newPlayerScore >= before.totalQuestions

                    val newPlayerProgress = maxOf(before.playerProgress, minOf(newPlayerScore, before.totalQuestions))
                    val newMachineProgress = maxOf(before.machineProgress, minOf(newMachineScore, before.totalQuestions))

                    _uiState.value = before.copy(
                        isLastAnswerCorrect = actuallyCorrect,
                        showFeedback = true,
                        playerScore = newPlayerScore,
                        machineScore = newMachineScore,
                        playerProgress = newPlayerProgress,
                        machineProgress = newMachineProgress,
                        gameEnded = reachedEnd || before.gameEnded,
                        winner = if (reachedEnd) "¡Ganaste!" else before.winner,
                        correctAnswer = answerResult.correctAnswer,
                        coinsAwarded = answerResult.coinsEarned ?: before.coinsAwarded,
                        isPenalized = !actuallyCorrect
                    )

                    if (!actuallyCorrect) {
                        android.util.Log.d("HistoryGameViewModel", "❌ Wrong answer. Applying penalty visual...")
                        delay(1000)
                        _uiState.value = _uiState.value.copy(isPenalized = false)
                    }

                    if (_uiState.value.gameEnded) return@fold

                    delay(3000)

                    val gid = _uiState.value.gameId
                    if (gid != null) {
                        try {
                            val update = observeSoloGameUpdatesUseCase(gid, intervalMs = 0L).firstOrNull()
                            update?.let { processGameUpdate(it) }
                        } catch (e: Exception) {
                            android.util.Log.e("HistoryGameViewModel", "❌ Failed to fetch next question: ${e.message}", e)
                        }
                    }

                    _uiState.value = _uiState.value.copy(
                        selectedOption = null,
                        showFeedback = false,
                        isLastAnswerCorrect = null,
                        canAnswer = true
                    )
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

    private fun startQuestionTimer() {
        questionTimerJob?.cancel()
        val total = _uiState.value.timePerEquation

        _uiState.value = _uiState.value.copy(
            timeLeft = total,
            canAnswer = true
        )

        questionTimerJob = viewModelScope.launch {
            while (_uiState.value.timeLeft > 0 && _uiState.value.canAnswer && !_uiState.value.gameEnded) {
                delay(1000)
                _uiState.value = _uiState.value.copy(timeLeft = _uiState.value.timeLeft - 1)
            }

            if (_uiState.value.timeLeft <= 0 && _uiState.value.canAnswer && !_uiState.value.gameEnded) {
                onTimeExpired()
            }
        }
    }

    private fun stopQuestionTimer() {
        questionTimerJob?.cancel()
    }

    private fun onTimeExpired() {
        stopQuestionTimer()
        val state = _uiState.value
        val gid = state.gameId ?: return

        viewModelScope.launch {
            val result = runCatching { submitSoloAnswerUseCase(gid, TIMEOUT_SENTINEL_ANSWER) }

            result.fold(
                onSuccess = { answerResult ->
                    _uiState.value = _uiState.value.copy(
                        isLastAnswerCorrect = false,
                        showFeedback = true,
//                        correctAnswer = answerResult.correctAnswer, // por si vuelve
//                        playerScore = answerResult.playerScore,
//                        machineScore = answerResult.machineScore,
//                        playerProgress = minOf(answerResult.playerScore, state.totalQuestions),
//                        machineProgress = minOf(answerResult.machineScore, state.totalQuestions),
                        livesRemaining = _uiState.value.livesRemaining,
                        canAnswer = false,
                        isPenalized = true
                    )

                    delay(1000)
                    _uiState.value = _uiState.value.copy(isPenalized = false)

                    val remaining = 3000L - 1000L
                    if (remaining > 0) delay(remaining)

                    requestNextQuestionOnce()
                    _uiState.value = _uiState.value.copy(
                        selectedOption = null,
                        showFeedback = false,
                        isLastAnswerCorrect = null,
                        canAnswer = true
                    )
                    if (!_uiState.value.gameEnded) startQuestionTimer()
                },
                onFailure = {
                    _uiState.value = _uiState.value.copy(
                        isLastAnswerCorrect = false,
                        showFeedback = true,
                        canAnswer = false,
                        isPenalized = true
                    )
                    delay(1000)
                    _uiState.value = _uiState.value.copy(isPenalized = false)
                    delay(2000)

                    requestNextQuestionOnce()
                    _uiState.value = _uiState.value.copy(
                        selectedOption = null,
                        showFeedback = false,
                        isLastAnswerCorrect = null,
                        canAnswer = true
                    )
                    if (!_uiState.value.gameEnded) startQuestionTimer()
                }
            )
        }
    }

    private fun showFeedbackAndLoadNext() {
        feedbackJob?.cancel()
        feedbackJob = viewModelScope.launch {
            delay(3000)

            requestNextQuestionOnce()

            _uiState.value = _uiState.value.copy(
                selectedOption = null,
                showFeedback = false,
                isLastAnswerCorrect = null,
                canAnswer = true
            )

            if (!_uiState.value.gameEnded) {
                startQuestionTimer()
            }
        }
    }

    private suspend fun requestNextQuestionOnce() {
        val gid = _uiState.value.gameId ?: return
        try {
            val update = observeSoloGameUpdatesUseCase(gid, intervalMs = 0L).firstOrNull()
            update?.let { processGameUpdate(it) }
        } catch (e: Exception) {
            android.util.Log.e("HistoryGameViewModel", "❌ Error al obtener siguiente pregunta: ${e.message}", e)
        }
    }
}
