package com.app.mathracer.data.model

import com.google.gson.annotations.SerializedName

data class SoloGameStartResponse(
    @SerializedName("gameId")
    val gameId: Int,

    @SerializedName("playerId")
    val playerId: Int,

    @SerializedName("playerName")
    val playerName: String,

    @SerializedName("levelId")
    val levelId: Int,

    @SerializedName("totalQuestions")
    val totalQuestions: Int,

    @SerializedName("timePerEquation")
    val timePerEquation: Int,

    @SerializedName("livesRemaining")
    val livesRemaining: Int,

    @SerializedName("gameStartedAt")
    val gameStartedAt: String,

    @SerializedName("currentQuestion")
    val currentQuestion: SoloQuestion?,

    @SerializedName("playerProducts")
    val playerProducts: List<Product>,

    @SerializedName("machineProducts")
    val machineProducts: List<Product>
)

data class SoloQuestion(
    @SerializedName("id")
    val id: Int,

    @SerializedName("equation")
    val equation: String,

    @SerializedName("options")
    val options: List<Int>,

    @SerializedName("startedAt")
    val startedAt: String
)

data class Product(
    @SerializedName("productId")
    val productId: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("productTypeId")
    val productTypeId: Int,

    @SerializedName("productTypeName")
    val productTypeName: String,

    @SerializedName("rarityId")
    val rarityId: Int,

    @SerializedName("rarityName")
    val rarityName: String,

    @SerializedName("rarityColor")
    val rarityColor: String
)

data class SoloGameUpdateResponse(
    @SerializedName("gameId")
    val gameId: Int,

    @SerializedName("playerId")
    val playerId: Int,

    @SerializedName("playerScore")
    val playerScore: Int,

    @SerializedName("machineScore")
    val machineScore: Int,

    @SerializedName("livesRemaining")
    val livesRemaining: Int,

    @SerializedName("currentQuestion")
    val currentQuestion: SoloQuestion?,

    @SerializedName("gameStatus")
    val gameStatus: String, // "InProgress", "Finished"

    @SerializedName("winner")
    val winner: String?, // "Player" o "Machine" o null

    @SerializedName("expectedResult")
    val expectedResult: String?
)

data class SoloAnswerRequest(
    @SerializedName("answer")
    val answer: Int
)

data class SoloAnswerResponse(
    @SerializedName("isCorrect")
    val isCorrect: Boolean,

    @SerializedName("correctAnswer")
    val correctAnswer: Int,

    @SerializedName("playerScore")
    val playerScore: Int,

    @SerializedName("machineScore")
    val machineScore: Int
)