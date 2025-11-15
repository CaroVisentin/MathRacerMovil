package com.app.mathracer.data.model

import com.google.gson.annotations.SerializedName
//comentario para commitear a dev
data class User(
    @SerializedName("uid") val uid: String,
    @SerializedName("email") val email: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("id") val id: Int? = 0,
    @SerializedName("name") val name: String? = null,
    @SerializedName("lastLevelId") val lastLevelId: Int? = 0,
    @SerializedName("points") val points: Int? = 0,
    @SerializedName("coins") val coins: Int = 0,
)

data class UserGoogle(
    @SerializedName("idToken") val idToken: String,
    @SerializedName("email") val email: String? = null,
    @SerializedName("username") val username: String? = null,
)