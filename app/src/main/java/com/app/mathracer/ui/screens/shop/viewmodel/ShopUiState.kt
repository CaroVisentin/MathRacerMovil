package com.app.mathracer.ui.screens.shop.viewmodel

import com.app.mathracer.data.network.GarageItemDto
import com.app.mathracer.data.network.ItemDto
import com.app.mathracer.ui.screens.shop.ShopCategory

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
    val error: String? = null
)

