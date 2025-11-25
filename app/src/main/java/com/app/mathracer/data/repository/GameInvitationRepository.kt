package com.app.mathracer.data.repository

import android.util.Log
import com.app.mathracer.data.network.RetrofitClient
import com.app.mathracer.data.network.ApiService
import com.app.mathracer.data.network.ApiService.GameInvitationDto
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object GameInvitationRepository {
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

    suspend fun sendInvitation(invitedFriendId: Int, difficulty: String, expectedResult: String): Response<Unit> {
        val token = try { getIdToken() } catch (e: Exception) { Log.e("GameInvRepo", "Failed to get idToken", e); null }
        val body = ApiService.GameInvitationSendRequest(invitedFriendId = invitedFriendId, difficulty = difficulty, expectedResult = expectedResult)
        try {
            val resp = api.sendGameInvitation(authHeader(token), body)
            if (!resp.isSuccessful) {
                val err = resp.errorBody()?.string()
                Log.e("GameInvRepo", "sendInvitation failed: code=${resp.code()} err=$err")
            } else {
                Log.d("GameInvRepo", "sendInvitation success: code=${resp.code()}")
            }
            return resp
        } catch (e: Exception) {
            Log.e("GameInvRepo", "Exception in sendInvitation", e)
            throw e
        }
    }

    suspend fun getInbox(): Response<com.app.mathracer.data.network.ApiService.GameInvitationInboxResponse> {
        val token = try { getIdToken() } catch (e: Exception) { Log.e("GameInvRepo", "Failed to get idToken", e); null }
        return api.getGameInvitationInbox(authHeader(token))
    }

    suspend fun respondInvitation(invitationId: Int, accept: Boolean): Response<Unit> {
        val token = try { getIdToken() } catch (e: Exception) { Log.e("GameInvRepo", "Failed to get idToken", e); null }
        val body = ApiService.GameInvitationRespondRequest(invitationId = invitationId, accept = accept)
        try {
            val resp = api.respondGameInvitation(authHeader(token), body)
            if (!resp.isSuccessful) {
                val err = resp.errorBody()?.string()
                Log.e("GameInvRepo", "respondInvitation failed: code=${resp.code()} err=$err")
            } else {
                Log.d("GameInvRepo", "respondInvitation success: code=${resp.code()}")
            }
            return resp
        } catch (e: Exception) {
            Log.e("GameInvRepo", "Exception in respondInvitation", e)
            throw e
        }
    }
}
