package com.eduplay.moblie.useCases

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppSettingsManager(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")
    private val language = intPreferencesKey("language")
    private val screenMode = intPreferencesKey("screenMode")

    enum class LanguageModes(val modeNumber: Int) {
        RUSSIAN(0),
        ENGLISH(1);

        companion object {
            fun valueOf(value: Int): LanguageModes {
                for (mode in entries) {
                    if (mode.modeNumber == value) return mode
                }
                throw IllegalAccessException("cant cast $value to LanguageModes")
            }
        }
    }

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

    fun getLanguage(): Flow<LanguageModes> {
        return context.dataStore.data.map { preferences ->
            LanguageModes.valueOf(preferences[language] ?: 0)
        }
    }

    suspend fun saveLanguage(mode: LanguageModes) {
        context.dataStore.edit { preferences ->
            preferences[language] = mode.modeNumber
        }
    }

    fun getTheme(): Flow<Themes> {
        return context.dataStore.data.map { preferences ->
            Themes.valueOf(preferences[screenMode] ?: 0)
        }
    }

    suspend fun saveTheme(mode: Themes) {
        context.dataStore.edit { preferences ->
            preferences[screenMode] = mode.themeNumber
        }
    }
}