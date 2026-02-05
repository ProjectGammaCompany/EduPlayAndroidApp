package com.eduplay.moblie.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.eduplay.moblie.models.AuthResult
import com.eduplay.moblie.repository.EduRepository
import com.eduplay.moblie.repository.requestTypes.Auth
import com.eduplay.moblie.repository.requestTypes.RegistrationData
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.ConnectException

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: EduRepository) : ViewModel() {

    fun emailHasErrors(email: String): Boolean {
        if (email.isNotEmpty()) {
            return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            return false
        }
    }

    val authResult = mutableStateOf<AuthResult?>(null)
    val noInternetConnection = mutableStateOf(false)


    fun passwordHasErrors(password: String): Boolean {
        if (password.isNotEmpty()) {
            return !(password.length >= 8 &&
                    password.any { it.isDigit() } &&
                    password.any { it.isLetter() } &&
                    password.any { it.isLowerCase() } &&
                    password.any { it.isUpperCase() })
        } else {
            return false
        }
    }

    fun submitLoginForm(
        email: String,
        password: String,
        callBack: () -> Unit
    ) {
        if (!emailHasErrors(email) && !passwordHasErrors(password)) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    authResult.value = repository.login(Auth(email, password))
                } catch (_: ConnectException) {
                    noInternetConnection.value = true
                } catch (e: Exception) {
                    Log.e("login error", e.message ?: "", e)
                    callBack()
                }
            }
        } else {
            callBack()
        }
    }

    fun submitRegisterForm(
        email: String,
        password: String,
        callBack: () -> Unit
    ) {
        if (!emailHasErrors(email) && !passwordHasErrors(password)) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    authResult.value = repository
                        .register(RegistrationData(email, password, password))
                } catch (e: ConnectException) {
                    Log.d("AUTHORISATION", e.message.toString())
                    noInternetConnection.value = true
                } catch (e: Exception) {
                    Log.e("AUTHORISATION", e.message ?: "", e)
                    callBack()
                }

            }
        } else {
            callBack()
        }
    }
}