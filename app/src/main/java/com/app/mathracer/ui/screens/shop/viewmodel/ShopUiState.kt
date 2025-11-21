package com.app.mathracer.ui.screens.shop.viewmodel

import com.app.mathracer.data.network.ItemDto
import com.app.mathracer.data.network.ShopResponseEnergies
import com.app.mathracer.data.network.ShopResponseWildcards

data class ShopItem(
    val id: Int,
    val price: Int,
    val image: Int,
    val category: ShopCategory
)

data class ShopUiState(
    val coins : Int = 0,
    val cars: List<ItemDto> = emptyList(),
    val characters: List<ItemDto> = emptyList(),
    val backgrounds: List<ItemDto> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val energies: ShopResponseEnergies? = null,
    val comodines: List<ShopResponseWildcards> = emptyList()
    ,
    val purchaseMessage: String? = null
)

enum class ShopBuyType {
    CAR,
    BACKGROUND,
    CHARACTER,
    ENERGY,
    COMODIN
}

enum class ShopCategory {
    CAR,
    BACKGROUND,
    CHARACTER
}