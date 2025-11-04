package com.app.mathracer.ui.screens.historyGame.viewmodel

data class HistoryGameUiState(
    val isLoading: Boolean = true,
    val gameId: Int? = null,
    val playerId: Int? = null,
    val playerName: String = "",
    val machineName: String = "Mathi",
    val playerScore: Int = 0,
    val machineScore: Int = 0,
    val playerProgress: Int = 0,
    val machineProgress: Int = 0,
    val currentQuestion: String = "",
    val options: List<Int> = emptyList(),
    val selectedOption: Int? = null,
    val correctAnswer: Int? = null,
    val showFeedback: Boolean = false,
    val isLastAnswerCorrect: Boolean? = null,
    val isPenalized: Boolean = false,
    val gameEnded: Boolean = false,
    val winner: String? = null,
    val error: String? = null,
    val expectedResult: String = "",
    val fireExtinguisherActive: Boolean = false,
    val fireExtinguisherCount: Int = 1,
    val livesRemaining: Int = 3,
    val timePerEquation: Int = 10,
    val totalQuestions: Int = 10,
    val timeLeft: Int = 10,
    val canAnswer: Boolean = true,
    val isWaitingNext: Boolean = false
)

