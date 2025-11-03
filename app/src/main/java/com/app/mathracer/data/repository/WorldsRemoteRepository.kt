package com.app.mathracer.data.repository

import com.app.mathracer.data.model.Worlds
import com.app.mathracer.data.network.RetrofitClient.api
import com.app.mathracer.data.repository.UserRemoteRepository.getIdToken
import retrofit2.Response

object WorldsRemoteRepository {

    suspend fun getWorlds(): Response<Worlds> {
        val token = try {
            getIdToken()
        } catch (e: Exception) {
            null
        }
        val header = token?.let { "Bearer $it" }
        return api.getWorlds(header)
    }
}