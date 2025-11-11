package com.app.mathracer.ui.screens.ranking.viewmodel

data class RankingUiState(
    val topPlayers: List<PlayerRanking> = emptyList(),
    val userPosition: Int? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

data class PlayerRanking(
    val username: String,
    val score: Int,
    val position: Int
)