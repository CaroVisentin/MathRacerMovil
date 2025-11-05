package com.app.mathracer.ui.screens.profile.viewmodel

import com.app.mathracer.ui.screens.profile.components.Friend
import com.app.mathracer.data.model.Friend as RemoteFriend

data class ProfileUiState(
    val selectedTab: String = "Perfil",
    val soundVolume: Float = 0.8f,
    val musicVolume: Float = 0.5f,
    val userName: String = "Usuario",
    val gamesPlayed: Int = 27,
    val points: Double = 11512.0,
    val userEmail: String? = "jugador3309@gmail.com",
    val friends: List<Friend> = emptyList(),
    val pending: List<RemoteFriend> = emptyList(),
    val remoteFriends: List<RemoteFriend> = emptyList()
)