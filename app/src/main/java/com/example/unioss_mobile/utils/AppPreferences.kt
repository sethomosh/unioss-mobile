package com.example.unioss_mobile.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

object AppPreferences {
    val BACKEND_URL = stringPreferencesKey("backend_url")
    const val DEFAULT_URL = "http://10.0.2.2:8000"

    fun getBackendUrl(context: Context): Flow<String> =
        context.dataStore.data.map { prefs ->
            prefs[BACKEND_URL] ?: DEFAULT_URL
        }

    suspend fun setBackendUrl(context: Context, url: String) {
        context.dataStore.edit { prefs ->
            prefs[BACKEND_URL] = url
        }
    }
}