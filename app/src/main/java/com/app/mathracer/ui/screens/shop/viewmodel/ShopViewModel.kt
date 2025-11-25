package com.app.mathracer.ui.screens.shop.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.data.CurrentUser
import com.app.mathracer.data.UserState
import com.app.mathracer.data.repository.UserRemoteRepository
import com.app.mathracer.data.network.ItemDto
import com.app.mathracer.data.network.ShopResponse
import com.app.mathracer.data.network.ShopResponseEnergies
import com.app.mathracer.data.network.ShopResponseWildcards
import com.app.mathracer.data.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.String


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
        _uiState.update { it.copy(loading = true, error = null) }
        viewModelScope.launch {
            try {
                val carsRes = repository.getCars(playerId)
                val charsRes = repository.getCharacters(playerId)
                val bgsRes = repository.getBackgrounds(playerId)
                val comodinesRes = repository.getComodines(playerId) // <- ajustá si se llama distinto
                val energiesRes = repository.getEneries(playerId)    // <- nombre según tu repo

                val cars = carsRes.getOrElse { ShopResponse() }.items
                    .filter { !it.isOwned }
                    .distinctBy { it.id }

                val chars = charsRes.getOrElse { ShopResponse() }.items
                    .filter { !it.isOwned }
                    .distinctBy { it.id }

                val bgs = bgsRes.getOrElse { ShopResponse() }.items
                    .filter { !it.isOwned }
                    .distinctBy { it.id }

                val comodines = comodinesRes.getOrElse {
                    ShopResponseWildcards(
                        id = 0,
                        name = "",
                        description= "",
                        price = 0,
                        currentQuantity = 0
                    )
                }

                val energies = energiesRes.getOrElse {
                    ShopResponseEnergies(
                        pricePerUnit = 0,
                        maxAmount = 0,
                        currentAmount = 0,
                        maxCanBuy = 0
                    )
                }

                _uiState.update {
                    it.copy(
                        cars = cars,
                        characters = chars,
                        backgrounds = bgs,
                        comodines = comodines as List<ShopResponseWildcards>,
                        energies = energies,
                        loading = false,
                        coins = CurrentUser.user?.coins ?: it.coins
                    )
                }
            } catch (e: Exception) {
                Log.e("ShopViewModel", "loadAll failed", e)
                _uiState.update {
                    it.copy(
                        loading = false,
                        error = e.localizedMessage
                    )
                }
            }
        }
    }

    // Compra genérica para auto / fondo / personaje
    fun buyItem(playerId: Int, item: ItemDto?) {
        if (item == null) return
        Log.d("ShopRepository", "purchaseCar: imte=$item")
        viewModelScope.launch {
            Log.d("ShopViewModel", "buyItem invoked: playerId=$playerId, itemId=${item.id}, type=${item.productTypeName}")
            val result = when (item.productTypeName) {
                "Fondo" -> repository.purchaseBackground(
                    playerId = playerId,
                    backgroundId = item.id
                )

                "Auto" -> repository.purchaseCar(
                    playerId = playerId,
                    carId = item.id
                )

                "Personaje" -> repository.purchaseCharacter(
                    playerId = playerId,
                    characterId = item.id
                )

                else -> {
                    Log.d("ShopViewModel", "Tipo de producto no soportado: ${item.productTypeName}")
                    return@launch
                }
            }

            result
                .onSuccess { response ->
                    // update CurrentUser and ui state if backend returned remainingCoins
                    try {
                        response.remainingCoins?.let { rc ->
                            Log.d("ShopViewModel", "purchase success remainingCoins from backend: $rc")
                            UserState.setCoins(rc)
                            Log.d("ShopViewModel", "UserState.coins after set: ${com.app.mathracer.data.UserState}")
                            _uiState.update { it.copy(coins = rc, purchaseMessage = "Compra exitosa. Monedas restantes: $rc") }
                        } ?: run {
                            Log.d("ShopViewModel", "purchase success but remainingCoins is null")
                        }
                    } catch (e: Exception) {
                        Log.e("ShopViewModel", "Error updating coins after purchase", e)
                    }
                     
                    try {
                        viewModelScope.launch {
                            try {
                                Log.d("ShopViewModel", "Refreshing user after purchase by playerId=$playerId")
                                val userResp = UserRemoteRepository.getUserByPlayerId(playerId)
                                if (userResp.isSuccessful) {
                                    val u = userResp.body()
                                    u?.let {
                                        CurrentUser.user = it
                                        UserState.setCoins(it.coins ?: 0)
                                        _uiState.update { st -> st.copy(coins = it.coins ?: st.coins) }
                                        Log.d("ShopViewModel", "User refreshed after purchase: coins=${it.coins}")
                                    }
                                } else {
                                    Log.w("ShopViewModel", "Failed to refresh user after purchase by playerId: ${userResp.code()}")
                                }
                            } catch (e: Exception) {
                                Log.e("ShopViewModel", "Error fetching user after purchase by playerId", e)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ShopViewModel", "Unexpected error scheduling user refresh by playerId", e)
                    }

                    loadAll(playerId)
                }
                .onFailure { e ->
                    Log.e("ShopViewModel", "Error al comprar ${item.productTypeName}", e)
                }
        }
    }

    // Atajos si querés llamarlos directo desde la UI
    fun buyCar(playerId: Int, item: ItemDto) {
        Log.d("ShopViewModel", "buyCar: playerId=$playerId, car=${item.id}")
        buyItem(playerId, item)
    }

    fun buyBackground(playerId: Int, item: ItemDto) = buyItem(playerId, item)

    fun buyCharacter(playerId: Int, item: ItemDto) = buyItem(playerId, item)

    // ENERGÍA
    fun buyEnergy(playerId: Int) {
        viewModelScope.launch {
            Log.d("ShopViewModel", "buyEnergy invoked: playerId=$playerId, quantity=1")
            val result = repository.purchaseEnergy(
                playerId = playerId,
                quantity = 1
            )
            result
                .onSuccess { response ->

                    try {
                        val msg = response.message ?: "Compra exitosa"
                        response.remainingCoins?.let { rc ->
                            Log.d("ShopViewModel", "buyEnergy remainingCoins: $rc")
                            UserState.setCoins(rc)
                            _uiState.update { it.copy(coins = rc, purchaseMessage = "$msg. Monedas restantes: $rc") }
                        } ?: run {
                            Log.d("ShopViewModel", "buyEnergy success but remainingCoins null")
                            _uiState.update { it.copy(purchaseMessage = response.message ?: "Compra exitosa") }
                        }
                    } catch (e: Exception) {
                        Log.e("ShopViewModel", "Error in buyEnergy handling", e)
                    }
                    try {
                        viewModelScope.launch {
                            try {
                                Log.d("ShopViewModel", "Refreshing user after buyEnergy by playerId=$playerId")
                                val userResp = UserRemoteRepository.getUserByPlayerId(playerId)
                                if (userResp.isSuccessful) {
                                    val u = userResp.body()
                                    u?.let {
                                        CurrentUser.user = it
                                        UserState.setCoins(it.coins ?: 0)
                                        _uiState.update { st -> st.copy(coins = it.coins ?: st.coins) }
                                        Log.d("ShopViewModel", "User refreshed after buyEnergy: coins=${it.coins}")
                                    }
                                } else {
                                    Log.w("ShopViewModel", "Failed to refresh user after buyEnergy by playerId: ${userResp.code()}")
                                }
                            } catch (e: Exception) {
                                Log.e("ShopViewModel", "Error fetching user after buyEnergy by playerId", e)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ShopViewModel", "Unexpected error scheduling user refresh after buyEnergy by playerId", e)
                    }
                    loadAll(playerId)
                }
                .onFailure { e ->
                    Log.e("ShopViewModel", "Error al comprar energía", e)
                }
        }
    }

    // COMODÍN
    fun buyComodin(playerId: Int, item: ItemDto) {
        viewModelScope.launch {
            Log.d("ShopViewModel", "buyComodin invoked: playerId=$playerId, wildcardId=${item.id}, quantity=1")
            val result = repository.purchaseWildcard(
                playerId = playerId,
                wildcardId = item.id,
                quantity = 1
            ) // <- ajustá nombre/params

            result
                .onSuccess { response ->

                    try {
                        val msg = response.message ?: "Compra exitosa"
                        response.remainingCoins?.let { rc ->
                            Log.d("ShopViewModel", "buyComodin remainingCoins: $rc")
                            UserState.setCoins(rc)
                            _uiState.update { it.copy(coins = rc, purchaseMessage = "$msg. Monedas restantes: $rc") }
                        } ?: run {
                            Log.d("ShopViewModel", "buyComodin success but remainingCoins null")
                            _uiState.update { it.copy(purchaseMessage = msg) }
                        }
                    } catch (e: Exception) {
                        Log.e("ShopViewModel", "Error in buyComodin handling", e)
                    }
                    try {
                        viewModelScope.launch {
                            try {
                                Log.d("ShopViewModel", "Refreshing user after buyComodin by playerId=$playerId")
                                val userResp = UserRemoteRepository.getUserByPlayerId(playerId)
                                if (userResp.isSuccessful) {
                                    val u = userResp.body()
                                    u?.let {
                                        CurrentUser.user = it
                                        UserState.setCoins(it.coins ?: 0)
                                        _uiState.update { st -> st.copy(coins = it.coins ?: st.coins) }
                                        Log.d("ShopViewModel", "User refreshed after buyComodin: coins=${it.coins}")
                                    }
                                } else {
                                    Log.w("ShopViewModel", "Failed to refresh user after buyComodin by playerId: ${userResp.code()}")
                                }
                            } catch (e: Exception) {
                                Log.e("ShopViewModel", "Error fetching user after buyComodin by playerId", e)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ShopViewModel", "Unexpected error scheduling user refresh after buyComodin by playerId", e)
                    }
                    loadAll(playerId)
                }
                .onFailure { e ->
                    Log.e("ShopViewModel", "Error al comprar comodín", e)
                }
        }
    }

    // Comprar comodín por id (cuando UI proporciona ShopResponseWildcards)
    fun buyWildcardById(playerId: Int, wildcardId: Int) {
        viewModelScope.launch {
            Log.d("ShopViewModel", "buyWildcardById invoked: playerId=$playerId, wildcardId=$wildcardId")
            val result = repository.purchaseWildcard(
                playerId = playerId,
                wildcardId = wildcardId,
                quantity = 1
            )
            result
                .onSuccess { response ->
                    try {
                        val msg = response.message ?: "Compra exitosa"
                        response.remainingCoins?.let { rc ->
                            Log.d("ShopViewModel", "buyWildcardById remainingCoins: $rc")
                            UserState.setCoins(rc)
                            _uiState.update { it.copy(coins = rc, purchaseMessage = "$msg. Monedas restantes: $rc") }
                        } ?: run {
                            Log.d("ShopViewModel", "buyWildcardById success but remainingCoins null")
                            _uiState.update { it.copy(purchaseMessage = msg) }
                        }
                    } catch (e: Exception) {
                        Log.e("ShopViewModel", "Error in buyWildcardById handling", e)
                    }
                    try {
                        viewModelScope.launch {
                            try {
                                Log.d("ShopViewModel", "Refreshing user after buyWildcardById by playerId=$playerId")
                                val userResp = UserRemoteRepository.getUserByPlayerId(playerId)
                                if (userResp.isSuccessful) {
                                    val u = userResp.body()
                                    u?.let {
                                        CurrentUser.user = it
                                        UserState.setCoins(it.coins ?: 0)
                                        _uiState.update { st -> st.copy(coins = it.coins ?: st.coins) }
                                        Log.d("ShopViewModel", "User refreshed after buyWildcardById: coins=${it.coins}")
                                    }
                                } else {
                                    Log.w("ShopViewModel", "Failed to refresh user after buyWildcardById by playerId: ${userResp.code()}")
                                }
                            } catch (e: Exception) {
                                Log.e("ShopViewModel", "Error fetching user after buyWildcardById by playerId", e)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("ShopViewModel", "Unexpected error scheduling user refresh after buyWildcardById by playerId", e)
                    }
                    loadAll(playerId)
                }
                .onFailure { e ->
                    Log.e("ShopViewModel", "Error al comprar comodín por id", e)
                }
        }
    }

    fun clearPurchaseMessage() {
        _uiState.update { it.copy(purchaseMessage = null) }
    }
}

