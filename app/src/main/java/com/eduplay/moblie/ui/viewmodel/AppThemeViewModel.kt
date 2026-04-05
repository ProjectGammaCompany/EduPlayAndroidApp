package com.eduplay.moblie.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.eduplay.moblie.useCases.AppSettingsManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

@HiltViewModel
class AppThemeViewModel @Inject constructor(private val appSettingsManager: AppSettingsManager): ViewModel() {
    fun getTheme(): Flow<AppSettingsManager.Themes> {
        return appSettingsManager.getTheme()
    }
}