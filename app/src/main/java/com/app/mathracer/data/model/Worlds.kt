package com.app.mathracer.data.model

import com.google.gson.annotations.SerializedName

data class Worlds(
    @SerializedName("worlds") val worlds: List<WorldDto>,
    @SerializedName("lastAvailableWorldId") val lastAvailableWorldId: Int
)

class WorldDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("timePerEquation") val timePerEquation: Int,
    @SerializedName("operations") val operations: List<String>,
    @SerializedName("optionsCount") val optionsCount: Int
)
