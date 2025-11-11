package com.app.mathracer.ui.screens.login.viewmodel

data class LoginUiState(
    val user: String = "",
    val pass: String = "",
    val showPass: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)