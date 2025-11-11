package com.app.mathracer.domain.models

data class Game(
    val id: String,
    val playerOne: Player,
    val playerTwo: Player?,
    val currentQuestion: Question?,
    val status: GameStatus,
    val winner: Player?,
    val expectedResult: String? = null
)

data class Player(
    val id: String,
    val name: String,
    val score: Int = 0
)

data class Question(
    val id: String,
    val text: String,
    val options: List<Int>,
    val correctAnswer: Int
)

enum class GameStatus {
    WAITING_FOR_PLAYERS,
    IN_PROGRESS,
    FINISHED
}

data class AnswerResult(
    val isCorrect: Boolean,
    val correctAnswer: Int,
    val playerId: String
)