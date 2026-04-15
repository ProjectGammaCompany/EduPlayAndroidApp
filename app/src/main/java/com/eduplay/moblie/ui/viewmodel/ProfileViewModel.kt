package com.eduplay.moblie.ui.viewmodel

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.eduplay.moblie.exceptions.NotAuthorisedException
import com.eduplay.moblie.models.NotificationData
import com.eduplay.moblie.models.ProfileInfo
import com.eduplay.moblie.repository.EduRepository
import com.eduplay.moblie.useCases.AppSettingsManager
import com.eduplay.moblie.useCases.OfflineModeManager
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
    private val offlineModeManager: OfflineModeManager,
    private val appSettingsManager: AppSettingsManager
) : ViewModel() {

    val email = mutableStateOf("")
    val avatar = mutableStateOf("")
    val password = mutableStateOf("")
    val canLogout = mutableStateOf(false)
    val hasUnsentAnswers = mutableStateOf(false)

    val unauthorised = mutableStateOf(false)
    val noInternet = mutableStateOf(false)

    val isOffline: MutableState<Flow<OfflineModeManager.AppModes>> =
        mutableStateOf(flowOf(OfflineModeManager.AppModes.ONLINE))

    val theme = mutableStateOf(flowOf<AppSettingsManager.Themes>())
    val notifications = mutableStateListOf<NotificationData>()

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
        theme.value = appSettingsManager.getTheme()
        viewModelScope.launch() {
            var result: ProfileInfo = ProfileInfo("", "")
            try {
                result = repository.getProfile()
                email.value = result.username
                avatar.value = result.avatar
                notifications.addAll(repository.getLatestNotifications())
            } catch (_: ConnectException) {
                noInternet.value = true
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
            } catch (e: Exception) {
                Log.e("fetch profile", e.message ?: "", e)
            }
            hasUnsentAnswers.value = repository.containsUnsentAnswers()
        }
    }

    fun checkEmail(email: String): Boolean {
        if (email.isNotEmpty()) {
            return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            return false
        }
    }

    fun toggleAppMode(isOffline: Boolean, navController: NavController) {
        if (isOffline) {
            navController.navigate("updateEvents")
        } else {
            viewModelScope.launch {
                offlineModeManager.saveAppMode(OfflineModeManager.AppModes.ONLINE)
                repository.postAllAnswers()
            }
        }
    }

    fun changeTheme(theme: AppSettingsManager.Themes) {
        viewModelScope.launch {
            appSettingsManager.saveTheme(theme)
        }
    }

    fun updateEmail(newEmail: String) {
        if (checkEmail(newEmail)) return

        viewModelScope.launch {
            try {
                repository.updateUserName(newEmail)
                email.value = newEmail
            } catch (_: ConnectException) {
                noInternet.value = true
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
            } catch (e: Exception) {
                Log.e("update_profile", e.message ?: "", e)
            }
        }
    }

    fun updateAvatar(uri: Uri, contentResolver: ContentResolver, context: Context) {
        viewModelScope.launch {
            try {
                avatar.value = repository.updateAvatar(uri, contentResolver, context)
            } catch (_: ConnectException) {
                noInternet.value = true
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
            } catch (e: Exception) {
                Log.e("update_avatar", e.message ?: "", e)
            }
        }
    }

    fun sendAnswers() {
        viewModelScope.launch {
            try {
                repository.postAllAnswers()
                hasUnsentAnswers.value = repository.containsUnsentAnswers()
            } catch (_: ConnectException) {
                noInternet.value = true
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
            } catch (e: Exception) {
                Log.e("update_avatar", e.message ?: "", e)
            }
        }
    }
}