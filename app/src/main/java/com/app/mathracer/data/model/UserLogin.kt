package com.app.mathracer.data.model

import com.google.gson.annotations.SerializedName

class UserLogin (
    @SerializedName("email") val email: String? = null,
    @SerializedName("password") val password: String? = null
)