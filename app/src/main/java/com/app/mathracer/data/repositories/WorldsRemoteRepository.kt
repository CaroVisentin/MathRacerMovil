package com.app.mathracer.data.repositories

import com.app.mathracer.data.model.Worlds
import com.app.mathracer.data.network.RetrofitClient.api
import retrofit2.Response

object WorldsRemoteRepository {

    suspend fun getWorlds(): Response<Worlds> {
        val token = try {
            UserRemoteRepository.getIdToken()
        } catch (e: Exception) {
            null
        }
        val header = token?.let { "Bearer $it" }
        return api.getWorlds(header)
    }
}