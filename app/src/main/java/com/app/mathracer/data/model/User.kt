package com.app.mathracer.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("uid") val id: String,
    @SerializedName("email") val email: String? = null,
    @SerializedName("username") val name: String? = null
)
