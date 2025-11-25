package com.app.mathracer.ui.screens.garage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.app.mathracer.data.repository.GarageRepository
import com.app.mathracer.data.network.GarageItemDto
import com.app.mathracer.data.network.GarageResponseDto
import com.app.mathracer.data.CurrentUser
import android.util.Log

data class GarageUiState(
    val cars: List<GarageItemDto> = emptyList(),
    val characters: List<GarageItemDto> = emptyList(),
    val backgrounds: List<GarageItemDto> = emptyList(),
    val activeCar: GarageItemDto? = null,
    val activeCharacter: GarageItemDto? = null,
    val activeBackground: GarageItemDto? = null,
    val loading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GarageViewModel @Inject constructor(
    private val repository: GarageRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GarageUiState(loading = true))
    val uiState: StateFlow<GarageUiState> = _uiState

    init {
        val playerId = CurrentUser.user?.id ?: 0
        if (playerId > 0) loadAll(playerId)
    }

    fun loadAll(playerId: Int) {
        _uiState.value = _uiState.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                val carsRes = repository.getCars(playerId)
                val charsRes = repository.getCharacters(playerId)
                val bgsRes = repository.getBackgrounds(playerId)

               
                val activeCar = carsRes.getOrNull()?.activeItem
                val activeChar = charsRes.getOrNull()?.activeItem
                val activeBg = bgsRes.getOrNull()?.activeItem

                 
                val cars = carsRes.getOrElse { GarageResponseDto() }.items
                    .filter { it.isOwned }
                    .distinctBy { it.productId }

                val chars = charsRes.getOrElse { GarageResponseDto() }.items
                    .filter { it.isOwned }
                    .distinctBy { it.productId }

                val bgs = bgsRes.getOrElse { GarageResponseDto() }.items
                    .filter { it.isOwned }
                    .distinctBy { it.productId }

                _uiState.value = GarageUiState(
                    cars = cars,
                    characters = chars,
                    backgrounds = bgs,
                    activeCar = activeCar,
                    activeCharacter = activeChar,
                    activeBackground = activeBg,
                    loading = false
                )
            } catch (e: Exception) {
                Log.e("GarageViewModel", "loadAll failed", e)
                _uiState.value = _uiState.value.copy(loading = false, error = e.localizedMessage)
            }
        }
    }

    fun activate(playerId: Int, productId: Int, productType: String) {
        viewModelScope.launch {
            val result = repository.activateItem(playerId, productId, productType)
            if (result.isSuccess) {
                when (productType) {
                    "Auto" -> {
                        val carsRes = repository.getCars(playerId)
                        val activeCar = carsRes.getOrNull()?.activeItem
                        val cars = carsRes.getOrElse { GarageResponseDto() }.items
                            .filter { it.isOwned }
                            .distinctBy { it.productId }
                        _uiState.value = _uiState.value.copy(cars = cars, activeCar = activeCar)
                    }
                    "Personaje" -> {
                        val charsRes = repository.getCharacters(playerId)
                        val activeChar = charsRes.getOrNull()?.activeItem
                        val chars = charsRes.getOrElse { GarageResponseDto() }.items
                            .filter { it.isOwned }
                            .distinctBy { it.productId }
                        _uiState.value = _uiState.value.copy(characters = chars, activeCharacter = activeChar)
                    }
                    "Fondo" -> {
                        val bgsRes = repository.getBackgrounds(playerId)
                        val activeBg = bgsRes.getOrNull()?.activeItem
                        val bgs = bgsRes.getOrElse { GarageResponseDto() }.items
                            .filter { it.isOwned }
                            .distinctBy { it.productId }
                        _uiState.value = _uiState.value.copy(backgrounds = bgs, activeBackground = activeBg)
                    }
                    else -> loadAll(playerId)
                }
            } else {
                Log.e("GarageViewModel", "activate failed: ${result.exceptionOrNull()}")
            }
        }
    }
}
