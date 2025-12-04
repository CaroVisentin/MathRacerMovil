package com.app.mathracer.ui.screens.profile.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.R
import com.app.mathracer.audio.MusicManager
import com.app.mathracer.data.CurrentUser
import com.app.mathracer.data.model.Player
import com.app.mathracer.data.repository.FriendRepository
import com.app.mathracer.ui.screens.profile.components.Friend as FriendUi
import com.app.mathracer.data.repository.GarageRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        val initialMusicVolume = 0.5f
        _uiState.update {
            it.copy(
                userName = CurrentUser.user?.name ?: "",
                userEmail = CurrentUser.user?.email,
                points = CurrentUser.user?.points ?: 0,
                actualLevel = CurrentUser.user?.lastLevelId ?: 0,
                musicVolume = initialMusicVolume
            )
        }
        MusicManager.setMusicVolume(initialMusicVolume)
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
                val repo = GarageRepository()
                val uiList = list.map { remote ->
                    FriendUi(
                        name = remote.name,
                        score = remote.points.toString(),
                        avatarRes = R.drawable.avatar,
                        carRes = R.drawable.car,
                        avatarProductId = null,
                        carProductId = null
                    )
                }
                _uiState.update { it.copy(friends = uiList, remoteFriends = list) }

                try {
                    coroutineScope {
                        val deferred = list.map { remote ->
                            async {
                                var avatarId: Int? = null
                                var carId: Int? = null
                                try {
                                    val chars = repo.getCharacters(remote.id)
                                    avatarId = chars.getOrNull()?.activeItem?.productId
                                } catch (_: Exception) { }
                                try {
                                    val cars = repo.getCars(remote.id)
                                    carId = cars.getOrNull()?.activeItem?.productId
                                } catch (_: Exception) { }
                                Pair(remote.id, Pair(avatarId, carId))
                            }
                        }

                        val results = deferred.map { it.await() }.toMap()
                        val updated = list.map { remote ->
                            val ids = results[remote.id]
                            FriendUi(
                                name = remote.name,
                                score = remote.points.toString(),
                                avatarRes = R.drawable.avatar,
                                carRes = R.drawable.car,
                                avatarProductId = ids?.first,
                                carProductId = ids?.second
                            )
                        }
                        _uiState.update { it.copy(friends = updated, remoteFriends = list) }
                    }
                } catch (_: Exception) { /* best effort; ignore errors */ }
            } else {
               val cached = CurrentUser.cachedFriends
                if (!cached.isNullOrEmpty()) {
                    val repo = GarageRepository()
                    val uiList = cached.map { remote ->
                        FriendUi(
                            name = remote.name,
                            score = remote.points.toString(),
                            avatarRes = R.drawable.avatar,
                            carRes = R.drawable.car,
                            avatarProductId = null,
                            carProductId = null
                        )
                    }
                    _uiState.update { it.copy(friends = uiList, remoteFriends = cached) }
                    try {
                        coroutineScope {
                            val deferred = cached.map { remote ->
                                async {
                                    var avatarId: Int? = null
                                    var carId: Int? = null
                                    try { avatarId = repo.getCharacters(remote.id).getOrNull()?.activeItem?.productId } catch (_: Exception) {}
                                    try { carId = repo.getCars(remote.id).getOrNull()?.activeItem?.productId } catch (_: Exception) {}
                                    Pair(remote.id, Pair(avatarId, carId))
                                }
                            }
                            val results = deferred.map { it.await() }.toMap()
                            val updated = cached.map { remote ->
                                val ids = results[remote.id]
                                FriendUi(
                                    name = remote.name,
                                    score = remote.points.toString(),
                                    avatarRes = R.drawable.avatar,
                                    carRes = R.drawable.car,
                                    avatarProductId = ids?.first,
                                    carProductId = ids?.second
                                )
                            }
                            _uiState.update { it.copy(friends = updated, remoteFriends = cached) }
                        }
                    } catch (_: Exception) { }
                } else {
                    _uiState.update { it.copy(friends = emptyList()) }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val cached = CurrentUser.cachedFriends

            if (!cached.isNullOrEmpty()) {
                val repo = GarageRepository()
                val uiList = cached.map { remote ->
                    FriendUi(
                        name = remote.name,
                        score = remote.points.toString(),
                        avatarRes = R.drawable.avatar,
                        carRes = R.drawable.car,
                        avatarProductId = null,
                        carProductId = null
                    )
                }
                _uiState.update { it.copy(friends = uiList, remoteFriends = cached) }
                try {
                    coroutineScope {
                        val deferred = cached.map { remote ->
                            async {
                                var avatarId: Int? = null
                                var carId: Int? = null
                                try { avatarId = repo.getCharacters(remote.id).getOrNull()?.activeItem?.productId } catch (_: Exception) {}
                                try { carId = repo.getCars(remote.id).getOrNull()?.activeItem?.productId } catch (_: Exception) {}
                                Pair(remote.id, Pair(avatarId, carId))
                            }
                        }
                        val results = deferred.map { it.await() }.toMap()
                        val updated = cached.map { remote ->
                            val ids = results[remote.id]
                            FriendUi(
                                name = remote.name,
                                score = remote.points.toString(),
                                avatarRes = R.drawable.avatar,
                                carRes = R.drawable.car,
                                avatarProductId = ids?.first,
                                carProductId = ids?.second
                            )
                        }
                        _uiState.update { it.copy(friends = updated, remoteFriends = cached) }
                    }
                } catch (_: Exception) { }
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
        _uiState.update { it.copy(soundVolume = value.coerceIn(0f, 1f)) }
    }

    fun onMusicVolumeChange(value: Float) {
        val clamped = value.coerceIn(0f, 1f)
        _uiState.update { it.copy(musicVolume = clamped) }

        MusicManager.setMusicVolume(clamped)
    }

    fun searchPlayer(email: String) {
        viewModelScope.launch {
            try {
                val resp = FriendRepository.getPlayer(email)
                Log.d("friend", "${resp.body()}")
                if (resp.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            friendToSearch = Player(
                                name = resp.body()?.name ?: "",
                                id =  resp.body()?.id ?: 0,
                                email =  resp.body()?.email ?: "",
                                coins =  resp.body()?.coins ?: 0,
                                points =  resp.body()?.points ?: 0,
                                character =  resp.body()?.character
                            ))
                    }
                    Log.d("friend", "aca ${resp.body()}")
                } else {
                    _uiState.update {
                        it.copy(
                            friendToSearch = null
                        )
                    }
                }
            } catch (e: Exception) {
                Log.d("friend", "catch")

                e.printStackTrace()
            }
        }
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
                    onComplete(false, "Usuario actual no identificado")
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
                    onComplete(false, "El usuario ya es un amigo.")
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
                    onComplete(false, "El usuario ya es un amigo.")
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
        val previousState = _uiState.value
        val updatedRemote = previousState.remoteFriends.filterNot { it.id == friendRemoteId }
        val updatedUi = updatedRemote.map { remote ->
            FriendUi(
                name = remote.name,
                score = remote.points.toString(),
                avatarRes = R.drawable.avatar,
                carRes = R.drawable.car,
                avatarProductId = null,
                carProductId = null
            )
        }
        _uiState.update { it.copy(friends = updatedUi, remoteFriends = updatedRemote) }

        viewModelScope.launch {
            try {
                val resp = FriendRepository.deleteFriend(currentId, friendRemoteId)
                if (!resp.isSuccessful) {
                    _uiState.update { previousState }
                }
                onComplete(resp.isSuccessful)
                refreshAll()
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { previousState }
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