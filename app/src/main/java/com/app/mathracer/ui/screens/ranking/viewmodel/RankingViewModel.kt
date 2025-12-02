package com.app.mathracer.ui.screens.ranking.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.data.CurrentUser
import com.app.mathracer.data.repository.RankingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import com.app.mathracer.data.repository.GarageRepository
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
                val repo = GarageRepository()
                val players = dto.top10.map { p ->
                    PlayerRanking(
                        username = p.name,
                        score = p.points,
                        position = p.position,
                        playerId = p.playerId
                    )
                }

                try {
                    val updated = players.map { pl ->
                        async {
                            if (pl.playerId != null && pl.playerId > 0) {
                                try {
                                    val chars = repo.getCharacters(pl.playerId)
                                    val avatarId = chars.getOrNull()?.activeItem?.productId
                                    pl.copy(avatarProductId = avatarId)
                                } catch (_: Exception) { pl }
                            } else pl
                        }
                    }.awaitAll()

                    _uiState.value = _uiState.value.copy(
                        topPlayers = updated,
                        userPosition = dto.currentPlayerPosition,
                        isLoading = false
                    )
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(
                        topPlayers = players,
                        userPosition = dto.currentPlayerPosition,
                        isLoading = false
                    )
                }
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