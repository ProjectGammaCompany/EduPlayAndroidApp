package com.eduplay.moblie.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eduplay.moblie.repository.EduRepository
import com.eduplay.moblie.repository.requestTypes.Auth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: EduRepository): ViewModel() {
    fun submitLoginForm(
        login: String,
        password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.login(Auth(login, password))
        }
    }

    fun submitRegisterForm(login: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.register(Auth(login, password))
        }.invokeOnCompletion {  }
    }
}