package com.app.mathracer.data.network

import com.app.mathracer.data.model.Friend
import com.app.mathracer.data.model.Levels
import com.app.mathracer.data.model.Player
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
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Header
import retrofit2.http.PUT
import retrofit2.http.Path
import com.google.gson.JsonObject


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

data class ShopResponseEnergies(
    val pricePerUnit: Int,
    val maxAmount: Int,
    val currentAmount: Int,
    val maxCanBuy: Int
)

data class ShopResponseWildcards(
    val id: Int,
    val name: String,
    val description: String,
    val price: Int,
    val currentQuantity: Int
)

data class PurchaseSuccessResponseDto(
    val remainingCoins: Int
)

// Request DTO for purchasing energy
data class PurchaseEnergyRequestDto(
    val quantity: Int
)

// Response DTO for purchasing energy
data class PurchaseEnergyResultDto(
    val success: Boolean,
    val message: String?,
    val newEnergyAmount: Int?,
    val remainingCoins: Int?,
    val totalPrice: Int?
)

// Request DTO for purchasing wildcards
data class PurchaseWildscardRequestDto(
    val wildcardId: Int,
    val quantity: Int
)

// Response DTO for purchasing wildcards
data class PurchaseWildscardResultDto(
    val success: Boolean,
    val message: String?,
    val newQuantity: Int?,
    val remainingCoins: Int?
)

data class CoinPackageDto(
    val id: Int = 0,
    val coinAmount: Int = 0,
    val price: Int = 0,
    val description: String? = null
)

data class PaymentPreferenceRequestDto(
    val playerId: Int,
    val coinPackageId: Int,
    val successUrl: String,
    val failureUrl: String,
    val pendingUrl: String
)

data class InfiniteQuestionDto(
        val questionId: Int = 0,
        val equation: String = "",
        val options: List<Int> = emptyList(),
        val correctAnswer: Int = 0,
        val expectedResult: String? = null
    )

    data class InfiniteStartResponse(
        val gameId: Int = 0,
        val playerName: String = "",
        val questions: List<InfiniteQuestionDto> = emptyList(),
        val totalCorrectAnswers: Int = 0,
        val currentBatch: Int = 0
    )

    data class InfiniteAnswerRequest(
        val selectedAnswer: Int
    )

    data class InfiniteAnswerResponse(
        val isCorrect: Boolean,
        val correctAnswer: Int,
        val totalCorrectAnswers: Int,
        val currentQuestionIndex: Int,
        val needsNewBatch: Boolean
    )

    data class InfiniteLoadBatchResponse(
        val gameId: Int,
        val questions: List<InfiniteQuestionDto>,
        val currentBatch: Int,
        val totalCorrectAnswers: Int
    )

    data class InfiniteStatusResponse(
        val gameId: Int,
        val playerName: String,
        val totalCorrectAnswers: Int,
        val currentQuestionIndex: Int,
        val currentBatch: Int,
        val isActive: Boolean,
        val gameStartedAt: String?,
        val abandonedAt: String?
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
    suspend fun getFriends(@Header("Authorization") authorization: String?, @Path("playerId") playerId: Int): Response<List<Friend>>

    @GET("Player/email/{email}")
    suspend fun getPlayer(@Header("Authorization") authorization: String?, @Path("email") email: String): Response<Player>

    @GET("Friendship/{playerId}/pending")
    suspend fun getPending(@Header("Authorization") authorization: String?, @Path("playerId") playerId: Int): Response<List<Friend>>

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

    @POST("Chest/open")
    suspend fun openChest(@Header("Authorization") authorization: String?): Response<com.app.mathracer.data.model.ChestResponse>

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

    @GET("Energy/store/{playerId}")
    suspend fun getShopEnergies(@Header("Authorization") authorization: String?, @Path("playerId") playerId: Int): Response<ShopResponseEnergies>

    @GET("Wildcards/store/{playerId}")
    suspend fun getShopWildcards(@Header("Authorization") authorization: String?, @Path("playerId") playerId: Int): Response<List<ShopResponseWildcards>>

    @POST("players/{playerId}/backgrounds/{backgroundId}")
    suspend fun purchaseBackground(@Path("playerId") playerId: Int, @Path("backgroundId") backgroundId: Int): Response<PurchaseSuccessResponseDto>

    @POST("players/{playerId}/characters/{charactersId}")
    suspend fun purchaseCharacters(@Path("playerId") playerId: Int, @Path("charactersId") charactersId: Int): Response<PurchaseSuccessResponseDto>

    @POST("players/{playerId}/cars/{carsId}")
    suspend fun purchaseCars(@Path("playerId") playerId: Int, @Path("carsId") carsId: Int): Response<PurchaseSuccessResponseDto>

    @POST("Energy/purchase/{playerId}")
    suspend fun purchaseEnergy(
        @Header("Authorization") authorization: String?,
        @Path("playerId") playerId: Int,
        @Body body: PurchaseEnergyRequestDto
    ): Response<PurchaseEnergyResultDto>

    @POST("Wildcards/purchase/{playerId}")
    suspend fun purchaseWildcards(
        @Path("playerId") playerId: Int,
        @Body body: PurchaseWildscardRequestDto
    ): Response<PurchaseWildscardResultDto>

    @GET("Coins/packages")
    suspend fun getCoinPackages(): Response<List<CoinPackageDto>>

    @POST("Payments/create-preference")
    suspend fun createPaymentPreference(@Body body: PaymentPreferenceRequestDto): Response<JsonObject>

    @POST("/api/Infinite/start")
    suspend fun startInfinite(@Header("Authorization") authorization: String?): Response<InfiniteStartResponse>

    @POST("/api/Infinite/{gameId}/answer")
    suspend fun submitInfiniteAnswer(
        @Header("Authorization") authorization: String?,
        @Path("gameId") gameId: Int,
        @Body request: InfiniteAnswerRequest
    ): Response<InfiniteAnswerResponse>

    @POST("/api/Infinite/{gameId}/load-batch")
    suspend fun loadInfiniteBatch(
        @Header("Authorization") authorization: String?,
        @Path("gameId") gameId: Int
    ): Response<InfiniteLoadBatchResponse>

    @GET("/api/Infinite/{gameId}/status")
    suspend fun getInfiniteStatus(
        @Header("Authorization") authorization: String?,
        @Path("gameId") gameId: Int
    ): Response<InfiniteStatusResponse>

    @POST("/api/Infinite/{gameId}/abandon")
    suspend fun abandonInfinite(
        @Header("Authorization") authorization: String?,
        @Path("gameId") gameId: Int
    ): Response<InfiniteStatusResponse>

    // Game Invitation endpoints
    data class GameInvitationSendRequest(
        val invitedFriendId: Int,
        val difficulty: String,
        val expectedResult: String
    )

    data class GameInvitationDto(
        val id: Int,
        val gameId: Int? = null,
        val inviterId: Int? = null,
        val inviterName: String? = null,
        // backend may return inviterPlayerName
        val inviterPlayerName: String? = null,
        val invitedFriendId: Int? = null,
        val gameName: String? = null,
        val difficulty: String? = null,
        val expectedResult: String? = null,
        val status: String? = null,
        val createdAt: String? = null
    )

    data class GameInvitationRespondRequest(
        val invitationId: Int,
        val accept: Boolean
    )

    data class GameInvitationRespondResponse(
        val accepted: Boolean,
        val gameId: Int?,
        val message: String?
    )

    @POST("/api/GameInvitation/send")
    suspend fun sendGameInvitation(
        @Header("Authorization") authorization: String?,
        @Body request: GameInvitationSendRequest
    ): Response<Unit>

    data class GameInvitationInboxResponse(
        val totalInvitations: Int = 0,
        val invitations: List<GameInvitationDto> = emptyList()
    )

    @GET("/api/GameInvitation/inbox")
    suspend fun getGameInvitationInbox(
        @Header("Authorization") authorization: String?
    ): Response<GameInvitationInboxResponse>

    @POST("/api/GameInvitation/respond")
    suspend fun respondGameInvitation(
        @Header("Authorization") authorization: String?,
        @Body request: GameInvitationRespondRequest
    ): Response<GameInvitationRespondResponse>

}
