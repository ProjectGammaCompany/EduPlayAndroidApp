package com.eduplay.moblie.ui.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.exceptions.NotAuthorisedException
import com.eduplay.moblie.models.ProfileInfo
import com.eduplay.moblie.repository.EduRepository
import com.eduplay.moblie.services.OfflineModeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.net.ConnectException

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: EduRepository,
    private val offlineModeManager: OfflineModeManager
) : ViewModel() {

    val email = mutableStateOf("")
    val avatar = mutableStateOf("")
    val password = mutableStateOf("")
    val canLogout = mutableStateOf(false)

    val unauthorised = mutableStateOf(false)
    val noInternet = mutableStateOf(false)

    val isOffline: MutableState<Flow<OfflineModeManager.AppModes>> = mutableStateOf(flowOf(OfflineModeManager.AppModes.ONLINE))

    fun logout() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                canLogout.value = repository.logout()
            } catch (_: ConnectException) {
                noInternet.value = true
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
                canLogout.value = true
            } catch (_: Exception) {
                canLogout.value = true
            }
        }
    }

    init {
        isOffline.value = offlineModeManager.getAppMode()
        viewModelScope.launch(Dispatchers.IO) {
            var result: ProfileInfo = ProfileInfo("", "")
            try {
                result = repository.getProfile()
                email.value = result.username
                avatar.value = result.avatar
            } catch (_: ConnectException) {
                noInternet.value = true
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
            } catch (e: Exception) {
                Log.e("fetch profile", e.message ?: "", e)
            }

        }
    }

    fun checkEmail(email: String): Boolean {
        if (email.isNotEmpty()) {
            return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            return false
        }
    }

    fun toggleAppMode(isOffline: Boolean) {
        viewModelScope.launch {
            if (isOffline) {
                offlineModeManager.saveAppMode(OfflineModeManager.AppModes.OFFLINE)
            } else {
                offlineModeManager.saveAppMode(OfflineModeManager.AppModes.ONLINE)
            }
        }
    }
}