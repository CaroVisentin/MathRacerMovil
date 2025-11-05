package com.app.mathracer.data.repository

import com.app.mathracer.data.model.Levels
import com.app.mathracer.data.network.RetrofitClient.api
import com.app.mathracer.data.repository.UserRemoteRepository.getIdToken
import retrofit2.Response

object LevelsRemoteRepository {

    suspend fun getLevels(worldId: Int): Response<Levels> {
        val token = try {
            getIdToken()
        } catch (e: Exception) {
            null
        }
        val header = token?.let { "Bearer $it" }
        return api.getLevels(header, worldId)
    }
}