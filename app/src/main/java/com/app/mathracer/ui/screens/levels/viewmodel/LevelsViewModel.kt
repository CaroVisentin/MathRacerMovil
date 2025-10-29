package com.app.mathracer.ui.screens.levels.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LevelsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LevelsUiState(isLoading = false))
    val uiState: StateFlow<LevelsUiState> = _uiState

    fun loadLevelsForWorld(worldId: Int, worldName: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            // Simula carga de datos según el ID del mundo
            val levels = (1..12).map {
                LevelUiModel(
                    id = it,
                    name = "Nivel $it",
                    isUnlocked = it <= worldId * 3,
                    stars = when {
                        it <= 2 -> 3
                        it == 3 -> 2
                        it == 4 -> 1
                        else -> 0
                    }
                )
            }

            _uiState.value = LevelsUiState(
                worldName = worldName,
                worldDescription = "Descripción del $worldName",
                levels = levels,
                isLoading = false
            )
        }
    }
}
