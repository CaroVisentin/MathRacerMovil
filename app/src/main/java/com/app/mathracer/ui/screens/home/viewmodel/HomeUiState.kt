package com.app.mathracer.ui.screens.home.viewmodel

data class HomeUiState(
    val navigateToWaiting: Boolean = false
)

data class EnergyRepository(
    val secondsUntilNextRecharge: Int,
    val currentAmount: Int,
    val maxAmount: Int
)
