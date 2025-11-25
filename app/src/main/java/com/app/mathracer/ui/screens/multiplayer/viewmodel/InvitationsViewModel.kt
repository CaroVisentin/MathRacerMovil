package com.app.mathracer.ui.screens.multiplayer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.data.network.ApiService
import com.app.mathracer.data.repository.GameInvitationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class InvitationsUiState(
    val invitations: List<ApiService.GameInvitationDto> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null
)

class InvitationsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(InvitationsUiState())
    val uiState: StateFlow<InvitationsUiState> = _uiState.asStateFlow()

    fun loadInbox() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            try {
                val resp = GameInvitationRepository.getInbox()
                if (resp.isSuccessful) {
                    val body = resp.body()
                    val list = body?.invitations ?: emptyList()
                    _uiState.value = _uiState.value.copy(invitations = list, loading = false)
                } else {
                    _uiState.value = _uiState.value.copy(error = "Error backend: ${resp.code()}", loading = false)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message ?: "Exception", loading = false)
            }
        }
    }

    fun respond(invitationId: Int, accept: Boolean, onComplete: (Boolean, String?) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            try {
                val resp = GameInvitationRepository.respondInvitation(invitationId, accept)
                if (resp.isSuccessful) {
                    // refresh inbox
                    loadInbox()
                    onComplete(true, null)
                } else {
                    val err = resp.errorBody()?.string()
                    onComplete(false, "Error backend: code=${resp.code()} ${err ?: ""}")
                }
            } catch (e: Exception) {
                onComplete(false, e.message ?: "Exception")
            }
        }
    }
}
