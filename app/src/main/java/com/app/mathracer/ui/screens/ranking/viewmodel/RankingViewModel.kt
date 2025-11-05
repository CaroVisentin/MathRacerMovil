package com.app.mathracer.ui.screens.ranking.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.data.CurrentUser
import com.app.mathracer.data.repository.RankingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class RankingViewModel @Inject constructor() : ViewModel() {


    private val repository = RankingRepository()

    private val _uiState = MutableStateFlow(RankingUiState())
    val uiState: StateFlow<RankingUiState> = _uiState

    init {

        val playerId = CurrentUser.user?.id?.takeIf { it != null && it > 0 }
        loadRanking(playerId)
    }

    fun loadRanking(playerId: Int? = null) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val result = repository.getRanking(playerId)
            if (result.isSuccess) {
                val dto = result.getOrNull()!!
                val players = dto.top10.map { p ->
                    PlayerRanking(
                        username = p.name,
                        score = p.points,
                        position = p.position
                    )
                }
                _uiState.value = _uiState.value.copy(
                    topPlayers = players,
                    userPosition = dto.currentPlayerPosition,
                    isLoading = false
                )
            } else {
                val ex = result.exceptionOrNull()
                _uiState.value = _uiState.value.copy(
                    errorMessage = ex?.message ?: "Error al cargar ranking",
                    isLoading = false
                )
            }
        }
    }
}