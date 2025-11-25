package com.app.mathracer.data.repository

import android.util.Log
import com.app.mathracer.data.model.ChestResponse
import com.app.mathracer.data.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object ChestRepository {
    private val api = RetrofitClient.api
    private val auth = FirebaseAuth.getInstance()

    private suspend fun getIdToken(): String? = suspendCancellableCoroutine { cont ->
        val user = auth.currentUser
        if (user == null) {
            cont.resume(null)
            return@suspendCancellableCoroutine
        }
        val task = user.getIdToken(false)
        task.addOnCompleteListener { t ->
            if (t.isSuccessful) cont.resume(t.result?.token) else cont.resumeWithException(t.exception ?: Exception("Failed to get idToken"))
        }
    }

    private fun authHeader(token: String?) = token?.let { "Bearer $it" }

    suspend fun completeTutorial(): Response<ChestResponse> {
        val token = try { getIdToken() } catch (e: Exception) { Log.e("ChestRepo","Failed to get idToken", e); null }
        return try {
            val resp = api.completeTutorial(authHeader(token))
            if (!resp.isSuccessful) {
                val err = resp.errorBody()?.string()
                Log.e("ChestRepo","completeTutorial failed: code=${resp.code()} err=$err")
            } else {
                Log.d("ChestRepo","completeTutorial success: code=${resp.code()}")
            }
            resp
        } catch (e: Exception) {
            Log.e("ChestRepo","Exception in completeTutorial", e)
            throw e
        }
    }
}
