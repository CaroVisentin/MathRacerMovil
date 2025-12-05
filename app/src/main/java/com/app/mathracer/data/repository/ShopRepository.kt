package com.app.mathracer.data.repository

import android.util.Log
import com.app.mathracer.data.network.CoinPackageDto
import com.app.mathracer.data.network.PaymentPreferenceRequestDto
import com.app.mathracer.data.network.PurchaseEnergyRequestDto
import com.app.mathracer.data.network.PurchaseEnergyResultDto
import com.app.mathracer.data.network.PurchaseSuccessResponseDto
import com.app.mathracer.data.network.PurchaseWildscardRequestDto
import com.app.mathracer.data.network.PurchaseWildscardResultDto
import com.app.mathracer.data.network.RetrofitClient
import com.app.mathracer.data.network.ShopResponse
import com.app.mathracer.data.network.ShopResponseEnergies
import com.app.mathracer.data.network.ShopResponseWildcards
import com.google.gson.JsonObject
import javax.inject.Inject

class ShopRepository @Inject constructor() {
    suspend fun getCars(playerId: Int): Result<ShopResponse> {
        return try {
            Log.d("GarageRepository", "Requesting cars for playerId=$playerId")
            val resp = RetrofitClient.api.getShopCars(playerId)
            if (resp.isSuccessful) Result.success(resp.body() ?: ShopResponse())
            else Result.failure(Exception("getShopCars failed ${resp.code()}"))
        } catch (e: Exception) {
            Log.e("GarageRepository", "getCars exception", e)
            Result.failure(e)
        }
    }

    suspend fun getCharacters(playerId: Int): Result<ShopResponse> {
        return try {
            val resp = RetrofitClient.api.getShopCharacters(playerId)
            Log.d("SHOP", resp.body().toString())
            if (resp.isSuccessful) Result.success(resp.body() ?: ShopResponse())
            else Result.failure(Exception("getCharacters failed ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBackgrounds(playerId: Int): Result<ShopResponse> {
        return try {
            val resp = RetrofitClient.api.getShopBackgrounds(playerId)
            Log.d("SHOP", resp.body().toString())
            if (resp.isSuccessful) Result.success(resp.body() ?: ShopResponse())
            else Result.failure(Exception("getBackgrounds failed ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEneries(playerId: Int): Result<ShopResponseEnergies> {
        return try {
            val token = try {
                UserRemoteRepository.getIdToken()
            } catch (e: Exception) { null }
            val header = token?.let { "Bearer $it" }
            val resp = RetrofitClient.api.getShopEnergies(header, playerId)
            Log.d("Energies", resp.body().toString())
            if (resp.isSuccessful) Result.success(resp.body() ?: ShopResponse())
            else Result.failure(Exception("getEnergies failed ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        } as Result<ShopResponseEnergies>
    }

    suspend fun getComodines(playerId: Int): Result<List<ShopResponseWildcards>> {
        return try {
            val token = try {
                UserRemoteRepository.getIdToken()
            } catch (e: Exception) { null }
            val header = token?.let { "Bearer $it" }
            val resp = RetrofitClient.api.getShopWildcards(header, playerId)
            Log.d("Wildcards", resp.body().toString())
            if (resp.isSuccessful) Result.success(resp.body() ?: ShopResponse())
            else Result.failure(Exception("getShopWildcards failed ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        } as Result<List<ShopResponseWildcards>>
    }

    suspend fun getCoinPackages(): Result<List<CoinPackageDto>> {
        return try {
            val resp = RetrofitClient.api.getCoinPackages()
            Log.d("CoinPackages", resp.body().toString())
            if (resp.isSuccessful) Result.success(resp.body() ?: emptyList())
            else Result.failure(Exception("getCoinPackages failed ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPaymentPreference(
        playerId: Int,
        coinPackageId: Int,
        successUrl: String,
        failureUrl: String,
        pendingUrl: String
    ): Result<JsonObject> {
        return try {
            val body = PaymentPreferenceRequestDto(
                playerId = playerId,
                coinPackageId = coinPackageId,
                successUrl = successUrl,
                failureUrl = failureUrl,
                pendingUrl = pendingUrl
            )
            val resp = RetrofitClient.api.createPaymentPreference(body)
            Log.d("Payments", resp.toString())
            if (resp.isSuccessful && resp.body() != null) Result.success(resp.body()!!)
            else Result.failure(Exception("createPaymentPreference failed ${resp.code()} - ${resp.message()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun purchaseBackground(
        playerId: Int,
        backgroundId: Int
    ): Result<PurchaseSuccessResponseDto> {
        return try {
            Log.d("ShopRepository", "purchaseBackground: playerId=$playerId, backgroundId=$backgroundId")
            val response = RetrofitClient.api.purchaseBackground(playerId, backgroundId)
            Log.d("SHOP", response.toString())
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Error compra fondo: ${response.code()} - ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun purchaseCar(
        playerId: Int,
        carId: Int
    ): Result<PurchaseSuccessResponseDto> {
        return try {
            Log.d("ShopRepository", "purchaseCar: playerId=$playerId, carId=$carId")
            val response = RetrofitClient.api.purchaseCars(playerId, carId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Error compra fondo: ${response.code()} - ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun purchaseCharacter(
        playerId: Int,
        characterId: Int
    ): Result<PurchaseSuccessResponseDto> {
        return try {
            Log.d("ShopRepository", "purchaseCharacter: playerId=$playerId, characterId=$characterId")
            val response = RetrofitClient.api.purchaseCharacters(playerId, characterId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Error compra fondo: ${response.code()} - ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun purchaseEnergy(playerId: Int, quantity: Int): Result<PurchaseEnergyResultDto> {
        return try {
            Log.d("ShopRepository", "purchaseEnergy: playerId=$playerId, quantity=$quantity")
            val token = try {
                UserRemoteRepository.getIdToken()
            } catch (e: Exception) { null }
            val header = token?.let { "Bearer $it" }
            val body = PurchaseEnergyRequestDto(quantity = quantity)
            val response = RetrofitClient.api.purchaseEnergy(
                header,
                playerId = playerId,
                body = body
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Error compra energía: ${response.code()} - ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun purchaseWildcard(
        playerId: Int,
        wildcardId: Int,
        quantity: Int
    ): Result<PurchaseWildscardResultDto> {
        return try {
            Log.d("ShopRepository", "purchaseWildcard: playerId=$playerId, wildcardId=$wildcardId, quantity=$quantity")
            val body = PurchaseWildscardRequestDto(
                wildcardId = wildcardId,
                quantity = quantity
            )
            val response = RetrofitClient.api.purchaseWildcards(
                playerId = playerId,
                body = body
            )
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    Exception("Error compra comodín: ${response.code()} - ${response.message()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
