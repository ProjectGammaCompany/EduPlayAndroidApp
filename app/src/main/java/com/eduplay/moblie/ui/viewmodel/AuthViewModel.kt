package com.eduplay.moblie.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val passwordEqualsRepeatPassword = mutableStateOf(true)


    fun passwordHasErrors(password: String): Boolean {
        if (password.isNotEmpty()) {
            return !(password.length >= 5 //&&
//                    password.any { it.isDigit() } &&
//                    password.any { it.isLetter() } &&
//                    password.any { it.isLowerCase() } &&
//                    password.any { it.isUpperCase() }
        )
        } else {
            return false
        }
    }

    fun submitLoginForm(
        email: String,
        password: String
    ) {
        if (!emailHasErrors(email) && !passwordHasErrors(password)) {
            viewModelScope.launch {
                try {
                    authResult.value = repository.login(Auth(email, password))
                } catch (_: ConnectException) {
                    noInternetConnection.value = true
                } catch (e: Exception) {
                    Log.e("login error", e.message ?: "", e)
                }
            }
        } else if (emailHasErrors(email)){
            authResult.value = AuthResult.INCORRECT_EMAIL
        } else if (passwordHasErrors(email)){
            authResult.value = AuthResult.UNSAFE_PASSWORD
        }
    }

    fun submitRegisterForm(
        email: String,
        password: String,
        repeatPassword: String,
    ) {
        if (!emailHasErrors(email) && !passwordHasErrors(password)) {
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    authResult.value = repository
                        .register(RegistrationData(email, password, repeatPassword))
                } catch (e: ConnectException) {
                    Log.d("AUTHORISATION", e.message.toString())
                    noInternetConnection.value = true
                } catch (e: Exception) {
                    Log.e("AUTHORISATION", e.message ?: "", e)
                    noInternetConnection.value = true
                }
            }
        } else if (password != repeatPassword)  {
           passwordEqualsRepeatPassword.value = false
        } else if (emailHasErrors(email)) {
            authResult.value = AuthResult.INCORRECT_EMAIL
        } else if (passwordHasErrors(password)) {
            authResult.value = AuthResult.UNSAFE_PASSWORD
        }
    }

    enum class ForgotPasswordStatus{
        ENTER_EMAIL,
        ENTER_CODE,
        CHANGE_PASSWORD,
        NONE
    }


}