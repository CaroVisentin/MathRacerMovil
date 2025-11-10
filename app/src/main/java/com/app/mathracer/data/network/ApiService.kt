package com.app.mathracer.data.network

import com.app.mathracer.data.model.Levels
import com.app.mathracer.data.model.SoloAnswerResponse
import com.app.mathracer.data.model.SoloGameStartResponse
import com.app.mathracer.data.model.SoloGameUpdateResponse
import com.app.mathracer.data.model.User
import com.app.mathracer.data.model.UserLogin
import com.app.mathracer.data.model.Worlds
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path


data class RankingPlayerDto(
    val position: Int,
    val playerId: Int,
    val name: String,
    val points: Int
)

data class RankingResponseDto(
    val top10: List<RankingPlayerDto> = emptyList(),
    val currentPlayerPosition: Int? = null
)

data class GarageItemDto(
    val id: Int = 0,
    val productId: Int = 0,
    val name: String = "",
    val description: String? = null,
    val price: Int = 0,
    val productType: String? = null,
    val rarity: String? = null,
    val isOwned: Boolean = false,
    val isActive: Boolean = false
)

data class GarageResponseDto(
    val items: List<GarageItemDto> = emptyList(),
    val activeItem: GarageItemDto? = null,
    val itemType: String? = null
)

interface ApiService {
    @POST("player/register")
    suspend fun createUser(
        @Header("Authorization") authorization: String?,
        @Body user: User
    ): Response<User>

    @POST("player/login")
    suspend fun loginUser(@Header("Authorization") authorization: String?, @Body user: UserLogin): Response<User>

    @GET("/api/Worlds")
    suspend fun getWorlds(@Header("Authorization") authorization: String?): Response<Worlds>

    @GET("/api/Levels/world/{worldId}")
    suspend fun getLevels(@Header("Authorization") authorization: String?, @Path("worldId") worldId: Int): Response<Levels>
    
    // Solo Game (History Mode) endpoints
    @POST("/api/solo/start/{levelId}")
    suspend fun startSoloGame(
        @Header("Authorization") authorization: String?,
        @Path("levelId") levelId: Int
    ): Response<SoloGameStartResponse>
    
    @GET("/api/solo/{gameId}")
    suspend fun getSoloGameUpdate(
        @Header("Authorization") authorization: String?,
        @Path("gameId") gameId: Int
    ): Response<SoloGameUpdateResponse>
    
    @POST("/api/solo/{gameId}/answer")
    suspend fun submitSoloAnswer(
        @Header("Authorization") authorization: String?,
        @Path("gameId") gameId: Int,
        @Body answer: Int
    ): Response<SoloAnswerResponse>

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

    @POST("Chest/complete-tutorial")
    suspend fun completeTutorial(@Header("Authorization") authorization: String?): Response<com.app.mathracer.data.model.ChestResponse>


    @GET("ranking")
    suspend fun getRanking(
        @Query("playerId") playerId: Int? = null
    ): Response<RankingResponseDto>

   
    @GET("Garage/cars/{playerId}")
    suspend fun getCars(@Path("playerId") playerId: Int): Response<GarageResponseDto>

    @GET("Garage/characters/{playerId}")
    suspend fun getCharacters(@Path("playerId") playerId: Int): Response<GarageResponseDto>

    @GET("Garage/backgrounds/{playerId}")
    suspend fun getBackgrounds(@Path("playerId") playerId: Int): Response<GarageResponseDto>

    @PUT("Garage/players/{playerId}/items/{productId}/activate")
    suspend fun activateItem(
        @Path("playerId") playerId: Int,
        @Path("productId") productId: Int,
        @Query("productType") productType: String? = null
    ): Response<com.app.mathracer.data.model.GenericResponse>
}
