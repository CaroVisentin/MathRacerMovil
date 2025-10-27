package com.app.mathracer.ui.screens.profile.viewmodel

import androidx.lifecycle.ViewModel
import com.app.mathracer.R
import com.app.mathracer.ui.screens.profile.components.Friend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        loadFriends()
    }

    private fun loadFriends() {
        val fakeFriends = listOf(
            Friend("Amigo1", "10.253", R.drawable.avatar, R.drawable.car),
            Friend("Amigo2", "4.535", R.drawable.avatar, R.drawable.car),
            Friend("Amigo3", "1.253", R.drawable.avatar, R.drawable.car),
            Friend("Amigo4", "103", R.drawable.avatar, R.drawable.car)
        )
        _uiState.update { it.copy(friends = fakeFriends) }
    }

    fun onTabSelected(tab: String) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun onSoundVolumeChange(value: Float) {
        _uiState.update { it.copy(soundVolume = value) }
    }

    fun onMusicVolumeChange(value: Float) {
        _uiState.update { it.copy(musicVolume = value) }
    }

    fun onLogout() {
        // TODO: lógica de cierre de sesión
    }

    fun onDeleteAccount() {
        // TODO: lógica de eliminar cuenta
    }
}