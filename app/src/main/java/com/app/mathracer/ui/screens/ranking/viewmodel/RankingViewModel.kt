package com.app.mathracer.ui.screens.ranking.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RankingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(RankingUiState())
    val uiState: StateFlow<RankingUiState> = _uiState

    init {
        loadRanking()
    }

    private fun loadRanking() {
        viewModelScope.launch {
            // Simulación de carga (podés reemplazarlo por una llamada a tu repo/API)
            delay(1000)

            val mockPlayers = listOf(
                PlayerRanking("jugador3309", 15900, 1),
                PlayerRanking("jugador3349", 14677, 2),
                PlayerRanking("jugador3360", 14500, 3),
                PlayerRanking("jugador3390", 14200, 4),
                PlayerRanking("jugador3400", 14000, 5),
                PlayerRanking("jugador3420", 13900, 6),
                PlayerRanking("jugador3430", 13750, 7),
                PlayerRanking("jugador3440", 13600, 8),
                PlayerRanking("jugador3450", 13500, 9),
                PlayerRanking("jugador3460", 13400, 10)
            )

            _uiState.value = RankingUiState(
                topPlayers = mockPlayers,
                userPosition = 4, // Ejemplo: usuario actual está 4°
                isLoading = false
            )
        }
    }
}