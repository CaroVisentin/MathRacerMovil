package com.app.mathracer.ui.screens.worlds.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.data.model.WorldDto
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
                    _uiState.value = WorldsUiState(worlds = worlds?.worlds, isLoading = false, lastAvailableWorldId = worlds?.lastAvailableWorldId ?: 0)

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
