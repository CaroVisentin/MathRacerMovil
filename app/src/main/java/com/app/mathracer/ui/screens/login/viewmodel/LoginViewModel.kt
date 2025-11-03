package com.app.mathracer.ui.screens.login.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.app.mathracer.data.repository.UserRemoteRepository
import com.app.mathracer.data.CurrentUser
import com.app.mathracer.data.model.User
import androidx.lifecycle.viewModelScope
import com.app.mathracer.data.model.UserLogin
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun onUserChange(newUser: String) {
        _uiState.update { it.copy(user = newUser) }
    }

    fun onPassChange(newPass: String) {
        _uiState.update { it.copy(pass = newPass) }
    }

    fun togglePasswordVisibility() {
        _uiState.update { it.copy(showPass = !it.showPass) }
    }

    fun loginWithEmail() {
        val state = _uiState.value
        if (state.user.isBlank() || state.pass.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Usuario y contraseña requeridos")
            return
        }
        _uiState.value = state.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(state.user, state.pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val uid = auth.currentUser?.uid
                        val user = UserLogin(email = state.user, password = state.pass)
                        if (!uid.isNullOrBlank()) {
                            viewModelScope.launch {
                                try {
                                    val response = UserRemoteRepository.loginUser(user) //getUserByUid(uid)
                                    Log.d("Login response", "getUser response: $response")
                                    if (response.isSuccessful) {
                                        CurrentUser.user = response.body()
                                    } else {
                                        android.util.Log.e("Login", "getUser failed: ${response.code()}")
                                    }
                                } catch (e: Exception) {
                                    android.util.Log.e("Login", "Error obteniendo usuario del backend", e)
                                }
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    isSuccess = true
                                )
                            }
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isSuccess = true
                            )
                        }
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = task.exception?.localizedMessage ?: "Error desconocido"
                        )
                    }
                }
        }
    }

    fun handleGoogleSignInResult(data: Intent?) {
        android.util.Log.d("GoogleSignIn", "Iniciando proceso de autenticación con Google")
        try {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val account = GoogleSignIn.getSignedInAccountFromIntent(data)
                .getResult(ApiException::class.java)

            android.util.Log.d("GoogleSignIn", "Cuenta Google obtenida: ${account?.email}")

            account?.let { googleAccount ->
                android.util.Log.d("GoogleSignIn", "ID Token obtenido, procediendo con Firebase")
                val credential = GoogleAuthProvider.getCredential(googleAccount.idToken, null)

                auth.signInWithCredential(credential)
                    .addOnSuccessListener { authResult ->
                        android.util.Log.d("GoogleSignIn", "Autenticación Firebase exitosa")
                        authResult.user?.let { firebaseUser ->
                            android.util.Log.d("GoogleSignIn", "Usuario Firebase: ${firebaseUser.email}")
                            val createdUser = User(
                                id = firebaseUser.uid,
                                email = firebaseUser.email,
                                name = firebaseUser.displayName //_uiState.value.user
                            )
                            viewModelScope.launch {
                                try {
                                    val resp = UserRemoteRepository.createUser(createdUser)
                                    if (resp.isSuccessful) CurrentUser.user = resp.body() else CurrentUser.user = createdUser
                                } catch (e: Exception) {
                                    android.util.Log.e("Register", "Error creando usuario en backend", e)
                                    CurrentUser.user = createdUser
                                }
                                _uiState.value = _uiState.value.copy(
                                    isLoading = false,
                                    isSuccess = true
                                )
                            }
                        }
                    }
                    .addOnFailureListener { e ->
                        android.util.Log.e("GoogleSignIn", "Error en autenticación Firebase", e)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Error al autenticar con Firebase: ${e.message}"
                        )
                    }
            } ?: run {
                android.util.Log.e("GoogleSignIn", "Cuenta Google es null")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "No se pudo obtener la cuenta de Google"
                )
            }
        } catch (e: ApiException) {
            android.util.Log.e("GoogleSignIn", "Error en Google Sign In", e)
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "Error al iniciar sesión con Google: ${e.message}"
            )
        }
    }

    fun resetError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun resetSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }
}
