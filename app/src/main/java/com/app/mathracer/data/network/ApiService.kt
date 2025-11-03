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

    @GET("player/uid/{uid}")
    suspend fun getUserByUid(@Header("Authorization") authorization: String?, @Path("uid") uid: String): Response<User>

    
    @GET("Friendship/{playerId}/friends")
    suspend fun getFriends(@Header("Authorization") authorization: String?, @Path("playerId") playerId: Int): Response<List<com.app.mathracer.data.model.Friend>>

    @GET("Friendship/{playerId}/pending")
    suspend fun getPending(@Header("Authorization") authorization: String?, @Path("playerId") playerId: Int): Response<List<com.app.mathracer.data.model.Friend>>

    @POST("Friendship/request")
    suspend fun sendFriendRequest(@Header("Authorization") authorization: String?, @Body body: com.app.mathracer.data.model.FriendshipActionRequest): Response<Unit>

    @POST("Friendship/accept")
    suspend fun acceptFriendRequest(@Header("Authorization") authorization: String?, @Body body: com.app.mathracer.data.model.FriendshipActionRequest): Response<Unit>

    @POST("Friendship/reject")
    suspend fun rejectFriendRequest(@Header("Authorization") authorization: String?, @Body body: com.app.mathracer.data.model.FriendshipActionRequest): Response<Unit>

    @POST("Friendship/delete")
    suspend fun deleteFriend(@Header("Authorization") authorization: String?, @Body body: com.app.mathracer.data.model.FriendshipActionRequest): Response<Unit>
}
