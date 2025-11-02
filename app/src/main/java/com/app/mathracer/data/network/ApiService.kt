package com.app.mathracer.data.network

import com.app.mathracer.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    // Backend expects Authorization: Bearer <idToken>
    @POST("player/register")
    suspend fun createUser(@Header("Authorization") authorization: String?, @Body user: User): Response<User>

    @GET("player/{id}")
    suspend fun getUser(@Header("Authorization") authorization: String?, @Path("id") id: String): Response<User>

    @GET("player/uid/{uid}")
    suspend fun getUserByUid(@Header("Authorization") authorization: String?, @Path("uid") uid: String): Response<User>
}
