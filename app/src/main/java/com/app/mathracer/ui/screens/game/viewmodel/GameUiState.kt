package com.app.mathracer.ui.screens.game.viewmodel

data class GameUiState(
    val isLoading: Boolean = true,
    val gameId: String = "",
    val playerName: String = "",
    val myPlayerId: String? = null,
    val opponentName: String = "Oponente",
    val playerScore: Int = 0,
    val opponentScore: Int = 0,
    val playerProgress: Int = 0,
    val opponentProgress: Int = 0,
    val currentQuestion: String = "",
    val options: List<Int> = emptyList(),
    val selectedOption: Int? = 0,
    val correctAnswer: Int? = 0,
    val showFeedback: Boolean = false,
    val isLastAnswerCorrect: Boolean? = null,
    val isPenalized: Boolean = false,
    val gameEnded: Boolean = false,
    val winner: String? = null,
    val error: String? = null
)