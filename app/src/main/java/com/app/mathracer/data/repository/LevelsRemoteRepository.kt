package com.app.mathracer.data.repository

import com.app.mathracer.data.model.Levels
import com.app.mathracer.data.network.RetrofitClient.api
import retrofit2.Response

object LevelsRemoteRepository {

    suspend fun getLevels(worldId: Int): Response<Levels> {
        val token = try {
            UserRemoteRepository.getIdToken()
        } catch (e: Exception) {
            null
        }
        val header = token?.let { "Bearer $it" }
        return api.getLevels(header, worldId)
    }
}