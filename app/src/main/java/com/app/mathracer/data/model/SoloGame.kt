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
    val playerProducts: List<ProductPlayerGame>,

    @SerializedName("machineProducts")
    val machineProducts: List<ProductPlayerGame>
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

data class ProductPlayerGame(
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

    @SerializedName("status")
    val status: String,

    @SerializedName("playerPosition")
    val playerPosition: Int,

    @SerializedName("machinePosition")
    val machinePosition: Int,

    @SerializedName("livesRemaining")
    val livesRemaining: Int,

    @SerializedName("correctAnswers")
    val correctAnswers: Int,

    @SerializedName("currentQuestion")
    val currentQuestion: SoloQuestion?,

    @SerializedName("currentQuestionIndex")
    val currentQuestionIndex: Int,

    @SerializedName("totalQuestions")
    val totalQuestions: Int,

    @SerializedName("timePerEquation")
    val timePerEquation: Int,

    @SerializedName("gameStartedAt")
    val gameStartedAt: String,

    @SerializedName("gameFinishedAt")
    val gameFinishedAt: String,

    @SerializedName("elapsedTime")
    val elapsedTime: Float,
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