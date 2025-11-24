package com.app.mathracer.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object UserState {
    private val _activeCharacter = MutableStateFlow<Int?>(CurrentUser.activeCharacterProductId)
    val activeCharacter = _activeCharacter.asStateFlow()

    private val _activeBackground = MutableStateFlow<Int?>(CurrentUser.activeBackgroundProductId)
    val activeBackground = _activeBackground.asStateFlow()

    private val _activeVehicle = MutableStateFlow<Int?>(CurrentUser.activeVehicleProductId)
    val activeVehicle = _activeVehicle.asStateFlow()

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
}
