package com.eduplay.moblie.ui.viewmodel

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.repository.EduRepository
import com.eduplay.moblie.repository.requestTypes.Auth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: EduRepository) : ViewModel() {
    var loginEmail by mutableStateOf("")
        private set

    val loginEmailHasErrors by derivedStateOf {
        emailHasErrors(loginEmail)
    }

    var registerEmail by mutableStateOf("")
        private set

    val registerEmailHasErrors by derivedStateOf {
        emailHasErrors(registerEmail)
    }

    var loginPassword by mutableStateOf("")
        private set

    val loginPasswordHasErrors by derivedStateOf {
        passwordHasErrors(loginPassword)
    }

    var registerPassword by mutableStateOf("")
        private set

    val registerPasswordHasErrors by derivedStateOf {
        passwordHasErrors(registerPassword)
    }

    fun emailHasErrors(email: String): Boolean {
        if (email.isNotEmpty()) {
            return !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            return false
        }
    }

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

    fun updateLoginEmail(input: String) {
        loginEmail = input
    }

    fun updateRegisterEmail(input: String) {
        registerEmail = input
    }

    fun updateLoginPassword(input: String) {
        loginPassword = input
    }

    fun updateRegisterPassword(input: String) {
        registerPassword = input
    }

    fun submitLoginForm(callBack: () -> Unit) {
        if (!loginEmailHasErrors && !loginPasswordHasErrors) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.login(Auth(loginEmail, loginPassword))
            }
        } else {
            callBack()
        }
    }

    fun submitRegisterForm(callBack: () -> Unit) {
        if (!registerEmailHasErrors && !registerPasswordHasErrors) {
            viewModelScope.launch(Dispatchers.IO) {
                repository.register(Auth(registerEmail, registerPassword))
            }.invokeOnCompletion { }
        } else {
            callBack()
        }
    }
}