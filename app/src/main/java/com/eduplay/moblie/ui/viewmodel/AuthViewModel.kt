package com.eduplay.moblie.ui.viewmodel

import androidx.lifecycle.ViewModel

class AuthViewModel(): ViewModel() {

@HiltViewModel
class AuthViewModel @Inject constructor(private val repository: EduRepository): ViewModel() {
    fun submitLoginForm() {
        //TODO()
    }

    fun submitRegisterForm() {
        //TODO()
    }
}