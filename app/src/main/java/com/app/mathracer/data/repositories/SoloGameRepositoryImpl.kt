package com.app.mathracer.data.repositories

import com.app.mathracer.data.model.SoloAnswerResponse
import com.app.mathracer.data.model.SoloGameStartResponse
import com.app.mathracer.data.model.SoloGameUpdateResponse
import com.app.mathracer.data.network.RetrofitClient
import com.app.mathracer.data.repository.UserRemoteRepository
import com.app.mathracer.domain.repositories.SoloGameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import retrofit2.HttpException
import java.io.IOException

class SoloGameRepositoryImpl : SoloGameRepository {
    
    private val api = RetrofitClient.api
    
    override suspend fun startSoloGame(levelId: Int): Result<SoloGameStartResponse> {
        return try {
            val token = UserRemoteRepository.getIdToken()
            val header = token?.let { "Bearer $it" }
            
            val response = api.startSoloGame(header, levelId)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    IOException("Error al iniciar partida: ${response.code()} - ${response.message()}")
                )
            }
        } catch (e: HttpException) {
            Result.failure(IOException("Error HTTP: ${e.code()} - ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getSoloGameUpdate(gameId: Int): Result<SoloGameUpdateResponse> {
        return try {
            val token = UserRemoteRepository.getIdToken()
            val header = token?.let { "Bearer $it" }
            
            val response = api.getSoloGameUpdate(header, gameId)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    IOException("Error al obtener actualización: ${response.code()} - ${response.message()}")
                )
            }
        } catch (e: HttpException) {
            Result.failure(IOException("Error HTTP: ${e.code()} - ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun submitSoloAnswer(gameId: Int, answer: Int): Result<SoloAnswerResponse> {
        return try {
            val token = UserRemoteRepository.getIdToken()
            val header = token?.let { "Bearer $it" }
            
            val response = api.submitSoloAnswer(header, gameId, answer)
            
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(
                    IOException("Error al enviar respuesta: ${response.code()} - ${response.message()}")
                )
            }
        } catch (e: HttpException) {
            Result.failure(IOException("Error HTTP: ${e.code()} - ${e.message()}"))
        } catch (e: IOException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeSoloGameUpdates(gameId: Int, intervalMs: Long): Flow<SoloGameUpdateResponse?> {
        return flow {
            // one-shot: si intervalMs <= 0, emitimos UNA vez y terminamos
            if (intervalMs <= 0L) {
                val once = getSoloGameUpdate(gameId)
                once.fold(
                    onSuccess = { emit(it) },
                    onFailure = {
                        android.util.Log.e("SoloGameRepository", "Error one-shot game update", it)
                        emit(null)
                    }
                )
                return@flow
            }

            // polling continuo
            var backoffMs = intervalMs // podés ajustar backoff si falla
            while (kotlin.coroutines.coroutineContext.isActive) {
                val result = getSoloGameUpdate(gameId)
                result.fold(
                    onSuccess = { update ->
                        emit(update)
                        backoffMs = intervalMs // reset backoff al éxito
                    },
                    onFailure = { exception ->
                        android.util.Log.e("SoloGameRepository", "Error polling game update", exception)
                        emit(null)
                        // backoff simple opcional: no lo hagas exponencial si no querés
                        backoffMs = (backoffMs.coerceAtMost(60_000L))
                    }
                )
                kotlinx.coroutines.delay(backoffMs)
            }
        }
    }
}

