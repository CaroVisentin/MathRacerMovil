package com.app.mathracer.data.repository

import android.util.Log
import com.app.mathracer.data.model.GenericResponse
import com.app.mathracer.data.network.GarageResponseDto
import com.app.mathracer.data.network.PurchaseSuccessResponseDto
import com.app.mathracer.data.network.RetrofitClient
import com.app.mathracer.data.network.ShopResponse
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

    suspend fun purchaseBackground(
        playerId: Int,
        backgroundId: Int
    ): Result<PurchaseSuccessResponseDto> {
        return try {
            val response = RetrofitClient.api.purchaseBackground(playerId, backgroundId)
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

}
