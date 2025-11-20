package com.app.mathracer.data.network

import com.app.mathracer.data.model.Levels
import com.app.mathracer.data.model.SoloAnswerResponse
import com.app.mathracer.data.model.SoloGameStartResponse
import com.app.mathracer.data.model.SoloGameUpdateResponse
import com.app.mathracer.data.model.User
import com.app.mathracer.data.model.UserGoogle
import com.app.mathracer.data.model.UserLogin
import com.app.mathracer.data.model.WildCard
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

data class EnergyDto(
    val secondsUntilNextRecharge: Int,
    val currentAmount: Int,
    val maxAmount: Int
)

data class ShopResponse(
    val items: List<ItemDto> = emptyList(),
    val totalCount: Int? = null
)

data class ItemDto(
    val id: Int,
    val name: String,
    val description: String,
    val price: Int,
    val imageUrl: String,
    val productTypeId: Int,
    val productTypeName: String,
    val rarity: String,
    val isOwned: Boolean,
    val currency: String
)

data class PurchaseSuccessResponseDto(
    val remainingCoins: Int
)

interface ApiService {
    @POST("player/register")
    suspend fun createUser(
        @Header("Authorization") authorization: String?,
        @Body user: User
    ): Response<User>

    @POST("player/google")
    suspend fun google(
        @Header("Authorization") authorization: String?,
        @Body user: UserGoogle
    ): Response<User>

    @POST("player/login")
    suspend fun loginUser(@Header("Authorization") authorization: String?, @Body user: UserLogin): Response<User>

    @GET("/api/Worlds")
    suspend fun getWorlds(@Header("Authorization") authorization: String?): Response<Worlds>

    @GET("/api/Levels/world/{worldId}")
    suspend fun getLevels(@Header("Authorization") authorization: String?, @Path("worldId") worldId: Int): Response<Levels>

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

    @POST("/api/solo/{gameId}/wildcard/{wildcardId}")
    suspend fun useWildcard(
        @Header("Authorization") authorization: String?,
        @Path("gameId") gameId: Int,
        @Path("wildcardId") wildcardId: Int
    ): Response<WildCard>

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

    @GET("energy")
    suspend fun getEnergy(@Header("Authorization") authorization: String?): Response<EnergyDto>

    // --- Online multiplayer endpoints ---

    data class AvailableGameDto(
        val gameId: Int = 0,
        val gameName: String = "",
        val isPrivate: Boolean = false,
        val requiresPassword: Boolean = false,
        val currentPlayers: Int = 0,
        val maxPlayers: Int = 0,
        val difficulty: String? = null,
        val expectedResult: String? = null,
        val createdAt: String? = null,
        val creatorName: String? = null,
        val isFull: Boolean = false,
        val status: String? = null
    )

    data class AvailableGamesResponse(
        val games: List<AvailableGameDto> = emptyList(),
        val totalGames: Int = 0,
        val publicGames: Int = 0,
        val privateGames: Int = 0,
        val timestamp: String? = null
    )

    data class ConnectionInfoDto(
        val hubUrl: String? = null,
        val events: List<String>? = null
    )

    data class CreateGameRequest(
        val gameName: String,
        val isPrivate: Boolean,
        val password: String? = null,
        val difficulty: String,
        val expectedResult: String
    )

    @GET("/api/Online/games/available")
    suspend fun getAvailableGames(@Query("publicOnly") publicOnly: Boolean = false): Response<AvailableGamesResponse>

    @GET("/api/Online/game/{gameId}")
    suspend fun getGameById(@Path("gameId") gameId: Int): Response<AvailableGameDto>

    @POST("/api/Online/create")
    suspend fun createOnlineGame(@Header("Authorization") authorization: String?, @Body body: CreateGameRequest): Response<Unit>

    @GET("/api/Online/connection-info")
    suspend fun getConnectionInfo(): Response<ConnectionInfoDto>

    @GET("cars")
    suspend fun getShopCars(@Query("playerId") playerId: Int): Response<ShopResponse>

    @GET("backgrounds")
    suspend fun getShopBackgrounds(@Query("playerId") playerId: Int): Response<ShopResponse>

    @GET("characters")
    suspend fun getShopCharacters(@Query("playerId") playerId: Int): Response<ShopResponse>

    @POST("api/players/{playerId}/backgrounds/{backgroundId}")
    suspend fun purchaseBackground(@Path("playerId") playerId: Int, @Path("backgroundId") backgroundId: Int): Response<PurchaseSuccessResponseDto>

    @POST("api/players/{playerId}/characters/{charactersId}")
    suspend fun purchaseCharacters(@Path("playerId") playerId: Int, @Path("charactersId") charactersId: Int): Response<PurchaseSuccessResponseDto>

    @POST("api/players/{playerId}/cars/{carsId}")
    suspend fun purchaseCars(@Path("playerId") playerId: Int, @Path("carsId") carsId: Int): Response<PurchaseSuccessResponseDto>

}
