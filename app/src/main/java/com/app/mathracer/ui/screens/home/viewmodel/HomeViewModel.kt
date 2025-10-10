package com.app.mathracer.ui.screens.home.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun navigateToMultiplayer() {
        _uiState.value = _uiState.value.copy(navigateToWaiting = true)
    }
    
    fun clearNavigation() {
        _uiState.value = _uiState.value.copy(navigateToWaiting = false)
    }
}