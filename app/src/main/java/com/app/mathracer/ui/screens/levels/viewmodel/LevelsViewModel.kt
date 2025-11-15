package com.app.mathracer.ui.screens.levels.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.data.model.Levels
import com.app.mathracer.data.model.Worlds
import com.app.mathracer.data.repository.LevelsRemoteRepository
import com.app.mathracer.data.repository.UserRemoteRepository
import com.app.mathracer.data.repository.WorldsRemoteRepository
import com.app.mathracer.ui.screens.worlds.viewmodel.WorldsUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LevelsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LevelsUiState(isLoading = false))
    val uiState: StateFlow<LevelsUiState> = _uiState

    fun loadLevelsForWorld(worldId: Int, worldName: String) {
        _uiState.value = _uiState.value.copy(isLoading = true)

        viewModelScope.launch {
            try {
                val response = LevelsRemoteRepository.getLevels(worldId) //getUserByUid(uid)
                Log.d("Level response", "getLevels response: $response")
                if (response.isSuccessful) {
                    val levels: Levels? = response.body()
                    if (levels != null) {
                        _uiState.value = LevelsUiState(
                            levels = levels.levels,
                            isLoading = false,
                            worldName = levels.worldName,
                            lastCompletedLevelId = levels.lastCompletedLevelId
                        )
                    }

                    Log.d("Login", "Usuario obtenido del backend: ${levels}")
                } else {
                    android.util.Log.e("Login", "getUser failed: ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("Login", "Error obteniendo usuario del backend", e)
            }
        }
    }

    fun checkEnergyBeforePlay(
        onHasEnergy: () -> Unit,
        onNoEnergy: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val resp = UserRemoteRepository.getEnergy()
                if (resp.isSuccessful) {
                    val dto = resp.body()
                    val energy = dto?.currentAmount ?: 0

                    if (energy > 0) onHasEnergy()
                    else onNoEnergy()
                }
            } catch (e: Exception) {
                    onNoEnergy() // fallback seguro
                }
            }
        }



    /*

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
     */
}
