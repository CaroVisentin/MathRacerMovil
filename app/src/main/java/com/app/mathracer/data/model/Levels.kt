package com.app.mathracer.data.model

import com.google.gson.annotations.SerializedName

data class Levels (
    @SerializedName("worldName") val worldName: String,
    @SerializedName("levels") val levels: List<LevelDto>,
    @SerializedName("lastCompletedLevelId") val lastCompletedLevelId: Int = 0
)

class LevelDto(
    @SerializedName("id") val id: Int,
    @SerializedName("worldId") val worldId: Int,
    @SerializedName("number") val number: Int,
    @SerializedName("termsCount") val termsCount: Int,
    @SerializedName("variablesCount") val variablesCount: Int,
    @SerializedName("resultType") val resultType: String
)