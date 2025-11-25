package com.app.mathracer.ui.screens.multiplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.data.network.RetrofitClient
import com.app.mathracer.data.network.ApiService
import com.app.mathracer.data.repository.UserRemoteRepository
import com.app.mathracer.domain.repositories.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateMatchViewModel @Inject constructor(
    private val gameRepository: GameRepository
): ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val api = RetrofitClient.api

    fun createMatch(name: String, privacy: String, difficulty: String, resultType: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val isPrivate = privacy.equals("Privada", ignoreCase = true)
                val password: String? = null

                val body = ApiService.CreateGameRequest(
                    gameName = name,
                    isPrivate = isPrivate,
                    password = password,
                    difficulty = difficulty,
                    expectedResult = resultType
                )

                val token = try { UserRemoteRepository.getIdToken() } catch (e: Exception) { null }
                val header = token?.let { "Bearer $it" }

                val resp = api.createOnlineGame(header, body)
                if (resp.isSuccessful) {
                    // Try to locate the created game and join it on the hub so the creator receives GameUpdate events
                    try {
                        val firebaseName = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.displayName
                        val gamesResp = api.getAvailableGames(publicOnly = false)
                        if (gamesResp.isSuccessful) {
                            val games = gamesResp.body()?.games ?: emptyList()
                            // Prefer exact match by name + creatorName when possible
                            val matched = games.firstOrNull { it.gameName == name && (firebaseName == null || it.creatorName == firebaseName) }
                                ?: games.firstOrNull { it.gameName == name }

                            if (matched != null) {
                                android.util.Log.d("CreateMatch", "Found created game with id=${matched.gameId}, attempting to join via SignalR")
                                val joinResult = gameRepository.joinGame(matched.gameId, password = null)
                                if (joinResult.isSuccess) {
                                    android.util.Log.d("CreateMatch", "Creator successfully joined hub group for game ${matched.gameId}")
                                } else {
                                    android.util.Log.w("CreateMatch", "Failed to join hub for created game: ${joinResult.exceptionOrNull()?.message}")
                                }
                            } else {
                                android.util.Log.w("CreateMatch", "Could not find created game in available games list")
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("CreateMatch", "Error locating/joining created game", e)
                    }

                    onResult(Result.success(Unit))
                } else {
                    val msg = "Error creating match: ${resp.code()}"
                    _error.value = msg
                    onResult(Result.failure(Exception(msg)))
                }

            } catch (e: Exception) {
                _error.value = e.message
                onResult(Result.failure(e))
            } finally {
                _isLoading.value = false
            }
        }
    }
}
