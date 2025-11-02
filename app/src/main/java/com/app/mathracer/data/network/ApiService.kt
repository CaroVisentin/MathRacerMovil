package com.app.mathracer.data.network

import com.app.mathracer.data.model.User
import com.app.mathracer.data.model.UserLogin
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("player/register")
    suspend fun createUser(@Header("Authorization") authorization: String?, @Body user: User): Response<User>

    @POST("player/login")
    suspend fun loginUser(@Header("Authorization") authorization: String?, @Body user: UserLogin): Response<User>
}
