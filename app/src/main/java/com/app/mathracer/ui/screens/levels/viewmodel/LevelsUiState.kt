package com.app.mathracer.ui.screens.levels.viewmodel

data class LevelsUiState(
    val worldName: String = "",
    val levels: List<LevelUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

data class LevelUiModel(
    val id: Int,
    val name: String,
    val isUnlocked: Boolean,
    val stars: Int
)
