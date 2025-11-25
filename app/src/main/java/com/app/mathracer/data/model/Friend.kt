package com.app.mathracer.data.model

import com.google.gson.annotations.SerializedName

data class Friend(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String?,
    @SerializedName("uid") val uid: String?,
    @SerializedName("points") val points: Int = 0,
    @SerializedName("character") val character: CharacterRef? = null
)

data class CharacterRef(
    @SerializedName("id") val id: Int
)

data class Player(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String?,
    @SerializedName("coins") val coins: Int = 0,
    @SerializedName("points") val points: Int = 0,
    @SerializedName("character") val character: CharacterRef? = null,
    @SerializedName("car") val car: CharacterRef? = null,
    @SerializedName("background") val background: CharacterRef? = null
)