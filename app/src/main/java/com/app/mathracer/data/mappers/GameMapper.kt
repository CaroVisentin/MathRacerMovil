package com.app.mathracer.data.mappers

import com.app.mathracer.data.entities.GameUpdateEntity
import com.app.mathracer.domain.models.*

object GameMapper {
    
    fun entityToDomain(entity: GameUpdateEntity): Game {
        val players = entity.players
        val playerOne = if (players.isNotEmpty()) {
            Player(
                id = players[0].id.toInt().toString(),
                name = players[0].name,
                score = players[0].correctAnswers.toInt()
            )
        } else {
            Player(id = "unknown", name = "Unknown", score = 0)
        }
        
        val playerTwo = if (players.size > 1) {
            Player(
                id = players[1].id.toInt().toString(),
                name = players[1].name,
                score = players[1].correctAnswers.toInt()
            )
        } else null
        
        return Game(
            id = entity.gameId.toInt().toString(),
            playerOne = playerOne,
            playerTwo = playerTwo,
            currentQuestion = entity.currentQuestion?.let { questionEntity ->
                Question(
                    id = questionEntity.id ?: "unknown",
                    text = questionEntity.equation ?: "",
                    options = questionEntity.options ?: emptyList(),
                    correctAnswer = questionEntity.correctAnswer ?: 0
                )
            },
            status = when (entity.status.lowercase()) {
                "waitingforplayers" -> GameStatus.WAITING_FOR_PLAYERS
                "inprogress" -> GameStatus.IN_PROGRESS
                "finished" -> GameStatus.FINISHED
                else -> GameStatus.WAITING_FOR_PLAYERS
            },
            winner = entity.winnerId?.let { winnerId ->
                players.find { it.id == winnerId }?.let { winnerEntity ->
                    Player(
                        id = winnerEntity.id.toInt().toString(),
                        name = winnerEntity.name,
                        score = winnerEntity.correctAnswers.toInt()
                    )
                }
            }
        )
    }
}