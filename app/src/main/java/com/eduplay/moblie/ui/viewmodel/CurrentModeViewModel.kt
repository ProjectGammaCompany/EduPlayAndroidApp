package com.eduplay.moblie.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.eduplay.moblie.services.OfflineModeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

@HiltViewModel
class CurrentModeViewModel @Inject constructor(private val offlineModeManager: OfflineModeManager): ViewModel() {
    val currentMode = mutableStateOf( offlineModeManager.getAppMode())
}