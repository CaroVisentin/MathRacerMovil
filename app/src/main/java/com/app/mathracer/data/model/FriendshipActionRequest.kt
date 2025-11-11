package com.app.mathracer.data.model

import com.google.gson.annotations.SerializedName

data class FriendshipActionRequest(
    @SerializedName("fromPlayerId") val fromPlayerId: Int,
    @SerializedName("toPlayerId") val toPlayerId: Int
)
