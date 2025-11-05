package com.app.mathracer.ui.screens.worlds.viewmodel

import com.app.mathracer.data.model.WorldDto

data class WorldsUiState(
    val isLoading: Boolean = false,
    val worlds: List<WorldDto>? = emptyList(),
    val lastAvailableWorldId: Int = 0,
    val errorMessage: String? = null
)
