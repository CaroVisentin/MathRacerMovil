package com.app.mathracer.data.repository

import com.app.mathracer.data.model.Friend
import android.util.Log
import com.app.mathracer.data.model.FriendshipActionRequest
import com.app.mathracer.data.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object FriendRepository {
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

    suspend fun getFriends(playerId: Int): Response<List<Friend>> {
        val token = try { getIdToken() } catch (e: Exception) { null }
        return api.getFriends(authHeader(token), playerId)
    }

    suspend fun getPending(playerId: Int): Response<List<Friend>> {
        val token = try { getIdToken() } catch (e: Exception) { null }
        return api.getPending(authHeader(token), playerId)
    }

    suspend fun sendRequest(fromPlayerId: Int, toPlayerId: Int): Response<Unit> {
        val token = try { getIdToken() } catch (e: Exception) {
            Log.e("FriendRepo", "Failed to get idToken", e)
            null
        }
        val body = FriendshipActionRequest(fromPlayerId = fromPlayerId, toPlayerId = toPlayerId)
        try {
            val resp = api.sendFriendRequest(authHeader(token), body)
            if (!resp.isSuccessful) {
                val err = resp.errorBody()?.string()
                Log.e("FriendRepo", "sendRequest failed: code=${resp.code()} err=$err")
            } else {
                Log.d("FriendRepo", "sendRequest success: code=${resp.code()}")
            }
            return resp
        } catch (e: Exception) {
            Log.e("FriendRepo", "Exception in sendRequest", e)
            throw e
        }
    }

    suspend fun acceptRequest(fromPlayerId: Int, toPlayerId: Int): Response<Unit> {
        val token = try { getIdToken() } catch (e: Exception) {
            Log.e("FriendRepo", "Failed to get idToken", e)
            null
        }
        val body = FriendshipActionRequest(fromPlayerId = fromPlayerId, toPlayerId = toPlayerId)
        try {
            val resp = api.acceptFriendRequest(authHeader(token), body)
            if (!resp.isSuccessful) {
                val err = resp.errorBody()?.string()
                Log.e("FriendRepo", "acceptRequest failed: code=${resp.code()} err=$err")
            } else {
                Log.d("FriendRepo", "acceptRequest success: code=${resp.code()}")
            }
            return resp
        } catch (e: Exception) {
            Log.e("FriendRepo", "Exception in acceptRequest", e)
            throw e
        }
    }

    suspend fun rejectRequest(fromPlayerId: Int, toPlayerId: Int): Response<Unit> {
        val token = try { getIdToken() } catch (e: Exception) {
            Log.e("FriendRepo", "Failed to get idToken", e)
            null
        }
        val body = FriendshipActionRequest(fromPlayerId = fromPlayerId, toPlayerId = toPlayerId)
        try {
            val resp = api.rejectFriendRequest(authHeader(token), body)
            if (!resp.isSuccessful) {
                val err = resp.errorBody()?.string()
                Log.e("FriendRepo", "rejectRequest failed: code=${resp.code()} err=$err")
            } else {
                Log.d("FriendRepo", "rejectRequest success: code=${resp.code()}")
            }
            return resp
        } catch (e: Exception) {
            Log.e("FriendRepo", "Exception in rejectRequest", e)
            throw e
        }
    }

    suspend fun deleteFriend(fromPlayerId: Int, toPlayerId: Int): Response<Unit> {
        val token = try { getIdToken() } catch (e: Exception) {
            Log.e("FriendRepo", "Failed to get idToken", e)
            null
        }
        val body = FriendshipActionRequest(fromPlayerId = fromPlayerId, toPlayerId = toPlayerId)
        try {
            val resp = api.deleteFriend(authHeader(token), body)
            if (!resp.isSuccessful) {
                val err = resp.errorBody()?.string()
                Log.e("FriendRepo", "deleteFriend failed: code=${resp.code()} err=$err")
            } else {
                Log.d("FriendRepo", "deleteFriend success: code=${resp.code()}")
            }
            return resp
        } catch (e: Exception) {
            Log.e("FriendRepo", "Exception in deleteFriend", e)
            throw e
        }
    }
}
