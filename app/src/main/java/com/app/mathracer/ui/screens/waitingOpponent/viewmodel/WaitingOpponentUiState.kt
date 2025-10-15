package com.app.mathracer.ui.screens.waitingOpponent.viewmodel

data class WaitingOpponentUiState(
    val isConnecting: Boolean = false,
    val isConnected: Boolean = false,
    val isSearchingMatch: Boolean = false,
    val gameFound: Boolean = false,
    val playerName: String = "",
    val opponentName: String = "",
    val gameId: String = "",
    val message: String = "Conectando...",
    val error: String? = null
)

sealed class NavigationEvent {
    data class NavigateToGame(val gameId: String) : NavigationEvent()
}