package com.example.unioss_mobile.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

object SessionManager {

    private val ROLE_KEY = stringPreferencesKey("user_role")

    const val ROLE_ADMIN = "admin"
    const val ROLE_GUEST = "guest"
    const val ROLE_NONE = "none"

    fun getRole(context: Context): Flow<String> =
        context.dataStore.data.map { prefs ->
            prefs[ROLE_KEY] ?: ROLE_NONE
        }

    suspend fun login(context: Context, username: String, password: String): Boolean {
        val role = when {
            username == "admin" && password == "admin123" -> ROLE_ADMIN
            username == "guest" && password == "guest123" -> ROLE_GUEST
            else -> return false
        }
        context.dataStore.edit { prefs ->
            prefs[ROLE_KEY] = role
        }
        return true
    }

    suspend fun logout(context: Context) {
        context.dataStore.edit { prefs ->
            prefs[ROLE_KEY] = ROLE_NONE
        }
    }
}