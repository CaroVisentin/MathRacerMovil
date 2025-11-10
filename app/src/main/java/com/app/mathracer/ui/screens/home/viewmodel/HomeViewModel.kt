package com.app.mathracer.ui.screens.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.data.repository.UserRemoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    // ----- DATA CLASSES -----
    data class EnergyState(
        val secondsLeft: Int = 0,
        val currentAmount: Int = 0,
        val maxAmount: Int = 0,
        val isLoading: Boolean = false,
        val error: String? = null
    )

    data class HomeUiState(
        val navigateToWaiting: Boolean = false,
        val energy: EnergyState = EnergyState()
    )

    // ----- STATE -----
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null
    private var refreshing = false

    // ----- NAVEGACIÓN -----
    fun navigateToMultiplayer() {
        _uiState.update { it.copy(navigateToWaiting = true) }
    }

    fun clearNavigation() {
        _uiState.update { it.copy(navigateToWaiting = false) }
    }

    // ----- CICLO DE VIDA -----
    fun onEnterHome() = refreshEnergy()

    fun onResume() {
        if (_uiState.value.energy.secondsLeft <= 0) refreshEnergy()
    }

    // ----- LLAMADA A /api/energy (directo al object repo) -----
    fun refreshEnergy() {
        if (refreshing) return
        refreshing = true

        viewModelScope.launch {
            _uiState.update { it.copy(energy = it.energy.copy(isLoading = true, error = null)) }
            try {
                val resp = UserRemoteRepository.getEnergy()
                if (resp.isSuccessful) {
                    val dto = resp.body()
                    if (dto != null) {
                        _uiState.update {
                            it.copy(
                                energy = it.energy.copy(
                                    secondsLeft = dto.secondsUntilNextRecharge.coerceAtLeast(0),
                                    currentAmount = dto.currentAmount.coerceAtLeast(0),
                                    maxAmount = dto.maxAmount.coerceAtLeast(0),
                                    isLoading = false,
                                    error = null
                                )
                            )
                        }
                        if (dto.maxAmount > 0 && dto.currentAmount >= dto.maxAmount) {
                            stopCountdown()
                        } else {
                            startCountdown()
                        }
                    } else {
                        setError("Respuesta vacía")
                    }
                } else {
                    setError("Error ${resp.code()}: ${resp.errorBody()?.string().orEmpty()}")
                }
            } catch (t: Throwable) {
                setError(t.message ?: "Error de red")
            } finally {
                refreshing = false
            }
        }
    }

    // ----- COUNTDOWN -----
    private fun startCountdown() {
        countdownJob?.cancel()
        val start = _uiState.value.energy.secondsLeft
        if (start <= 0) {
            refreshEnergy()
            return
        }

        countdownJob = viewModelScope.launch {
            var left = start
            while (left > 0 && isActive) {
                delay(1000)
                left--
                _uiState.update { s -> s.copy(energy = s.energy.copy(secondsLeft = left)) }
            }
            if (isActive) refreshEnergy()
        }
    }

    private fun stopCountdown() {
        countdownJob?.cancel()
        countdownJob = null
    }

    private fun setError(msg: String) {
        _uiState.update { it.copy(energy = it.energy.copy(isLoading = false, error = msg)) }
        stopCountdown()
    }

    override fun onCleared() {
        stopCountdown()
        super.onCleared()
    }
}
