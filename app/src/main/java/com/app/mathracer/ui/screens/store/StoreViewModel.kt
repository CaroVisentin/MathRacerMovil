package com.app.mathracer.ui.screens.store

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class StoreItem(val id: String, val title: String, val price: Int)

class StoreViewModel : ViewModel() {
    private val _coins = MutableStateFlow(123500)
    val coins: StateFlow<Int> = _coins

    private val _items = MutableStateFlow(
        listOf(
            StoreItem("car_1", "Auto Azul", 5000),
            StoreItem("car_2", "Auto Naranja", 5000),
            StoreItem("car_3", "Auto Amarillo", 5000),
            StoreItem("car_4", "Auto Retro", 5000),
            StoreItem("car_5", "Auto Verde", 5000),
            StoreItem("car_6", "Auto Rojo", 5000)
        )
    )
    val items: StateFlow<List<StoreItem>> = _items

    
    fun buyItem(itemId: String) {
       // acá hay que hacer dps la logica de cuando vayan a comprar
    }
}
