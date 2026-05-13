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
    val passwordsAreNotTheSame = mutableStateOf(false)


    fun passwordHasErrors(password: String): Boolean {
        if (password.isNotEmpty()) {
            return !(password.length >= 5 //&&
//                    password.any { it.isDigit() } &&
//                    password.any { it.isLetter() } &&
//                    password.any { it.isLowerCase() } &&
//                    password.any { it.isUpperCase() }
                    ) && authResult.value == AuthResult.WRONG_PASSWORD
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
        } else if (emailHasErrors(email)) {
            authResult.value = AuthResult.INCORRECT_EMAIL
        } else if (passwordHasErrors(email)) {
            authResult.value = AuthResult.UNSAFE_PASSWORD
        }
    }

    fun submitRegisterForm(
        email: String,
        password: String,
        repeatPassword: String,
    ) {
        passwordsAreNotTheSame.value = false
        if (!emailHasErrors(email) && !passwordHasErrors(password) && password == repeatPassword) {
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
        } else if (password != repeatPassword) {
            passwordsAreNotTheSame.value = true
        } else if (emailHasErrors(email)) {
            authResult.value = AuthResult.INCORRECT_EMAIL
        } else if (passwordHasErrors(password)) {
            authResult.value = AuthResult.UNSAFE_PASSWORD
        }
    }

    enum class ForgotPasswordStatus {
        ENTER_EMAIL,
        ENTER_CODE,
        CHANGE_PASSWORD,
        NONE
    }


    val currentForgotStatusFormState = mutableStateOf(ForgotPasswordStatus.NONE)
    val changePasswordEmailIsCorrect = mutableStateOf(true)
    val changePasswordCodeIsCorrect = mutableStateOf(true)
    val changePasswordsIdentical = mutableStateOf(true)
    val changePasswordsCorrect = mutableStateOf(true)
    private var changePasswordCode: String = ""


    fun requestCode(email: String) {
        viewModelScope.launch {
            try {
                val result = repository.requestCodeByEmail(email)
                if (result == AuthResult.SUCCESSES) {
                    currentForgotStatusFormState.value = ForgotPasswordStatus.ENTER_CODE
                } else {
                    changePasswordEmailIsCorrect.value = false
                }
            } catch (e: ConnectException) {
                Log.d("CHANGE_PASSWORD", e.message.toString())
                noInternetConnection.value = true
            } catch (e: Exception) {
                Log.e("CHANGE_PASSWORD", e.message ?: "", e)
                noInternetConnection.value = true
            }
        }
    }

    fun checkCode(code: String) {
        viewModelScope.launch {
            try {
                val result = repository.checkPasswordCodeValidity(code)
                if (result) {
                    currentForgotStatusFormState.value = ForgotPasswordStatus.CHANGE_PASSWORD
                    changePasswordCode = code
                } else {
                    changePasswordCodeIsCorrect.value = false
                }
            } catch (e: ConnectException) {
                Log.d("CHANGE_PASSWORD", e.message.toString())
                noInternetConnection.value = true
            } catch (e: Exception) {
                Log.e("CHANGE_PASSWORD", e.message ?: "", e)
                noInternetConnection.value = true
            }
        }
    }

    fun updatePassword(
        password: String,
        repeatPassword: String
    ) {
        if (password != repeatPassword) {
            changePasswordsIdentical.value = false
            return
        }
        if (passwordHasErrors(password)) {
            changePasswordsCorrect.value = false
        }

        viewModelScope.launch {
            try {
                val result = repository.updatePassword(password, repeatPassword, changePasswordCode)
                if (result) {
                    authResult.value = AuthResult.SUCCESSES
                    currentForgotStatusFormState.value = ForgotPasswordStatus.NONE
                } else {
                    changePasswordCodeIsCorrect.value = false
                }
            } catch (e: ConnectException) {
                Log.d("CHANGE_PASSWORD", e.message.toString())
                noInternetConnection.value = true
            } catch (e: Exception) {
                Log.e("CHANGE_PASSWORD", e.message ?: "", e)
                noInternetConnection.value = true
            }
        }
    }

    fun changePasswordGoBack() {
        currentForgotStatusFormState.value = when (currentForgotStatusFormState.value) {
            ForgotPasswordStatus.ENTER_EMAIL -> ForgotPasswordStatus.NONE
            ForgotPasswordStatus.ENTER_CODE -> ForgotPasswordStatus.ENTER_EMAIL
            ForgotPasswordStatus.CHANGE_PASSWORD -> ForgotPasswordStatus.ENTER_CODE
            ForgotPasswordStatus.NONE -> ForgotPasswordStatus.NONE
        }
    }

    fun setForgotStatusToFirstStep() {
        currentForgotStatusFormState.value = ForgotPasswordStatus.ENTER_EMAIL
    }


}