package com.eduplay.moblie.useCases

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineModeManager(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "offline")
    private val appMode = intPreferencesKey("mode")
    private val currentUserId = stringPreferencesKey("currentUser")

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

    fun getAppMode(): Flow<AppModes> {
        return context.dataStore.data.map { preferences ->
            AppModes.valueOf(preferences[appMode] ?: 0)
        }
    }

    suspend fun saveAppMode(mode: AppModes) {
        context.dataStore.edit { preferences ->
            preferences[appMode] = mode.modeNumber
        }
    }

    fun getCurrentUserId(): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[currentUserId] ?: ""
        }
    }

    suspend fun removeCurrentUserId() {
        context.dataStore.edit { preferences ->
            preferences.remove(currentUserId)
        }
    }

    suspend fun saveCurrentUserId(id: String) {
        context.dataStore.edit { preferences ->
            preferences[currentUserId] = id
        }
    }
}