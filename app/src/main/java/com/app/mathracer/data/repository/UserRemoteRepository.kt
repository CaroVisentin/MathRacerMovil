package com.app.mathracer.data.repository

import com.app.mathracer.data.model.User
import com.app.mathracer.data.model.UserGoogle
import com.app.mathracer.data.model.UserLogin
import com.app.mathracer.data.network.EnergyDto
import com.app.mathracer.data.network.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object UserRemoteRepository {
    private val api = RetrofitClient.api
    private val auth = FirebaseAuth.getInstance()

    public suspend fun getIdToken(): String? = suspendCancellableCoroutine { cont ->
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

    suspend fun createUser(user: User): Response<User> {
        val token = try { getIdToken() } catch (e: Exception) { null }
        val header = token?.let { "Bearer $it" }
        return api.createUser(header, user)
    }

    suspend fun google(user: UserGoogle): Response<User> {
        val token = try { getIdToken() } catch (e: Exception) { null }
        val header = token?.let { "Bearer $it" }
        return api.google(header, user)
    }

    suspend fun loginUser(user: UserLogin): Response<User> {
        val token = try {
            getIdToken()
        } catch (e: Exception) {
            null
        }
        val header = token?.let { "Bearer $it" }
        return api.loginUser(header, user)
    }

    suspend fun getUserByUid(uid: String): Response<User> {
        val token = try { getIdToken() } catch (e: Exception) { null }
        val header = token?.let { "Bearer $it" }
        return api.getUserByUid(header, uid)
    }

    suspend fun getEnergy(): Response<EnergyDto> {
        val token = try { getIdToken() } catch (e: Exception) { null }
        val header = token?.let { "Bearer $it" }
        return api.getEnergy(header)
    }
}
