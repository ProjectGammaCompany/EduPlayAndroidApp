package com.eduplay.moblie.useCases

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class OfflineModeManagerDataStore(private val context: Context): OfflineModeManager {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "offline")
    private val appMode = intPreferencesKey("mode")
    private val currentUserId = stringPreferencesKey("currentUser")

    override fun getAppMode(): Flow<OfflineModeManager.AppModes> {
        return context.dataStore.data.map { preferences ->
            OfflineModeManager.AppModes.valueOf(preferences[appMode] ?: 0)
        }
    }

    override suspend fun saveAppMode(mode: OfflineModeManager.AppModes) {
        context.dataStore.edit { preferences ->
            preferences[appMode] = mode.modeNumber
        }
    }

    override fun getCurrentUserId(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[currentUserId] ?: ""
        }
    }

    override suspend fun removeCurrentUserId() {
        context.dataStore.edit { preferences ->
            preferences.remove(currentUserId)
        }
    }

    override suspend fun saveCurrentUserId(id: String) {
        context.dataStore.edit { preferences ->
            preferences[currentUserId] = id
        }
    }
}

interface OfflineModeManager {
    enum class AppModes(val modeNumber: Int) {
        ONLINE(0),
        OFFLINE(1);

        companion object {
            fun valueOf(value: Int): AppModes {
                for (mode in entries) {
                    if (mode.modeNumber == value) return mode
                }
                throw IllegalAccessException("cant cast $value to AppModes")
            }
        }
    }

    fun getAppMode(): Flow<AppModes>

    suspend fun saveAppMode(mode: AppModes)

    fun getCurrentUserId(): Flow<String>

    suspend fun removeCurrentUserId()

    suspend fun saveCurrentUserId(id: String)
}