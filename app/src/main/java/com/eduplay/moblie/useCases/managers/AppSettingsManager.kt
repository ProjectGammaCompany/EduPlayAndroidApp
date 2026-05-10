package com.eduplay.moblie.useCases.managers

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class AppSettingsManagerDataStore(private val context: Context) : AppSettingsManager {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")
    private val screenMode = intPreferencesKey("screenMode")

    override fun getTheme(): Flow<AppSettingsManager.Themes> {
        return context.dataStore.data.map { preferences ->
            AppSettingsManager.Themes.valueOf(preferences[screenMode] ?: 0)
        }
    }

    override suspend fun saveTheme(mode: AppSettingsManager.Themes) {
        context.dataStore.edit { preferences ->
            preferences[screenMode] = mode.themeNumber
        }
    }
}

interface AppSettingsManager {
    enum class Themes(val themeNumber: Int) {
        SYSTEM(0),
        LIGHT(1),
        DARK(2);

        companion object {
            fun valueOf(value: Int): Themes {
                for (mode in entries) {
                    if (mode.themeNumber == value) return mode
                }
                throw IllegalAccessException("cant cast $value to ScreenModes")
            }
        }
    }

    fun getTheme(): Flow<Themes>

    suspend fun saveTheme(mode: Themes)
}