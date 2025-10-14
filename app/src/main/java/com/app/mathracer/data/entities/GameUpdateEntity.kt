package com.app.mathracer.data.entities

import com.google.gson.annotations.SerializedName

// Entity que coincide con el formato REAL del backend
data class GameUpdateEntity(
    @SerializedName("gameId")
    val gameId: Double,
    
    @SerializedName("players")
    val players: List<PlayerEntity>,
    
    @SerializedName("status")
    val status: String,
    
    @SerializedName("currentQuestion")
    val currentQuestion: QuestionEntity?,
    
    @SerializedName("winnerId")
    val winnerId: Double?,
    
    @SerializedName("createdAt")
    val createdAt: String?,
    
    @SerializedName("questionCount")
    val questionCount: Double?,
    
    @SerializedName("conditionToWin")
    val conditionToWin: Double?,

    @SerializedName("expectedResult")
    val expectedResult: String? = null
)

data class PlayerEntity(
    @SerializedName("id")
    val id: Double,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("correctAnswers")
    val correctAnswers: Double,
    
    @SerializedName("position")
    val position: Double?,

    @SerializedName("isReady")
    val isReady: Boolean?,
    
    @SerializedName("penaltyUntil")
    val penaltyUntil: String?,
    
    @SerializedName("finishedAt")
    val finishedAt: String?
)

data class QuestionEntity(
    @SerializedName("id")
    val id: String?,
    
    @SerializedName("equation")
    val equation: String?,
    
    @SerializedName("options")
    val options: List<Int>?,
    
    @SerializedName("correctAnswer")
    val correctAnswer: Int?
)