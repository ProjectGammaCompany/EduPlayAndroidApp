package com.eduplay.moblie.ui.viewmodel

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
    val password = mutableStateOf("")
    val canLogout = mutableStateOf(false)

    val unauthorised = mutableStateOf(false)

    fun logout(onErrorCallBack: ()->Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                canLogout.value = repository.logout()
            } catch (e: ConnectException) {
                onErrorCallBack()
            } catch (e: NotAuthorisedException) {
                unauthorised.value = true
            }
        }
    }

    fun fetchProfileInfo(onCompletion: () -> Unit, onErrorCallBack: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            var result: ProfileInfo
            try {
                result = repository.getProfile()
            } catch (e: ConnectException) {
                onErrorCallBack()
                result = ProfileInfo("", "", "")
            } catch (e: NotAuthorisedException) {
                unauthorised.value = true
                result = ProfileInfo("", "", "")
            }
            email.value = result.email
            password.value = result.password
        }.invokeOnCompletion { onCompletion() }
    }

    fun checkEmail(email: String): Boolean {
        if (email.isNotEmpty()) {
            return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            return false
        }
    }
}