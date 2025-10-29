package com.app.mathracer.ui.screens.worlds.viewmodel

import com.app.mathracer.ui.screens.worlds.World

data class WorldsUiState(
    val worlds: List<World> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
