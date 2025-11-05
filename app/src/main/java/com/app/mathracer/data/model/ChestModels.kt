package com.app.mathracer.data.model

data class ChestResponse(
    val items: List<ChestItem>
)
//comentario para pushear a dev
data class ChestItem(
    val type: String,
    val quantity: Int,
    val product: Product? = null,
    val wildcard: Wildcard? = null,
    val compensationCoins: Int? = null
)

data class Product(
    val id: Int,
    val name: String,
    val description: String?,
    val productType: Int,
    val rarityId: Int,
    val rarityName: String?,
    val rarityColor: String?
)

data class Wildcard(
    val id: Int,
    val name: String,
    val description: String?
)
