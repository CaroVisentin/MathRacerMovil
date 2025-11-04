package com.app.mathracer.ui.screens.historyGame

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.mathracer.R
import com.app.mathracer.ui.screens.game.GamePlayScreen
import com.app.mathracer.ui.screens.game.PowerUp
import com.app.mathracer.ui.screens.game.components.GameResultModal
import com.app.mathracer.ui.screens.historyGame.viewmodel.HistoryGameViewModel

@Composable
fun HistoryGameScreen(
    levelId: Int,
    playerName: String = "Jugador",
    onNavigateBack: () -> Unit = {},
    onPlayAgain: () -> Unit = {},
    viewModel: HistoryGameViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Inicializar el juego
    LaunchedEffect(levelId, playerName) {
        viewModel.initializeGame(levelId, playerName)
    }

    // Limpiar feedback automáticamente después de mostrar resultado
    LaunchedEffect(uiState.showFeedback) {
        if (uiState.showFeedback) {
            if (uiState.isLastAnswerCorrect == true) {
                kotlinx.coroutines.delay(200)
                viewModel.clearFeedback()
                viewModel.prepareForNextQuestion()
            } else {
                kotlinx.coroutines.delay(1000)
                viewModel.clearFeedback()
            }
        }
    }

    GamePlayScreen(
        timeLabel = "10 seg",
        coins = 123_000,
        rivalTrackRes = R.drawable.track_city,
        youTrackRes = R.drawable.track_cake,
        rivalCarRes = R.drawable.car_game,
        opponentName = uiState.machineName,
        playerName = uiState.playerName,
        youCarRes = R.drawable.car_game,
        powerUps = listOf(
            PowerUp(R.drawable.ic_shield, uiState.fireExtinguisherCount, Color(0xFFFF6B6B))
        ),
        expression = uiState.currentQuestion.ifEmpty { 
            when {
                uiState.isLoading -> "Conectando al juego..."
                uiState.gameEnded -> "¡Juego terminado!"
                uiState.isPenalized -> "⏱️ PENALIZADO - Espera 1 segundo..."
                uiState.selectedOption != null && !uiState.showFeedback -> "Procesando respuesta..."
                uiState.showFeedback -> if (uiState.isLastAnswerCorrect == true) "✅ ¡Correcto!" else "❌ Incorrecto"
                else -> ""
            }
        },
        options = uiState.options.map { it },
        rivalProgress = uiState.machineProgress,
        yourProgress = uiState.playerProgress,
        isWaitingForAnswer = uiState.selectedOption != null && !uiState.showFeedback,
        lastAnswerGiven = uiState.selectedOption,
        lastAnswerWasCorrect = uiState.isLastAnswerCorrect,
        showAnswerFeedback = uiState.showFeedback,
        isPenalized = uiState.isPenalized,
        expectedResult = uiState.expectedResult,
        onBack = onNavigateBack,
        onPowerUpClick = { index -> 
            when (index) {
                0 -> viewModel.useFireExtinguisher()
            }
        },
        onOptionClick = { index, value ->
            if (uiState.currentQuestion.isNotEmpty() && !uiState.isPenalized && !uiState.showFeedback) {
                viewModel.submitAnswer(value)
            }
        }
    )

    // Modal de resultado del juego
    if (uiState.gameEnded) {
        GameResultModal(
            isWinner = uiState.winner?.contains("Ganaste") == true,
            userName = uiState.playerName,
            userNameRival = uiState.machineName,
            onDismiss = { 
                // No necesitamos método específico, el estado ya está manejado
            },
            onPlayAgain = {
                onPlayAgain()
            },
            onBackToHome = {
                onNavigateBack()
            }
        )
    }
}
