package com.app.mathracer.data.network

import com.app.mathracer.data.model.Levels
import com.app.mathracer.data.model.User
import com.app.mathracer.data.model.UserLogin
import com.app.mathracer.data.model.Worlds
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("player/register")
    suspend fun createUser(
        @Header("Authorization") authorization: String?,
        @Body user: User
    ): Response<User>

    @POST("player/login")
    suspend fun loginUser(
        @Header("Authorization") authorization: String?,
        @Body user: UserLogin
    ): Response<User>

    @GET("/api/Worlds")
    suspend fun getWorlds(@Header("Authorization") authorization: String?): Response<Worlds>

    @GET("/api/Levels/world/{worldId}")
    suspend fun getLevels(@Header("Authorization") authorization: String?, @Path("worldId") worldId: Int): Response<Levels>
}
