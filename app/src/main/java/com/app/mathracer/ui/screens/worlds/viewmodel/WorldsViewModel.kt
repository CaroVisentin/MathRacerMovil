package com.app.mathracer.ui.screens.worlds.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.ui.screens.worlds.World
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WorldsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WorldsUiState(isLoading = true))
    val uiState: StateFlow<WorldsUiState> = _uiState

    init {
        loadWorlds()
    }

    private fun loadWorlds() {
        viewModelScope.launch {
            // Simular carga de datos
            delay(1000)
            val worlds = listOf(
                World(1, "Mundo 1", "Suma y Resta", 10, 10, false),
                World(2, "Mundo 2", "Multiplicación", 6, 15, false),
                World(3, "Mundo 3", "División", 0, 10, true),
                World(4, "Mundo 4", "Operaciones Mixtas", 0, 10, true)
            )
            _uiState.value = WorldsUiState(worlds = worlds, isLoading = false)
        }
    }

    fun onWorldClicked(world: World, onWorldClick: (World) -> Unit) {
        onWorldClick(world)
    }

}
