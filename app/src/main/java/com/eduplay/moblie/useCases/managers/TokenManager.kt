package com.eduplay.moblie.useCases.managers

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class TokenManagerDataStore(private val context: Context) : TokenManager {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

    companion object {
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_type")
    }

    override fun getAccessToken(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN_KEY] ?: ""
        }
    }

    override suspend fun saveAccessToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = token
        }
    }

    override fun getRefreshToken(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN_KEY] ?: ""
        }
    }

    override suspend fun saveRefreshToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN_KEY] = token
        }
    }
}

interface TokenManager {
    fun getAccessToken(): Flow<String>

    suspend fun saveAccessToken(token: String)

    fun getRefreshToken(): Flow<String>

    suspend fun saveRefreshToken(token: String)
}