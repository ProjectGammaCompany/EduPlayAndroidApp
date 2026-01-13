package com.eduplay.moblie.ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.eduplay.moblie.repository.EduRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: EduRepository) : ViewModel() {

    val email = mutableStateOf("")
    val password = mutableStateOf("")
    private var canLogout = false

    fun logout(navController: NavController) {
        viewModelScope.launch(Dispatchers.IO) {
            canLogout = repository.logout()
        }.invokeOnCompletion {
            if (canLogout) {
                navController.clearBackStack<Any>()
                navController.navigate("auth_screen")
            }
        }
    }

    fun fetchProfileInfo(onCompletion: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getProfile()
            email.value = result.email
            password.value = result.password
        }.invokeOnCompletion { onCompletion() }
    }
}