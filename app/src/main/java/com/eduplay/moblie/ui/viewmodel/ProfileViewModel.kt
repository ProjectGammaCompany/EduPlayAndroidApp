package com.eduplay.moblie.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.exceptions.NotAuthorisedException
import com.eduplay.moblie.models.ProfileInfo
import com.eduplay.moblie.repository.EduRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ConnectException

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: EduRepository) : ViewModel() {

    val email = mutableStateOf("")
    val avatar = mutableStateOf("")
    val password = mutableStateOf("")
    val canLogout = mutableStateOf(false)
    val gotData = mutableStateOf(false)

    val unauthorised = mutableStateOf(false)

    fun logout(onErrorCallBack: ()->Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                canLogout.value = repository.logout()
            } catch (_: ConnectException) {
                onErrorCallBack()
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
                canLogout.value = true
            } catch (_:Exception) {
                canLogout.value = true
            }
        }
    }

    fun fetchProfileInfo(onErrorCallBack: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var result: ProfileInfo = ProfileInfo("", "")
            try {
                result = repository.getProfile()
                gotData.value = true
            } catch (_: ConnectException) {
                onErrorCallBack()
            } catch (_: NotAuthorisedException) {
                unauthorised.value = true
            } catch (e: Exception) {
                Log.e("fetch profile", e.message ?: "", e)
            }
            email.value = result.username
            avatar.value = result.avatar
        }
    }

    fun checkEmail(email: String): Boolean {
        if (email.isNotEmpty()) {
            return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            return false
        }
    }
}