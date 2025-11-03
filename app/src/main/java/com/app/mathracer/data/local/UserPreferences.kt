package com.app.mathracer.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "user_prefs"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

data class LocalUser(
    val uid: String,
    val username: String?,
    val email: String?
)

object UserPreferences {
    private val KEY_UID = stringPreferencesKey("uid")
    private val KEY_USERNAME = stringPreferencesKey("username")
    private val KEY_EMAIL = stringPreferencesKey("email")
    private val KEY_LOGGED_IN = booleanPreferencesKey("logged_in")

    suspend fun saveUser(context: Context, uid: String, username: String?, email: String?) {
        context.dataStore.edit { prefs ->
            prefs[KEY_UID] = uid
            prefs[KEY_USERNAME] = username ?: ""
            prefs[KEY_EMAIL] = email ?: ""
            prefs[KEY_LOGGED_IN] = true
        }
    }

    suspend fun clearUser(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_UID)
            prefs.remove(KEY_USERNAME)
            prefs.remove(KEY_EMAIL)
            prefs.remove(KEY_LOGGED_IN)
        }
    }

    fun getUserFlow(context: Context): Flow<LocalUser?> {
        return context.dataStore.data.map { prefs ->
            val logged = prefs[KEY_LOGGED_IN] ?: false
            if (!logged) return@map null
            val uid = prefs[KEY_UID] ?: return@map null
            val username = prefs[KEY_USERNAME]
            val email = prefs[KEY_EMAIL]
            LocalUser(uid = uid, username = username, email = email)
        }
    }
}
