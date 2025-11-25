package com.app.mathracer.ui.screens.game.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.domain.models.Game
import com.app.mathracer.domain.models.GameStatus
import com.app.mathracer.domain.usecases.ObserveGameUpdatesUseCase
import com.app.mathracer.domain.usecases.SubmitAnswerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import com.app.mathracer.data.repository.UserRemoteRepository
import com.app.mathracer.domain.usecases.UsePowerUpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val observeGameUpdatesUseCase: ObserveGameUpdatesUseCase,
    private val submitAnswerUseCase: SubmitAnswerUseCase,
    private val usePowerUpUseCase: UsePowerUpUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    
    fun initializeGame(gameId: String, playerName: String ) {
        _uiState.value = _uiState.value.copy(
            gameId = gameId,
            playerName = playerName
        )
        // Try to resolve numeric player id from server using Firebase UID
        viewModelScope.launch {
            try {
                val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                val uid = firebaseUser?.uid
                if (!uid.isNullOrBlank()) {
                    try {
                        val resp = UserRemoteRepository.getUserByUid(uid)
                        if (resp.isSuccessful) {
                            val body = resp.body()
                            val resolvedId = body?.id?.toString()
                            if (!resolvedId.isNullOrBlank()) {
                                android.util.Log.d("GameViewModel", "Resolved numeric player id from server: $resolvedId")
                                _uiState.value = _uiState.value.copy(myPlayerId = resolvedId)
                            }
                        } else {
                            android.util.Log.w("GameViewModel", "Failed to get user by uid: ${resp.code()}")
                        }
                    } catch (e: Exception) {
                        android.util.Log.w("GameViewModel", "Error fetching user by uid: ${e.message}")
                    }
                }
            } catch (e: Exception) {
                android.util.Log.w("GameViewModel", "Failed to resolve player id: ${e.message}")
            }
        }
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

            // Try to reliably determine "my player" id if not set yet
            val currentState = _uiState.value
            var myPlayerId = currentState.myPlayerId

            // Prefer id match if available
            if (myPlayerId == null) {
                // Try to match by player name
                val displayName = currentState.playerName
                if (displayName.isNotBlank()) {
                    if (game.playerOne.name.equals(displayName, ignoreCase = true)) {
                        myPlayerId = game.playerOne.id
                    } else if (game.playerTwo?.name?.equals(displayName, ignoreCase = true) == true) {
                        myPlayerId = game.playerTwo?.id
                    }
                }
            }

            // If still null, try to infer from previous opponentName (fallback)
            if (myPlayerId == null && currentState.opponentName.isNotBlank()) {
                if (!game.playerOne.name.equals(currentState.opponentName, ignoreCase = true)) {
                    myPlayerId = game.playerOne.id
                } else if (!game.playerTwo?.name.equals(currentState.opponentName, ignoreCase = true)) {
                    myPlayerId = game.playerTwo?.id
                }
            }

            // Persist discovered id into state for future submits
            if (myPlayerId != null && myPlayerId != currentState.myPlayerId) {
                android.util.Log.d("GameViewModel", "🎮 Detected myPlayerId=$myPlayerId from game update")
            }

            _uiState.value = currentState.copy(myPlayerId = myPlayerId)

            when (game.status) {
                GameStatus.IN_PROGRESS -> {
                    android.util.Log.d("GameViewModel", "🎮 Processing IN_PROGRESS status")
                    val stateBefore = _uiState.value

                    android.util.Log.d("GameViewModel", "🎮 Player names: p1='${game.playerOne.name}', p2='${game.playerTwo?.name}' | myPlayerId(state)='${stateBefore.myPlayerId}'")

                    // Determine myPlayer and opponent based on discovered myPlayerId or name fallback
                    val myPlayer = when {
                        stateBefore.myPlayerId != null && game.playerOne.id == stateBefore.myPlayerId -> game.playerOne
                        stateBefore.myPlayerId != null && game.playerTwo?.id == stateBefore.myPlayerId -> game.playerTwo
                        game.playerOne.name.equals(stateBefore.playerName, ignoreCase = true) -> game.playerOne
                        game.playerTwo?.name?.equals(stateBefore.playerName, ignoreCase = true) == true -> game.playerTwo
                        else -> null
                    }

                    val opponent = when (myPlayer) {
                        null -> if (game.playerOne.name.equals(stateBefore.playerName, ignoreCase = true)) game.playerTwo else game.playerOne
                        game.playerOne -> game.playerTwo
                        else -> game.playerOne
                    }

                    android.util.Log.d("GameViewModel", "🎮 Found myPlayer: ${myPlayer?.name} (score: ${myPlayer?.score})")
                    android.util.Log.d("GameViewModel", "🎮 Found opponent: ${opponent?.name} (score: ${opponent?.score})")
                    android.util.Log.d("GameViewModel", "🎮 Current question: '${game.currentQuestion?.text}' | options: ${game.currentQuestion?.options}")

                    val newQuestion = game.currentQuestion?.text ?: ""
                    val hasNewQuestion = newQuestion.isNotEmpty() && newQuestion != stateBefore.currentQuestion

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

                    _uiState.value = stateBefore.copy(
                        isLoading = false,
                        playerScore = myPlayerScore,
                        opponentScore = opponentScore,
                        currentQuestion = game.currentQuestion?.text ?: "",
                        options = game.currentQuestion?.options ?: emptyList(),
                        correctAnswer = game.currentQuestion?.correctAnswer,
                        playerProgress = myPlayerPosition,
                        opponentProgress = opponentPosition,
                        myPlayerId = myPlayer?.id ?: stateBefore.myPlayerId,
                        opponentName = opponent?.name ?: "Oponente",
                        gameEnded = gameFinished || stateBefore.gameEnded,
                        winner = winner ?: stateBefore.winner,

                        showFeedback = if (hasNewQuestion) false else stateBefore.showFeedback,
                        selectedOption = if (hasNewQuestion) null else stateBefore.selectedOption,
                        isLastAnswerCorrect = if (hasNewQuestion) null else stateBefore.isLastAnswerCorrect,

                        isPenalized = stateBefore.isPenalized,
                        expectedResult = game.expectedResult ?: "" // <-- NUEVO
                    )

                    if (hasNewQuestion) {
                        android.util.Log.d("GameViewModel", "✅ New question loaded and UI updated!")
                    }

                    android.util.Log.d("GameViewModel", "🎮 ✅ UI State updated successfully!")
                }
                GameStatus.FINISHED -> {
                    val currentState = _uiState.value

                    // Determine myPlayer and opponent consistently like in IN_PROGRESS
                    val myPlayer = when {
                        currentState.myPlayerId != null && game.playerOne.id == currentState.myPlayerId -> game.playerOne
                        currentState.myPlayerId != null && game.playerTwo?.id == currentState.myPlayerId -> game.playerTwo
                        game.playerOne.name.equals(currentState.playerName, ignoreCase = true) -> game.playerOne
                        game.playerTwo?.name?.equals(currentState.playerName, ignoreCase = true) == true -> game.playerTwo
                        else -> null
                    }

                    val opponent = when (myPlayer) {
                        null -> if (game.playerOne.name.equals(currentState.playerName, ignoreCase = true)) game.playerTwo else game.playerOne
                        game.playerOne -> game.playerTwo
                        else -> game.playerOne
                    }

                    // Decide winner by id when possible, fallback to name
                    val winnerEntity = game.winner
                    val isWinner = if (!winnerEntity?.id.isNullOrBlank() && !currentState.myPlayerId.isNullOrBlank()) {
                        winnerEntity?.id == currentState.myPlayerId
                    } else {
                        winnerEntity?.name == currentState.playerName
                    }

                    val playerScore = myPlayer?.score ?: 0
                    val oppScore = opponent?.score ?: 0

                    android.util.Log.d("GameViewModel", "🏁 FINISHED - myPlayer='${myPlayer?.name}', opponent='${opponent?.name}', isWinner=$isWinner")

                    _uiState.value = currentState.copy(
                        isLoading = false,
                        gameEnded = true,
                        winner = if (isWinner) "¡Ganaste!" else "Perdiste",
                        playerScore = playerScore,
                        opponentScore = oppScore,
                        expectedResult = currentState.expectedResult ?: ""
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
    
    fun useFireExtinguisher() {
        val currentState = _uiState.value
        if (currentState.fireExtinguisherActive || currentState.gameEnded || currentState.options.isEmpty() || currentState.fireExtinguisherCount <= 0) return

        // Encontrar la respuesta correcta
        val correctAnswer = currentState.correctAnswer
        if (correctAnswer == null) return

        // Crear una lista filtrada con solo dos opciones: la correcta y una incorrecta
        val filteredOptions = currentState.options.filter { it == correctAnswer }.take(1) +
                            currentState.options.filter { it != correctAnswer }.shuffled().take(1)

        // Asegurar que la lista esté ordenada aleatoriamente
        val shuffledOptions = filteredOptions.shuffled()

        _uiState.value = currentState.copy(
            options = shuffledOptions,
            fireExtinguisherActive = true,
            fireExtinguisherCount = 0 // Reducir el contador a 0 cuando se usa
        )
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