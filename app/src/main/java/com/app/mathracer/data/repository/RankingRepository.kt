package com.app.mathracer.data.repository

import com.app.mathracer.data.network.RankingResponseDto
import com.app.mathracer.data.network.RetrofitClient
import android.util.Log

class RankingRepository {
    private val api = RetrofitClient.api

    suspend fun getRanking(playerId: Int? = null): Result<RankingResponseDto> {
        return try {
            Log.d("RankingRepository", "Requesting ranking for playerId=$playerId")
            val resp = api.getRanking(playerId)
            Log.d("RankingRepository", "Ranking response code=${resp.code()}")
            if (resp.isSuccessful) {
                Result.success(resp.body() ?: RankingResponseDto())
            } else {
                val body = resp.errorBody()?.string()
                Log.w("RankingRepository", "Ranking API error: ${resp.code()} body=$body")
                Result.failure(Exception("Ranking API error: ${resp.code()} - $body"))
            }
        } catch (e: Exception) {
            Log.e("RankingRepository", "Exception while fetching ranking", e)
            Result.failure(e)
        }
    }
}
