package com.app.mathracer.ui.screens.multiplayer.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.data.network.RetrofitClient
import com.app.mathracer.domain.repositories.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JoinMatchesViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val api = RetrofitClient.api

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _games = MutableStateFlow<List<com.app.mathracer.data.network.ApiService.AvailableGameDto>>(emptyList())
    val games: StateFlow<List<com.app.mathracer.data.network.ApiService.AvailableGameDto>> = _games.asStateFlow()

    fun fetchAvailableGames(publicOnly: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val resp = api.getAvailableGames(publicOnly)
                if (resp.isSuccessful) {
                    val body = resp.body()
                    _games.value = body?.games ?: emptyList()
                } else {
                    _error.value = "Error fetching games: ${resp.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun joinGame(matchId: Int, password: String?, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Ensure connection + join over SignalR
                val result = gameRepository.joinGame(matchId, password)
                Log.d("MATHI", result.toString())
                onResult(result)
            } catch (e: Exception) {
                _error.value = e.message
                onResult(Result.failure(e))
            } finally {
                _isLoading.value = false
            }
        }
    }
}
