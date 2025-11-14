package com.app.mathracer.data.repository

import android.util.Log
import com.app.mathracer.data.network.GarageResponseDto
import com.app.mathracer.data.network.RetrofitClient
import javax.inject.Inject

class GarageRepository @Inject constructor() {
    suspend fun getCars(playerId: Int): Result<GarageResponseDto> {
        return try {
            Log.d("GarageRepository", "Requesting cars for playerId=$playerId")
            val resp = RetrofitClient.api.getCars(playerId)
            if (resp.isSuccessful) Result.success(resp.body() ?: GarageResponseDto())
            else Result.failure(Exception("getCars failed ${resp.code()}"))
        } catch (e: Exception) {
            Log.e("GarageRepository", "getCars exception", e)
            Result.failure(e)
        }
    }

    suspend fun getCharacters(playerId: Int): Result<GarageResponseDto> {
        return try {
            val resp = RetrofitClient.api.getCharacters(playerId)
            if (resp.isSuccessful) Result.success(resp.body() ?: GarageResponseDto())
            else Result.failure(Exception("getCharacters failed ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBackgrounds(playerId: Int): Result<GarageResponseDto> {
        return try {
            val resp = RetrofitClient.api.getBackgrounds(playerId)
            if (resp.isSuccessful) Result.success(resp.body() ?: GarageResponseDto())
            else Result.failure(Exception("getBackgrounds failed ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun activateItem(playerId: Int, productId: Int, productType: String): Result<com.app.mathracer.data.model.GenericResponse> {
        return try {
            val resp = RetrofitClient.api.activateItem(playerId, productId, productType)
            if (resp.isSuccessful) Result.success(resp.body() ?: com.app.mathracer.data.model.GenericResponse(success = true))
            else Result.failure(Exception("activateItem failed ${resp.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
