package com.app.mathracer.ui.screens.levels.viewmodel

import com.app.mathracer.data.model.LevelDto

data class LevelsUiState(
    val worldName: String = "",
    val lastCompletedLevelId: Int = 0,
    val levels: List<LevelDto>? = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
