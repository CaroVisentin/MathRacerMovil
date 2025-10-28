package com.app.mathracer.ui.screens.levels.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LevelsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LevelsUiState(isLoading = true))
    val uiState: StateFlow<LevelsUiState> = _uiState

    init {
        loadLevels()
    }

    private fun loadLevels() {
        viewModelScope.launch {
            // Simula carga de datos
            val levels = (1..12).map {
                LevelUiModel(
                    id = it,
                    name = "Nivel $it",
                    isUnlocked = it <= 5,
                    stars = when {
                        it <= 2 -> 3
                        it == 3 -> 2
                        it == 4 -> 1
                        else -> 0
                    }
                )
            }

            _uiState.value = LevelsUiState(
                worldName = "Mundo 1",
                levels = levels,
                isLoading = false
            )
        }
    }
}
