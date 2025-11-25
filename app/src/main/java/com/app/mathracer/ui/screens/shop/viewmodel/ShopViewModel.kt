package com.app.mathracer.ui.screens.shop.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.mathracer.data.CurrentUser
import com.app.mathracer.data.network.ItemDto
import com.app.mathracer.data.network.ShopResponse
import com.app.mathracer.data.network.ShopResponseEnergies
import com.app.mathracer.data.network.ShopResponseWildcards
import com.app.mathracer.data.repositories.ShopRepository
import com.app.mathracer.data.network.CoinPackageDto
import com.app.mathracer.BuildConfig
import com.google.gson.JsonObject
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
                val coinPackagesRes = repository.getCoinPackages()

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

                val coinPackages = coinPackagesRes.getOrElse { emptyList() }

                _uiState.update {
                    it.copy(
                        cars = cars,
                        characters = chars,
                        backgrounds = bgs,
                        comodines = comodines as List<ShopResponseWildcards>,
                        coinPackages = coinPackages,
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

    // Compra de paquete de monedas — aquí puedes redirigir a la pasarela de pago
    fun buyCoinPackage(playerId: Int, pkg: CoinPackageDto) {
        viewModelScope.launch {
            try {
                // Llamamos al backend para crear la preferencia de pago
                val result = repository.createPaymentPreference(
                    playerId = playerId,
                    coinPackageId = pkg.id,
                    successUrl = "app://payments/success",
                    failureUrl = "app://payments/failure",
                    pendingUrl = "app://payments/pending"
                )

                result
                    .onSuccess { json: JsonObject ->
                        // Intentamos extraer un URL de redirección común (init_point / sandbox_init_point / url / preferenceUrl)
                        var url: String? = when {
                            json.has("initPoint") -> json.get("initPoint").asString
                            json.has("sandbox_init_point") -> json.get("sandbox_init_point").asString
                            json.has("url") -> json.get("url").asString
                            json.has("preferenceUrl") -> json.get("preferenceUrl").asString
                            else -> null
                        }

                        // Si backend solo devuelve PreferenceId (como en tu controller), construimos la URL de MercadoPago
                        if (url.isNullOrBlank()) {
                            val prefId = when {
                                json.has("PreferenceId") -> json.get("PreferenceId").asString
                                json.has("preferenceId") -> json.get("preferenceId").asString
                                json.has("preference_id") -> json.get("preference_id").asString
                                else -> null
                            }

                            if (!prefId.isNullOrBlank()) {
                                val base = if (BuildConfig.DEBUG) "https://sandbox.mercadopago.com/checkout/v1/redirect?pref_id=" else "https://www.mercadopago.com/checkout/v1/redirect?pref_id="
                                url = base + prefId
                            }
                        }

                        if (!url.isNullOrBlank()) {
                            _uiState.update { it.copy(paymentRedirectUrl = url) }
                        } else {
                            _uiState.update { it.copy(purchaseMessage = "Preferencia creada, pero no se recibió URL") }
                        }
                    }
                    .onFailure { e ->
                        Log.e("ShopViewModel", "Error al crear preferencia de pago", e)
                        _uiState.update { it.copy(purchaseMessage = "Error al iniciar pago: ${e.localizedMessage}") }
                    }

            } catch (e: Exception) {
                Log.e("ShopViewModel", "Error al procesar compra de paquete", e)
                _uiState.update { it.copy(purchaseMessage = "Error al comprar paquete") }
            }
        }
    }

    fun clearPaymentRedirect() {
        _uiState.update { it.copy(paymentRedirectUrl = null) }
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
                            CurrentUser.user?.coins = rc
                            _uiState.update { it.copy(coins = rc, purchaseMessage = "Compra exitosa. Monedas restantes: $rc") }
                        }
                    } catch (_: Exception) {}
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
                            CurrentUser.user?.coins = rc
                            _uiState.update { it.copy(coins = rc, purchaseMessage = "$msg. Monedas restantes: $rc") }
                        } ?: run {
                            _uiState.update { it.copy(purchaseMessage = response.message ?: "Compra exitosa") }
                        }
                    } catch (_: Exception) {}
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
                            CurrentUser.user?.coins = rc
                            _uiState.update { it.copy(coins = rc, purchaseMessage = "$msg. Monedas restantes: $rc") }
                        } ?: run {
                            _uiState.update { it.copy(purchaseMessage = msg) }
                        }
                    } catch (_: Exception) {}
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
                            CurrentUser.user?.coins = rc
                            _uiState.update { it.copy(coins = rc, purchaseMessage = "$msg. Monedas restantes: $rc") }
                        } ?: run {
                            _uiState.update { it.copy(purchaseMessage = msg) }
                        }
                    } catch (_: Exception) {}
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

    // Manejar resultado del pago desde deep link
    fun handlePaymentResult(status: String, playerId: Int) {
        viewModelScope.launch {
            when (status) {
                "success" -> {
                    Log.d("ShopViewModel", "✅ Pago exitoso - Recargando monedas del jugador")
                    _uiState.update { it.copy(purchaseMessage = "¡Pago exitoso! Recargando monedas...") }
                    // Recargar los datos del jugador para actualizar las monedas
                    loadAll(playerId)
                }
                "failure" -> {
                    Log.d("ShopViewModel", "❌ Pago fallido")
                    _uiState.update { it.copy(purchaseMessage = "El pago no se pudo completar. Intenta nuevamente.") }
                }
                "pending" -> {
                    Log.d("ShopViewModel", "⏳ Pago pendiente")
                    _uiState.update { it.copy(purchaseMessage = "Tu pago está pendiente de confirmación.") }
                }
            }
        }
    }
}

