package com.app.mathracer.ui.screens.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.data.CurrentUser
import com.app.mathracer.data.model.Friend
import com.app.mathracer.data.repository.FriendRepository
import com.app.mathracer.ui.screens.profile.components.Friend as FriendUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        refreshAll()
    }

    fun refreshAll() {
        viewModelScope.launch {
            loadFriends()
            loadPending()
        }
    }

    private suspend fun loadFriends() {
        val currentId = CurrentUser.user?.id ?: return
        try {
            val resp = FriendRepository.getFriends(currentId)
            if (resp.isSuccessful) {
                val list = resp.body() ?: emptyList()
               val uiList = list.map { remote ->
                    FriendUi(
                        name = remote.name,
                        score = remote.points.toString(),
                        avatarRes = com.app.mathracer.R.drawable.avatar,
                        carRes = com.app.mathracer.R.drawable.car
                    )
                }
                _uiState.update { it.copy(friends = uiList, remoteFriends = list) }
            } else {
               val cached = CurrentUser.cachedFriends
                if (!cached.isNullOrEmpty()) {
                    val uiList = cached.map { remote ->
                        FriendUi(
                            name = remote.name,
                            score = remote.points.toString(),
                            avatarRes = com.app.mathracer.R.drawable.avatar,
                            carRes = com.app.mathracer.R.drawable.car
                        )
                    }
                    _uiState.update { it.copy(friends = uiList, remoteFriends = cached) }
                } else {
                    _uiState.update { it.copy(friends = emptyList()) }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val cached = CurrentUser.cachedFriends
            if (!cached.isNullOrEmpty()) {
                val uiList = cached.map { remote ->
                    FriendUi(
                        name = remote.name,
                        score = remote.points.toString(),
                        avatarRes = com.app.mathracer.R.drawable.avatar,
                        carRes = com.app.mathracer.R.drawable.car
                    )
                }
                _uiState.update { it.copy(friends = uiList, remoteFriends = cached) }
            } else {
                _uiState.update { it.copy(friends = emptyList()) }
            }
        }
    }

    private suspend fun loadPending() {
        val currentId = CurrentUser.user?.id ?: return
        try {
            val resp = FriendRepository.getPending(currentId)
            if (resp.isSuccessful) {
                val list = resp.body() ?: emptyList()
                _uiState.update { it.copy(pending = list) }
            } else {
                _uiState.update { it.copy(pending = emptyList()) }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _uiState.update { it.copy(pending = emptyList()) }
        }
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

    fun inviteByPlayerId(toPlayerId: Int, onComplete: (Boolean, String?) -> Unit = { _, _ -> }) {
        val fromId = CurrentUser.user?.id
        if (fromId == null) {
            onComplete(false, "Usuario actual no identificado")
            return
        }
        viewModelScope.launch {
            try {
                val resp = FriendRepository.sendRequest(fromId, toPlayerId)
                if (resp.isSuccessful) {
                    onComplete(true, null)
                } else {
                    val err = resp.errorBody()?.string()
                    onComplete(false, "Error backend: code=${resp.code()} ${err ?: ""}")
                }
                refreshAll()
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false, "Exception: ${e.message}")
            }
        }
    }

    fun acceptRequest(fromPlayerId: Int, onComplete: (Boolean, String?) -> Unit = { _, _ -> }) {
        val toId = CurrentUser.user?.id
        if (toId == null) {
            onComplete(false, "Usuario actual no identificado")
            return
        }
        viewModelScope.launch {
            try {
                val resp = FriendRepository.acceptRequest(fromPlayerId, toId)
                if (resp.isSuccessful) {
                    onComplete(true, null)
                } else {
                    val err = resp.errorBody()?.string()
                    onComplete(false, "Error backend: code=${resp.code()} ${err ?: ""}")
                }
                refreshAll()
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false, "Exception: ${e.message}")
            }
        }
    }

    fun rejectRequest(fromPlayerId: Int, onComplete: (Boolean, String?) -> Unit = { _, _ -> }) {
        val toId = CurrentUser.user?.id
        if (toId == null) {
            onComplete(false, "Usuario actual no identificado")
            return
        }
        viewModelScope.launch {
            try {
                val resp = FriendRepository.rejectRequest(fromPlayerId, toId)
                if (resp.isSuccessful) {
                    onComplete(true, null)
                } else {
                    val err = resp.errorBody()?.string()
                    onComplete(false, "Error backend: code=${resp.code()} ${err ?: ""}")
                }
                refreshAll()
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false, "Exception: ${e.message}")
            }
        }
    }

    fun deleteFriend(friendRemoteId: Int, onComplete: (Boolean) -> Unit = {}) {
        val currentId = CurrentUser.user?.id ?: run { onComplete(false); return }
        viewModelScope.launch {
            try {
                val resp = FriendRepository.deleteFriend(currentId, friendRemoteId)
                onComplete(resp.isSuccessful)
                refreshAll()
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete(false)
            }
        }
    }

    fun onLogout() {
        
    }

    fun onDeleteAccount() {
        // TODO: eliminar cuenta en backend
    }
}