package com.app.mathracer.ui.screens.game.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.data.repository.GameRepository
import com.app.mathracer.data.services.GameEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameRepository: GameRepository
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
            gameRepository.gameEvents.collect { event ->
                when (event) {
                    is GameEvent.GameUpdate -> {
                        val gameUpdate = event.gameUpdate
                        
                        // Solo procesar si es el juego correcto
                        if (gameUpdate.gameId.toString() == _uiState.value.gameId) {
                            when (gameUpdate.status) {
                                "InProgress" -> {
                                    val currentState = _uiState.value
                                    val previousProgress = currentState.playerProgress
                                    
                                    // Encontrar mi jugador basado en el nombre del state
                                    val myPlayerName = currentState.playerName
                                    val myPlayer = gameUpdate.players.find { it.name == myPlayerName }
                                        ?: gameUpdate.players.firstOrNull()
                                    
                                    // El oponente es cualquier otro jugador
                                    val otherPlayer = gameUpdate.players.find { it.id != myPlayer?.id }
                                        ?: gameUpdate.players.getOrNull(1)
                                    
                                    val newProgress = myPlayer?.correctAnswers ?: 0
                                    
                                    // Detectar si hubo un cambio en el progreso (respuesta correcta)
                                    val answersChanged = newProgress > previousProgress
                                    val wasLastAnswerCorrect = if (currentState.lastAnswerGiven != null && answersChanged) {
                                        true
                                    } else if (currentState.lastAnswerGiven != null && !answersChanged) {
                                        false
                                    } else null
                                    
                                    android.util.Log.d("GameViewModel", "📊 Progreso: anterior=$previousProgress, nuevo=$newProgress, " +
                                            "últimaRespuesta=${currentState.lastAnswerGiven}, fuéCorrecta=$wasLastAnswerCorrect")
                                    
                                    // Actualizar pregunta actual si existe
                                    gameUpdate.currentQuestion?.let { question ->
                                        _uiState.value = _uiState.value.copy(
                                            currentQuestion = question.equation,
                                            currentOptions = question.options,
                                            isWaitingForAnswer = false, // Permitir responder cuando llega una nueva pregunta
                                            // Actualizar progreso
                                            actualPlayerId = myPlayer?.id?.toString() ?: currentState.actualPlayerId,
                                            playerProgress = newProgress,
                                            opponentProgress = otherPlayer?.correctAnswers ?: 0,
                                            // Feedback de respuesta
                                            lastAnswerWasCorrect = wasLastAnswerCorrect,
                                            showAnswerFeedback = wasLastAnswerCorrect != null
                                        )
                                    } ?: run {
                                        // Solo actualizar progreso sin nueva pregunta
                                        _uiState.value = _uiState.value.copy(
                                            actualPlayerId = myPlayer?.id?.toString() ?: currentState.actualPlayerId,
                                            playerProgress = newProgress,
                                            opponentProgress = otherPlayer?.correctAnswers ?: 0,
                                            lastAnswerWasCorrect = wasLastAnswerCorrect,
                                            showAnswerFeedback = wasLastAnswerCorrect != null
                                        )
                                    }
                                }
                                
                                "Finished" -> {
                                    // Encontrar el ganador
                                    val winner = gameUpdate.players.find { it.id == gameUpdate.winnerId }
                                    
                                    // Determinar si el jugador móvil ganó basándose en el actualPlayerId o playerName
                                    val actualPlayerId = _uiState.value.actualPlayerId
                                    val playerName = _uiState.value.playerName
                                    
                                    val isWinner = when {
                                        // Comparar por ID real del backend si está disponible
                                        actualPlayerId != null && gameUpdate.winnerId != null -> {
                                            actualPlayerId.toIntOrNull() == gameUpdate.winnerId
                                        }
                                        // Comparar por nombre del jugador como fallback
                                        winner?.name == playerName -> true
                                        else -> false
                                    }
                                    
                                    android.util.Log.d("GameViewModel", "🏆 Juego terminado: winnerId=${gameUpdate.winnerId}, " +
                                            "actualPlayerId=$actualPlayerId, playerName=$playerName, " +
                                            "winnerName=${winner?.name}, isWinner=$isWinner")
                                    
                                    _uiState.value = _uiState.value.copy(
                                        gameFinished = true,
                                        isWinner = isWinner,
                                        gameSummary = "Partida terminada. Ganador: ${winner?.name ?: "Desconocido"}"
                                    )
                                }
                            }
                        }
                    }
                    
                    is GameEvent.NewQuestion -> {
                        if (event.gameId == _uiState.value.gameId) {
                            // Parsear la pregunta del JSON
                            try {
                                val questionData = parseQuestionData(event.questionData)
                                _uiState.value = _uiState.value.copy(
                                    currentQuestion = questionData.equation,
                                    currentOptions = questionData.options,
                                    isWaitingForAnswer = true
                                )
                            } catch (e: Exception) {
                                _uiState.value = _uiState.value.copy(
                                    error = "Error al cargar pregunta: ${e.message}"
                                )
                            }
                        }
                    }
                    
                    is GameEvent.PlayerAnswered -> {
                        if (event.gameId == _uiState.value.gameId) {
                            // Actualizar progreso de jugadores
                            if (event.playerId == _uiState.value.playerId) {
                                _uiState.value = _uiState.value.copy(
                                    playerProgress = event.progress,
                                    isWaitingForAnswer = false
                                )
                            } else {
                                _uiState.value = _uiState.value.copy(
                                    opponentProgress = event.progress
                                )
                            }
                        }
                    }
                    
                    is GameEvent.GameFinished -> {
                        if (event.gameId == _uiState.value.gameId) {
                            val isWinner = event.winnerId == _uiState.value.playerId
                            _uiState.value = _uiState.value.copy(
                                gameFinished = true,
                                isWinner = isWinner,
                                gameSummary = event.summary
                            )
                        }
                    }
                    
                    is GameEvent.Error -> {
                        _uiState.value = _uiState.value.copy(
                            error = event.message
                        )
                    }
                    
                    else -> { /* Otros eventos */ }
                }
            }
        }
    }
    
    fun submitAnswer(answer: String) {
        viewModelScope.launch {
            val gameId = _uiState.value.gameId ?: return@launch
            
            // El playerId debería venir de la última GameUpdate
            val playerId = _uiState.value.actualPlayerId ?: _uiState.value.playerId
            
            android.util.Log.d("GameViewModel", "🎯 Enviando respuesta: $answer para gameId=$gameId, playerId=$playerId")
            
            // Registrar la respuesta enviada y bloquear nuevas respuestas
            _uiState.value = _uiState.value.copy(
                isWaitingForAnswer = true,
                lastAnswerGiven = answer,
                showAnswerFeedback = false, // Ocultar feedback anterior
                lastAnswerWasCorrect = null // Resetear estado
            )
            
            val result = gameRepository.sendAnswer(gameId, playerId, answer)
            result.fold(
                onSuccess = {
                    android.util.Log.d("GameViewModel", "✅ Respuesta enviada correctamente")
                    // El estado se actualizará a través de los eventos
                },
                onFailure = { error ->
                    android.util.Log.e("GameViewModel", "❌ Error enviando respuesta: ${error.message}")
                    _uiState.value = _uiState.value.copy(
                        isWaitingForAnswer = false,
                        error = "Error al enviar respuesta: ${error.message}",
                        lastAnswerGiven = null // Limpiar respuesta si falló
                    )
                }
            )
        }
    }
    
    fun dismissGameResult() {
        _uiState.value = _uiState.value.copy(gameFinished = false)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearAnswerFeedback() {
        _uiState.value = _uiState.value.copy(
            showAnswerFeedback = false,
            lastAnswerWasCorrect = null,
            lastAnswerGiven = null
        )
    }
    
    private fun parseQuestionData(questionData: String): QuestionData {
        // Simple parsing - en producción usarías un parser JSON adecuado
        // Por ahora, asumo que viene como "equation|option1|option2|option3|option4"
        val parts = questionData.split("|")
        return QuestionData(
            equation = parts.getOrNull(0) ?: "X + Y = ?",
            options = parts.drop(1).takeIf { it.isNotEmpty() } ?: listOf("1", "2", "3", "4")
        )
    }
}

data class GameUiState(
    val gameId: String? = null,
    val playerId: String = "player_${System.currentTimeMillis()}", // ID temporal del jugador
    val actualPlayerId: String? = null, // ID real del jugador del backend
    val playerName: String = "Jugador", // Nombre del jugador
    val currentQuestion: String = "",
    val currentOptions: List<String> = emptyList(),
    val playerProgress: Int = 0,
    val opponentProgress: Int = 0,
    val isWaitingForAnswer: Boolean = false,
    val gameFinished: Boolean = false,
    val isWinner: Boolean = false,
    val gameSummary: String = "",
    val error: String? = null,
    // Feedback de respuesta
    val lastAnswerGiven: String? = null,
    val lastAnswerWasCorrect: Boolean? = null,
    val showAnswerFeedback: Boolean = false
)

data class QuestionData(
    val equation: String,
    val options: List<String>
)