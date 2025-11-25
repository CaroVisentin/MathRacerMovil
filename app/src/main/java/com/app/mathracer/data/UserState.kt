package com.app.mathracer.data

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserState {
    private val _activeCharacter = MutableStateFlow<Int?>(CurrentUser.activeCharacterProductId)
    val activeCharacter = _activeCharacter.asStateFlow()

    private val _activeBackground = MutableStateFlow<Int?>(CurrentUser.activeBackgroundProductId)
    val activeBackground = _activeBackground.asStateFlow()

    private val _activeVehicle = MutableStateFlow<Int?>(CurrentUser.activeVehicleProductId)
    val activeVehicle = _activeVehicle.asStateFlow()

    private val _coins = MutableStateFlow<Int>(CurrentUser.user?.coins ?: 0)
    val coins = _coins.asStateFlow()

    fun setActiveCharacter(productId: Int?) {
        CurrentUser.activeCharacterProductId = productId
        _activeCharacter.value = productId
    }

    fun setActiveBackground(productId: Int?) {
        CurrentUser.activeBackgroundProductId = productId
        _activeBackground.value = productId
    }

    fun setActiveVehicle(productId: Int?) {
        CurrentUser.activeVehicleProductId = productId
        _activeVehicle.value = productId
    }

    fun setCoins(amount: Int) {
        Log.d("UserState", "setCoins called: $amount (prev CurrentUser=${CurrentUser.user?.coins})")
        CurrentUser.user?.coins = amount
        _coins.value = amount
        Log.d("UserState", "setCoins applied: ${_coins.value} (CurrentUser=${CurrentUser.user?.coins})")
    }
}
