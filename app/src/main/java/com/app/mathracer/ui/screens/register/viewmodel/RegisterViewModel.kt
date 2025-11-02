package com.app.mathracer.ui.screens.register.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.app.mathracer.data.model.User
import com.app.mathracer.data.repository.UserRemoteRepository
import com.app.mathracer.data.CurrentUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun onUserChange(value: String) {
        _uiState.value = _uiState.value.copy(user = value)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value)
    }

    fun onRepeatPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(repeatPassword = value)
    }

    fun registerUser() {
        val state = _uiState.value
        if (state.email.isBlank() || state.password.isBlank() || state.repeatPassword.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Todos los campos son obligatorios")
            return
        }
        if (state.password != state.repeatPassword) {
            _uiState.value = state.copy(errorMessage = "Las contraseñas no coinciden")
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(state.email, state.password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Registrar en back con el uid de Firebase
                        val firebaseUser = auth.currentUser
                        val createdUser = User(
                            id = firebaseUser?.uid ?: "",
                            email = firebaseUser?.email ?: state.email,
                            name = state.user
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
                                name = _uiState.value.user
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
