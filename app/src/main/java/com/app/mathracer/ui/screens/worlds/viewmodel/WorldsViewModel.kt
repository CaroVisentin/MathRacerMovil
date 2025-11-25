package com.app.mathracer.ui.screens.worlds.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.data.model.WorldDto
import com.app.mathracer.data.repository.LevelsRemoteRepository
import com.app.mathracer.data.model.Worlds
import com.app.mathracer.data.repository.WorldsRemoteRepository
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
            try {
                val response = WorldsRemoteRepository.getWorlds() //getUserByUid(uid)
                Log.d("Login response", "getUser response: $response")
                if (response.isSuccessful) {
                    val worlds: Worlds? = response.body()
                    val fetchedWorlds = worlds?.worlds.orEmpty()

                    val progressMap = mutableMapOf<Int, Pair<Int, Int>>()
                    for (w in fetchedWorlds) {
                        try {
                            Log.d("WorldsViewModel", "Fetching levels for world ${w}")
                            if(w.id == (worlds?.lastAvailableWorldId ?: 0)) {
                                val levelsResp = LevelsRemoteRepository.getLevels(w.id)
                                if (levelsResp.isSuccessful) {
                                    val levelsBody = levelsResp.body()
                                    val total = levelsBody?.levels?.size ?: 15
                                    val lastCompletedId = levelsBody?.lastCompletedLevelId ?: 0
                                    val completed =
                                        levelsBody?.levels?.count { it.id <= lastCompletedId } ?: 0
                                    progressMap[w.id] = Pair(completed, total)
                                } else {
                                    progressMap[w.id] = Pair(0, 15)
                                }
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("WorldsViewModel", "Error fetching levels for world ${w.id}", e)
                            progressMap[w.id] = Pair(0, 15)
                        }
                    }

                    _uiState.value = WorldsUiState(
                        worlds = fetchedWorlds,
                        isLoading = false,
                        lastAvailableWorldId = worlds?.lastAvailableWorldId ?: 0,
                        worldProgress = progressMap
                    )

                    Log.d("Login", "Usuario obtenido del backend: ${worlds}")
                } else {
                    android.util.Log.e("Login", "getUser failed: ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("Login", "Error obteniendo usuario del backend", e)
            }
        }
    }


    fun onWorldClicked(world: WorldDto, onWorldClick: (WorldDto) -> Unit) {
        onWorldClick(world)
    }

}
