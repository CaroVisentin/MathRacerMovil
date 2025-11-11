package com.app.mathracer.ui.screens.register.viewmodel

data class RegisterUiState(
    val email: String = "",
    val user: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)