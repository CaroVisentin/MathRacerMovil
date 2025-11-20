package com.app.mathracer.ui.screens.shop.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.data.CurrentUser
import com.app.mathracer.data.network.ItemDto
import com.app.mathracer.data.network.ShopResponse
import com.app.mathracer.data.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
class ShopViewModel @Inject constructor(
    private val repository: ShopRepository
) : ViewModel() {


    private val _uiState = MutableStateFlow(ShopUiState(loading = true))
    val uiState: StateFlow<ShopUiState> = _uiState

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

                val cars = carsRes.getOrElse { ShopResponse() }.items
                    .filter { it.isOwned }
                    .distinctBy { it.id }

                val chars = charsRes.getOrElse { ShopResponse() }.items
                    .filter { it.isOwned }
                    .distinctBy { it.id }

                val bgs = bgsRes.getOrElse { ShopResponse() }.items
                    .filter { it.isOwned }
                    .distinctBy { it.id }

                _uiState.value = ShopUiState(
                    cars = cars,
                    characters = chars,
                    backgrounds = bgs,
                    loading = false,
                    coins = CurrentUser.user?.coins ?: 0,
                )
            } catch (e: Exception) {
                Log.e("GarageViewModel", "loadAll failed", e)
                _uiState.value = _uiState.value.copy(loading = false, error = e.localizedMessage)
            }
        }
    }

    fun buyItem(playerId: Int, item: ItemDto?) {
        viewModelScope.launch {
            if(item != null) {
                when (item.productTypeName.lowercase()) {
                    "background" -> {
                        val result = repository.purchaseBackground(
                            playerId = playerId,
                            backgroundId = item.id
                        )
                        result.onSuccess { response ->
                            _uiState.update {
                                it.copy(
                                    coins = response.remainingCoins
                                )
                            }
                            loadAll(playerId)
                        }
                    }

                    "car" -> {
                        val result = repository.purchaseCar(
                            playerId = playerId,
                            carId = item.id
                        )
                        result.onSuccess { response ->
                            _uiState.update {
                                it.copy(
                                    coins = response.remainingCoins
                                )
                            }
                            loadAll(playerId)
                        }
                    }

                    "character" -> {
                        val result = repository.purchaseCharacter(
                            playerId = playerId,
                            characterId = item.id
                        )
                        result.onSuccess { response ->
                            _uiState.update {
                                it.copy(
                                    coins = response.remainingCoins
                                )
                            }
                            loadAll(playerId)
                        }
                    }

                    else -> {
                        Log.d(
                            "error purchase",
                            "Tipo de producto no soportado: ${item.productTypeName}"
                        )
                    }
                }
            }

        }
    }
}