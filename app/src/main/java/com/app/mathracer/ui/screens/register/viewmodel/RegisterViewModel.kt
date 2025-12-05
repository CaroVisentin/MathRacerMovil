package com.app.mathracer.ui.screens.register.viewmodel

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.app.mathracer.data.model.User
import com.app.mathracer.data.repository.UserRemoteRepository
import com.app.mathracer.data.CurrentUser
import com.app.mathracer.data.repository.GarageRepository
import com.app.mathracer.data.UserState
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
                            uid = firebaseUser?.uid ?: "",
                            email = firebaseUser?.email ?: state.email,
                            username = state.user
                        )
                        viewModelScope.launch {
                            try {
                                val resp = UserRemoteRepository.createUser(createdUser)
                                if (resp.isSuccessful) CurrentUser.user = resp.body() else CurrentUser.user = createdUser
                                com.app.mathracer.data.UserState.setCoins(CurrentUser.user?.coins ?: 0)
                                 
                                try {
                                    val playerId = CurrentUser.user?.id ?: 0
                                    if (playerId > 0) {
                                        viewModelScope.launch {
                                            try {
                                                val repo = GarageRepository()
                                                val charsRes = repo.getCharacters(playerId)
                                                val bgsRes = repo.getBackgrounds(playerId)
                                                val carsRes = repo.getCars(playerId)
                                                val activeChar = charsRes.getOrNull()?.activeItem
                                                val activeBg = bgsRes.getOrNull()?.activeItem
                                                val activeCar = carsRes.getOrNull()?.activeItem
                                                com.app.mathracer.data.CurrentUser.activeCharacterProductId = activeChar?.productId
                                                com.app.mathracer.data.CurrentUser.activeBackgroundProductId = activeBg?.productId
                                                com.app.mathracer.data.CurrentUser.activeVehicleProductId = activeCar?.productId
                                                com.app.mathracer.data.UserState.setActiveCharacter(activeChar?.productId)
                                                com.app.mathracer.data.UserState.setActiveBackground(activeBg?.productId)
                                                com.app.mathracer.data.UserState.setActiveVehicle(activeCar?.productId)
                                            } catch (_: Exception) { }
                                        }
                                    }
                                } catch (_: Exception) { }
                            } catch (e: Exception) {
                                android.util.Log.e("Register", "Error creando usuario en backend", e)
                                CurrentUser.user = createdUser
                                com.app.mathracer.data.UserState.setCoins(CurrentUser.user?.coins ?: 0)
                            }
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isSuccess = true
                            )
                        }
                    } else {
                        val friendlyMessage = mapRegisterError(task.exception)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = friendlyMessage
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
                                uid = firebaseUser.uid,
                                email = firebaseUser.email,
                                username = firebaseUser.displayName
                            )
                            viewModelScope.launch {
                                    try {
                                    val resp = UserRemoteRepository.createUser(createdUser)
                                    if (resp.isSuccessful) CurrentUser.user = resp.body() else CurrentUser.user = createdUser
                                    com.app.mathracer.data.UserState.setCoins(CurrentUser.user?.coins ?: 0)
                                } catch (e: Exception) {
                                    android.util.Log.e("Register", "Error creando usuario en backend", e)
                                    CurrentUser.user = createdUser
                                    com.app.mathracer.data.UserState.setCoins(CurrentUser.user?.coins ?: 0)
                                }
                                 
                                try {
                                    val playerId = CurrentUser.user?.id ?: 0
                                    if (playerId > 0) {
                                        viewModelScope.launch {
                                            try {
                                                    val repo = GarageRepository()
                                                    val charsRes = repo.getCharacters(playerId)
                                                    val bgsRes = repo.getBackgrounds(playerId)
                                                    val carsRes = repo.getCars(playerId)
                                                    val activeChar = charsRes.getOrNull()?.activeItem
                                                    val activeBg = bgsRes.getOrNull()?.activeItem
                                                    val activeCar = carsRes.getOrNull()?.activeItem
                                                    com.app.mathracer.data.CurrentUser.activeCharacterProductId = activeChar?.productId
                                                    com.app.mathracer.data.CurrentUser.activeBackgroundProductId = activeBg?.productId
                                                    com.app.mathracer.data.CurrentUser.activeVehicleProductId = activeCar?.productId
                                                    com.app.mathracer.data.UserState.setActiveCharacter(activeChar?.productId)
                                                    com.app.mathracer.data.UserState.setActiveBackground(activeBg?.productId)
                                                    com.app.mathracer.data.UserState.setActiveVehicle(activeCar?.productId)
                                            } catch (_: Exception) { }
                                        }
                                    }
                                } catch (_: Exception) { }
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

    private fun mapRegisterError(ex: Exception?): String {
        val defaultMessage = "No se pudo crear la cuenta. Intentalo nuevamente."

        val fb = ex as? FirebaseAuthException ?: return ex?.localizedMessage ?: defaultMessage
        return when (fb.errorCode) {
            "ERROR_INVALID_EMAIL" ->
                "El correo no tiene un formato válido."
            "ERROR_EMAIL_ALREADY_IN_USE" ->
                "Ya existe una cuenta registrada con este correo."
            "ERROR_WEAK_PASSWORD" ->
                "La contraseña es demasiado débil. Usa al menos 6 caracteres."
            "ERROR_OPERATION_NOT_ALLOWED" ->
                "El registro con email y contraseña está deshabilitado."
            else ->
                defaultMessage
        }
    }
}
